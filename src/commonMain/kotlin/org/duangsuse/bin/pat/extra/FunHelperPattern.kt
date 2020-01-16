package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Tuple2
import org.duangsuse.bin.pat.Pattern

// atomic helper patterns that should not inherited in like companion objects

fun <T, T1> Pattern<T>.converted(from: (T) -> T1, to: (T1) -> T) = object: Pattern.BySized<T1>(this) {
  override fun read(s: Reader): T1 = this@converted.read(s).let(from)
  override fun write(s: Writer, x: T1) = this@converted.write(s, x.let(to))
  override fun writeSize(x: T1): Cnt = this@converted.writeSize(to(x))
}
infix fun <T, T1> Pattern<T>.mapped(map: Map<T, T1>) = object: Pattern.BySized<T1>(this) {
  private val revMap = map.reverseMap()
  override fun read(s: Reader): T1 = map.getValue(this@mapped.read(s))
  override fun write(s: Writer, x: T1): Unit = this@mapped.write(s, revMap.getValue(x))
  override fun writeSize(x: T1): Cnt = this@mapped.writeSize(revMap.getValue(x))
}
fun <T> Pattern<T>.magic(value: T, onError: (T) -> Nothing) = object: Pattern.BySizedFully<T>(this) {
  override fun read(s: Reader): T = this@magic.read(s).also { if (it != value) onError(it) }
  override fun write(s: Writer, x: T) { this@magic.write(s, x) }
}
fun <T> Pattern<T>.offset(n: Cnt) = object: Pattern.BySized<Tuple2<Buffer, T>>(this) {
  override fun read(s: Reader): Tuple2<Buffer, T> {
    val savedBuffer = s.asNat8Reader().takeByte(n)
    return Tuple2(savedBuffer, this@offset.read(s))
  }
  override fun write(s: Writer, x: Tuple2<Buffer, T>) {
    s.asNat8Writer().writeFrom(x.first)
    this@offset.write(s, x.second)
  }
  override fun writeSize(x: Tuple2<Buffer, T>): Cnt = x.first.size + this@offset.writeSize(x.second)
  override val size: Cnt? get() = super.size?.plus(n)
}

/**
 * NOTE: Kotlin 1.3.41 __WILL NOT__ inline [defaultValue] call in [init] __causing intrinsics exception in runtime, that's a bug__
 *
 * Easiest workaround: __specify [init] directly__
 */
inline fun <reified T> Pattern<T>.primitiveArray(sizer: Pattern<Cnt>, init: T = defaultValue()): Pattern<Array<T>>
  = object: Pattern<Array<T>> {
  override fun read(s: Reader): Array<T> {
    val size = sizer.read(s)
    val ary: Array<T> = Array(size) {init}
    for (i in ary.indices) ary[i] = this@primitiveArray.read(s)
    return ary
  }
  override fun write(s: Writer, x: Array<T>) {
    sizer.write(s, x.size)
    for (item in x) this@primitiveArray.write(s, item)
  }
  override fun writeSize(x: Array<T>): Cnt = sizer.writeSize(x.size) + x.map(this@primitiveArray::writeSize).sum()
}
fun Pattern<Cnt>.sizedByteArray() = object: Pattern<ByteArray> {
  override fun read(s: Reader): ByteArray {
    val n = this@sizedByteArray.read(s)
    return s.asNat8Reader().takeByte(n)
  }
  override fun write(s: Writer, x: ByteArray) {
    this@sizedByteArray.write(s, x.size)
    s.asNat8Writer().writeFrom(x)
  }
  override fun writeSize(x: ByteArray): Cnt = x.size
}
