package org.duangsuse.bin

import org.duangsuse.bin.pat.Sized

inline val Sized.lastIndex: Idx get() = size.dec()
inline val Sized.indices: IdxRange get() = 0..lastIndex

inline val IdxRange.size: Cnt get() = (last-first).inc()
infix fun Idx.untilSize(size: Cnt): IdxRange = this..(this+size).dec()

inline fun <R> MarkReset.positional(op: Producer<R>): R
  = try { mark(); op() } finally { reset() }

inline fun <R> Closeable.use(op: Producer<R>): R
  = try { op() } finally { close() }

fun Nat8Reader.takeByte(n: Cnt): Buffer {
  val buffer = Buffer(n)
  readTo(buffer); return buffer
}
fun Nat8Reader.takeNat8(n: Cnt): IntArray {
  val buffer = IntArray(n)
  for (i in (0 untilSize n)) {
    val byte = read()
    if (byte == (-1)) throw StreamEnd()
    else buffer[i] = byte
  }
  return buffer
}
fun ReadControl.makeAligned(n: Cnt) {
  val chunkPosition = (position % n)
  if (chunkPosition != 0) skip(n - chunkPosition)
}
fun DataWriter.writePadding(n: Cnt, x: Nat8 = 0x00) {
  repeat(n) { writeNat8(x) }
}
fun Nat8Writer.writePadding(n: Cnt, x: Nat8 = 0x00) {
  repeat(n) { write(x) }
}

fun ByteOrdered.makeBigEndian() { byteOrder = ByteOrder.BigEndian }
fun ByteOrdered.makeLittleEndian() { byteOrder = ByteOrder.LittleEndian }
fun ByteOrdered.makeNativeEndian() { byteOrder = nativeOrder }

fun Sequence<Nat8>.toArray(n: Cnt): IntArray {
  val buffer = IntArray(n)
  for ((i, b) in this.withIndex()) buffer[i] = b
  return buffer
}
fun ByteIterator.widenIterator(): IntIterator = object: IntIterator() {
  override fun hasNext(): Boolean = this@widenIterator.hasNext()
  override fun nextInt(): Int = this@widenIterator.nextByte().toInt()
}
fun <E> MutableList<E>.removeLast(): E = removeAt(lastIndex)
