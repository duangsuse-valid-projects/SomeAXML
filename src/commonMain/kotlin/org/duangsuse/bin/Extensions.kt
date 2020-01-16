package org.duangsuse.bin

import org.duangsuse.bin.type.*

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

fun Nat8Reader.readTo(buffer: Buffer) { readTo(buffer, buffer.indices) }
fun Nat8Writer.writeFrom(buffer: Buffer) { writeFrom(buffer, buffer.indices) }

fun Nat8Reader.takeByte(n: Cnt): Buffer {
  val buffer = Buffer(n)
  readTo(buffer); return buffer
}
fun Nat8Reader.takeNat8(n: Cnt): Nat8Buffer {
  val buffer = Nat8Buffer(n)
  var neg1Detect = 0
  for (i in 0 until n) {
    buffer[i] = read().also { neg1Detect = neg1Detect or it }
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
  if (chunkPosition != 0) asNat8Writer().writePadding(n - chunkPosition)
}

fun Nat8Writer.writePadding(n: Cnt, x: Byte = 0x00) {
  val padding = Buffer(n) {x}
  writeFrom(padding)
}

fun ByteOrdered.makeBigEndian() { byteOrder = ByteOrder.BigEndian }
fun ByteOrdered.makeLittleEndian() { byteOrder = ByteOrder.LittleEndian }

//// Bitwise operation

/** Perform unsigned extension, left-padding with zeros without moving its sign bit */
fun Int8.uExt(): Int32 = if (this < 0) {
  0x0000_0100 + this
} else this.toInt()
fun Int16.uExt(): Int32 = if (this < 0) {
  0x0001_0000 + this
} else this.toInt()
fun Int32.uExt(): Int64 = if (this < 0) {
  0x0000_0001_0000_0000L + this
} else this.toLong()

internal fun Int.bitUnion(other: Int): Int = or(other)
internal fun Int.bitSubtract(mask: Int): Int = and(mask.inv())

//// Collections & Maps

fun Iterable<Nat8>.toArray(n: Cnt): Nat8Buffer {
  val buffer = Nat8Buffer(n)
  for ((i, b) in this.withIndex()) buffer[i] = b
  return buffer
}

internal fun <E> MutableList<E>.removeLast(): E = removeAt(lastIndex)
internal fun <K, V> Map<K, V>.reverseMap(): Map<V, K> = keys.map { k -> getValue(k) to k }.toMap()

internal inline fun <reified T> Collection<*>.takeIfAllIsInstance(): List<T>?
  = filterIsInstance<T>().takeIf { it.size == this.size }

internal fun <T, R: Any> Collection<T>.mapTakeIfAllNotNull(op: (T) -> R?): List<R>?
  = mapNotNull(op).takeIf { it.size == this.size }

abstract class StackMarkReset<T>: MarkReset {
  protected abstract var position: T
  private val stack: MutableList<T> by lazy(::mutableListOf)
  override fun mark() { stack.add(position) }
  override fun reset() { position = stack.removeLast() }
}

//// Functions

internal fun impossible(): Nothing = error("impossible")
