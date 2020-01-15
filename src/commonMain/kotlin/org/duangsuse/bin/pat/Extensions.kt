package org.duangsuse.bin.pat

import org.duangsuse.bin.*

//// Atom & BitFlags

/** Perform unsigned extension, left-padding with zeros without moving its sign bit */
fun Int16.uExt(): Int32 = if (this < 0) {
  0x0001_0000 + this
} else this.toInt()
fun Int32.uExt(): Int64 = if (this < 0) {
  0x0000_0001_0000_0000L + this
} else this.toLong()

internal fun Int.bitUnion(other: Int): Int = or(other)
internal fun Int.bitSubtract(mask: Int): Int = and(mask.inv())

//// HelperPattern

fun <T> Pattern<T>.littleEndian() = EndianSwitch.LittleEndian(this)
fun <T> Pattern<T>.bigEndian() = EndianSwitch.BigEndian(this)

fun <T> Pattern<T>.aligned(n: Cnt) = Aligned(n, this)
infix fun <A, B> Pattern<A>.contextual(body: (A) -> Pattern<B>) = Contextual(this, body)

//// Pattern extensions

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

//// Tuple

fun <E> Tuple<E>.toList(): List<E> = indices.map(this::get)
