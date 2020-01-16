package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.BitFlags
import org.duangsuse.bin.pat.BitFlags32Creator
import org.duangsuse.bin.pat.Tuple2
import org.duangsuse.bin.pat.Pattern

/** Make stream aligned when read(pre)/write(post)
 *
 * Since alignments are just zero-padding, they are ignored in data storage
 *
 * __WARN__: Using this pattern __WILL BREAK__ [Pattern.writeSize] */
class Aligned<T>(private val alignment: Cnt, item: Pattern<T>): PrePost<T>(item) {
  override fun onReadPre(s: Reader) { s.makeAligned(alignment) }
  override fun onWritePost(s: Writer) { s.makeAligned(alignment) }
}

class BitFlags32Of<BIT_FL: BitFlags>(private val creator: BitFlags32Creator<BIT_FL>): Pattern.StaticallySized<BIT_FL> {
  override fun read(s: Reader): BIT_FL = creator(s.readInt32())
  override fun write(s: Writer, x: BIT_FL): Unit = s.writeInt32(x.toInt())
  override val size: Cnt = Int32.SIZE_BYTES
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
