package org.duangsuse.bin

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
  var neg1Detect = 0
  for (i in 0.untilSize(n)) {
    val byte = read()
    buffer[i] = byte
    neg1Detect = neg1Detect or byte
  }
  if (neg1Detect < 0) throw StreamEnd()
  else return buffer
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
fun Writer.makeAligned(n: Cnt) {
  val chunkPosition = (count % n)
  if (chunkPosition != 0) writePadding(n - chunkPosition)
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
fun <K, V> Map<K, V>.reverseMap(): Map<V, K> = keys.map { k -> this.getValue(k) to k }.toMap()
