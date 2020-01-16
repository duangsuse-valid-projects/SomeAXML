package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.Cnt
import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer
import org.duangsuse.bin.pat.Pattern

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

infix fun <T> Pattern.Sized<T>.magic(value: T) = magic(value) { error("Unknown magic <$it>") }
infix fun Pattern<Cnt>.padding(n: Int) = converted({ it + n }, { it - n })
