package org.duangsuse.bin

//// Sized & Idx

inline val Sized.lastIndex: Idx get() = size.dec()
inline val Sized.indices: IdxRange get() = 0..lastIndex

inline val IdxRange.size: Cnt get() = (last-first).inc()
infix fun Idx.untilSize(size: Cnt): IdxRange = this..(this+size).dec()

//// MarkRest & Closeable

inline fun <R> MarkReset.positional(op: Producer<R>): R
  = try { mark(); op() } finally { reset() }

inline fun <R> Closeable.use(op: Producer<R>): R
  = try { op() } finally { close() }

//// Nat8Reader

fun Nat8Reader.takeByte(n: Cnt): Buffer {
  val buffer = Buffer(n)
  readTo(buffer); return buffer
}
fun Nat8Reader.takeNat8(n: Cnt): IntArray {
  val buffer = IntArray(n)
  var neg1Detect = 0
  for (i in 0.untilSize(n)) {
    val byte = read()
    buffer[i] = byte
    neg1Detect = neg1Detect or byte
  }
  if (neg1Detect < 0) throw StreamEnd()
  else return buffer
}

//// ReadControl & Writer & ByteOrder & ...

fun ReadControl.makeAligned(n: Cnt) {
  val chunkPosition = (position % n)
  if (chunkPosition != 0) skip(n - chunkPosition)
}
fun Writer.makeAligned(n: Cnt) {
  val chunkPosition = (count % n)
  if (chunkPosition != 0) writePadding(n - chunkPosition)
}

fun DataWriter.writePadding(n: Cnt, x: Nat8 = 0x00) {
  repeat(n) { writeNat8(x) }
}

fun ByteOrdered.makeBigEndian() { byteOrder = ByteOrder.BigEndian }
fun ByteOrdered.makeLittleEndian() { byteOrder = ByteOrder.LittleEndian }

//// Bitwise operation

/** Perform unsigned extension, left-padding with zeros without moving its sign bit */
fun Int16.uExt(): Int32 = if (this < 0) {
  0x0001_0000 + this
} else this.toInt()
fun Int32.uExt(): Int64 = if (this < 0) {
  0x0000_0001_0000_0000L + this
} else this.toLong()

internal fun Int.bitUnion(other: Int): Int = or(other)
internal fun Int.bitSubtract(mask: Int): Int = and(mask.inv())

//// Collections & Maps

fun Iterable<Nat8>.toArray(n: Cnt): IntArray {
  val buffer = IntArray(n)
  for ((i, b) in this.withIndex()) buffer[i] = b
  return buffer
}

fun <E> MutableList<E>.removeLast(): E = removeAt(lastIndex)
fun <K, V> Map<K, V>.reverseMap(): Map<V, K> = keys.map { k -> getValue(k) to k }.toMap()

internal inline fun <reified T> Collection<*>.takeIfAllIsInstance(): List<T>?
  = filterIsInstance<T>().takeIf { it.size == this.size }

internal fun <T, R: Any> Collection<T>.mapTakeIfAllNotNull(op: (T) -> R?): List<R>?
  = mapNotNull(op).takeIf { it.size == this.size }

//// Functions

fun impossible(): Nothing = error("impossible")
