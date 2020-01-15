package org.duangsuse.bin.pat

import org.duangsuse.bin.*

/** Add argument/return listen for [Pattern] read/write */
abstract class PrePost<T>(private val item: Pattern<T>): Pattern.Sized<T> {
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
  override val size: Cnt? get() = (item as? Pattern.Sized<T>)?.size
}

/** Read/write child [item] in byte order of [newEndian], then restore old order.
 *
 * Thread safety is not guaranteed */
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

fun <T> Pattern<T>.littleEndian() = EndianSwitch.LittleEndian(this)
fun <T> Pattern<T>.bigEndian() = EndianSwitch.BigEndian(this)

/** Make stream aligned when read(pre)/write(post) */
class Aligned<T>(private val alignment: Cnt, item: Pattern<T>): PrePost<T>(item) {
  override fun onReadPre(s: Reader) { s.makeAligned(alignment) }
  override fun onWritePost(s: Writer) { s.makeAligned(alignment) }
}

fun <T> Pattern<T>.aligned(n: Cnt) = Aligned(n, this)

/** Some complex pattern that have sub-patterns depend on actual data stream */
open class Contextual<A, B>(private val head: Pattern<A>, private val body: (A) -> Pattern<B>): Pattern<Pair<A, B>> {
  override fun read(s: Reader): Pair<A, B> {
    val dataDep = head.read(s)
    return Pair(dataDep, body(dataDep).read(s))
  }
  override fun write(s: Writer, x: Pair<A, B>) {
    val (dataDep, state) = x
    head.write(s, dataDep)
    body(dataDep).write(s, state)
  }
}

infix fun <A, B> Pattern<A>.contextual(body: (A) -> Pattern<B>) = Contextual(this, body)

// atomic helper patterns that should not inherited in like companion objects
inline fun <reified T> Pattern<T>.array(init: T, sizer: Pattern<Cnt>): Pattern<Array<T>>
  = object: Pattern<Array<T>> {
  override fun read(s: Reader): Array<T> {
    val size = sizer.read(s)
    val ary: Array<T> = Array(size) {init}
    for (i in ary.indices) ary[i] = this@array.read(s)
    return ary
  }
  override fun write(s: Writer, x: Array<T>) {
    sizer.write(s, x.size)
    for (item in x) this@array.write(s, item)
  }
}

fun <BIT_FL: BitFlags> bitFlags(creator: (Int32) -> BIT_FL) = object: Pattern.Sized<BIT_FL> {
  override fun read(s: Reader): BIT_FL = creator(s.readInt32())
  override fun write(s: Writer, x: BIT_FL): Unit = s.writeInt32(x.toInt())
  override val size: Cnt = Int32.SIZE_BYTES
}
fun <T> mapped(item: Pattern.Sized<T>, map: Map<T, T>) = object: Pattern.Sized<T> {
  private val revMap = map.reverseMap()
  override fun read(s: Reader): T = map.getValue(item.read(s))
  override fun write(s: Writer, x: T): Unit = item.write(s, revMap.getValue(x))
  override val size: Cnt? get() = item.size
}

/** Pseudo pattern, specify known constants not related to actual data stream
 *
 * This pattern __WILL NOT__ modify actual data stream */
fun <T> T.statically() = object: Pattern.Sized<T> {
  override fun read(s: Reader): T = this@statically
  override fun write(s: Writer, x: T) {}
  override val size: Cnt = 0
}

fun <T> Pattern.Sized<T>.magic(value: T, onError: (T) -> Nothing) = object: Pattern.Sized<T> {
  override fun read(s: Reader): T = this@magic.read(s).also { if (it != value) onError(it) }
  override fun write(s: Writer, x: T) { this@magic.write(s, x) }
  override val size: Cnt? get() = this@magic.size
}
infix fun <T> Pattern.Sized<T>.magic(value: T) = magic(value) { error("Unknown magic <$it>") }

fun <T> offset(n: Cnt, item: Pattern<T>) = object: Pattern.Sized<Pair<Buffer, T>> {
  override fun read(s: Reader): Pair<Buffer, T> {
    val savedBuffer = s.asNat8Reader().takeByte(n)
    return Pair(savedBuffer, item.read(s))
  }
  override fun write(s: Writer, x: Pair<Buffer, T>) {
    s.asNat8Writer().writeFrom(x.first)
    item.write(s, x.second)
  }
  override val size: Cnt? get() = (item as? Pattern.Sized)?.size?.plus(n)
}
