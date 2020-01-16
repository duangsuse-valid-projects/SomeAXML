package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.BitFlags
import org.duangsuse.bin.pat.BitFlags32Creator
import org.duangsuse.bin.pat.Tuple2
import org.duangsuse.bin.pat.Pattern

/** Add argument/return listen for [Pattern] read/write */
abstract class PrePost<T>(private val item: Pattern<T>): Pattern.BySizedFully<T>(item) {
  override fun read(s: Reader): T {
    onReadPre(s)
    return item.read(s).also { onReadPost(s, it) }
  }
  override fun write(s: Writer, x: T) {
    onWritePre(s, x)
    item.write(s, x).also { onWritePost(s) }
  }

  open fun onReadPre(s: Reader) {}
  open fun onReadPost(s: Reader, result: T) {}
  open fun onWritePre(s: Writer, x: T) {}
  open fun onWritePost(s: Writer) {}
}

/** Read/write child [item] in byte order of [newEndian], then restore old order.
 *
 * NOTE: Thread safety __IS NOT__ guaranteed */
open class EndianSwitch<T>(item: Pattern<T>, private val newEndian: ByteOrder): PrePost<T>(item) {
  private lateinit var oldByteOrder: ByteOrder
  override fun onReadPre(s: Reader) {
    oldByteOrder = s.byteOrder
    s.byteOrder = newEndian
  }
  override fun onReadPost(s: Reader, result: T) {
    s.byteOrder = oldByteOrder
  }
  override fun onWritePre(s: Writer, x: T) {
    oldByteOrder = s.byteOrder
    s.byteOrder = newEndian
  }
  override fun onWritePost(s: Writer) {
    s.byteOrder = oldByteOrder
  }
  open class BigEndian<T>(item: Pattern<T>): EndianSwitch<T>(item, ByteOrder.BigEndian)
  open class LittleEndian<T>(item: Pattern<T>): EndianSwitch<T>(item, ByteOrder.LittleEndian)
}

/** Make stream aligned when read(pre)/write(post)
 *
 * Since alignments are just zero-padding, they are ignored in data storage
 *
 * __WARN__: Using this pattern __WILL BREAK__ [Pattern.writeSize] */
class Aligned<T>(private val alignment: Cnt, item: Pattern<T>): PrePost<T>(item) {
  override fun onReadPre(s: Reader) { s.makeAligned(alignment) }
  override fun onWritePost(s: Writer) { s.makeAligned(alignment) }
}

/** Some complex pattern that have sub-patterns depend on actual data stream */
open class Contextual<A, B>(private val head: Pattern<A>, private val body: (A) -> Pattern<B>): Pattern<Tuple2<A, B>> {
  override fun read(s: Reader): Tuple2<A, B> {
    val dataDep = head.read(s)
    return Tuple2(dataDep, body(dataDep).read(s))
  }
  override fun write(s: Writer, x: Tuple2<A, B>) {
    val (dataDep, state) = x
    head.write(s, dataDep)
    body(dataDep).write(s, state)
  }
  override fun writeSize(x: Tuple2<A, B>): Cnt {
    val (dataDep, state) = x
    return head.writeSize(dataDep) + body(dataDep).writeSize(state)
  }
}

class BitFlags32Of<BIT_FL: BitFlags>(private val creator: BitFlags32Creator<BIT_FL>): Pattern.StaticallySized<BIT_FL> {
  override fun read(s: Reader): BIT_FL = creator(s.readInt32())
  override fun write(s: Writer, x: BIT_FL): Unit = s.writeInt32(x.toInt())
  override val size: Cnt = Int32.SIZE_BYTES
}

/** Keep original array for __ALL BYTES REST__ in stream, may used for partial data extracting */
object Keep: Pattern<ByteArray> {
  override fun read(s: Reader): ByteArray = s.asNat8Reader().takeByte(s.estimate)
  override fun write(s: Writer, x: ByteArray): Unit = s.asNat8Writer().writeFrom(x)
  override fun writeSize(x: ByteArray): Cnt = x.size
}

/** [IllegalStateException] will be thrown when called */
object Unknown: Pattern<Nothing> {
  override fun read(s: Reader): Nothing = error("unknown data part @${s.position}")
  override fun write(s: Writer, x: Nothing) = impossible()
  override fun writeSize(x: Nothing): Cnt = impossible()
}
operator fun Pattern<Nothing>.unaryPlus() = object: Pattern<Any> {
  override fun read(s: Reader): Nothing = this@unaryPlus.read(s)
  override fun write(s: Writer, x: Any) = impossible()
  override fun writeSize(x: Any): Cnt = impossible()
}
