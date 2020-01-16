package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.Cnt
import org.duangsuse.bin.Idx
import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer
import org.duangsuse.bin.pat.BitFlags
import org.duangsuse.bin.pat.BitFlags32Creator
import org.duangsuse.bin.pat.Pattern
import org.duangsuse.bin.pat.atom.ConvertedPattern
import org.duangsuse.bin.pat.basic.Cond
import org.duangsuse.bin.pat.basic.Repeat

//// basic patterns

fun <T, T1> Pattern<T>.converted(from: (T) -> T1, to: (T1) -> T) = object: ConvertedPattern<T, T1>(this) {
  override fun from(src: T): T1 = from(src)
  override fun to(x: T1): T = to(x)
}

infix fun <T: Any> Pattern<Cnt>.sizedRepeat(item: Pattern<T>) = Repeat(this, item)
fun <T> Pattern<Idx>.cond(vararg conditions: Pattern<T>) = Cond(this, *conditions)

//// HelperPattern

fun <T> Pattern<T>.littleEndian() = EndianSwitch.LittleEndian(this)
fun <T> Pattern<T>.bigEndian() = EndianSwitch.BigEndian(this)

fun <T> Pattern<T>.aligned(n: Cnt) = Aligned(n, this)
fun <BIT_FL: BitFlags> BitFlags32Creator<BIT_FL>.bitFlags32() = BitFlags32Of(this)
infix fun <A, B> Pattern<A>.contextual(body: (A) -> Pattern<B>) = Contextual(this, body)

//// Pattern extensions

/** Pseudo pattern, specify known constants not related to actual data stream
 *
 * This pattern __WILL NOT__ modify actual data stream */
fun <T> T.statically() = object: Pattern.Sized<T> {
  override fun read(s: Reader): T = this@statically
  override fun write(s: Writer, x: T) {}
  override fun writeSize(x: T): Cnt = 0
  override val size: Cnt = 0
}

infix fun <T> Pattern<T>.magic(value: T) = magic(value) { error("Unknown magic <$it>") }
infix fun Pattern<Int>.padding(n: Int) = converted({ it + n }, { it - n })
