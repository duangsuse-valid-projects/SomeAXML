package org.duangsuse.bin

import org.duangsuse.bin.pat.Sized

val Sized.lastIndex: Idx get() = size.dec()
val Sized.indices: IdxRange get() = 0..lastIndex

val IdxRange.size: Cnt get() = (last-first).inc()
infix fun Idx.untilSize(size: Cnt): IdxRange = this..(this+size).dec()

inline fun <R> MarkReset.positional(op: Producer<R>): R
  = try { mark(); op() } finally { reset() }

inline fun <R> Closeable.use(op: Producer<R>): R
  = try { op() } finally { close() }

fun ByteReader.takeByte(n: Cnt): Buffer {
  val buffer = Buffer(n)
  readTo(buffer); return buffer
}
fun Reader.makeAligned(n: Cnt) {
  val chunkPosition = (position % n)
  if (chunkPosition != 0) skip(n - chunkPosition)
}

fun ByteOrdered.makeBigEndian() { byteOrder = ByteOrder.BigEndian }
fun ByteOrdered.makeLittleEndian() { byteOrder = ByteOrder.LittleEndian }
fun ByteOrdered.makeNativeEndian() { byteOrder = nativeOrder }

fun Sequence<Byte>.toArray(n: Cnt): ByteArray {
  val buffer = Buffer(n)
  for ((i, b) in this.withIndex()) buffer[i] = b
  return buffer
}
fun <E> MutableList<E>.removeLast(): E = removeAt(lastIndex)
