package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Tuple2
import org.duangsuse.bin.pat.Pattern
import org.duangsuse.bin.pat.atom.*

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

inline fun <reified T> Pattern<T>.primitiveArray(sizer: Pattern<Cnt>, init: T): Pattern<Array<T>>
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

fun Pattern<Cnt>.sizedPrimitiveArray(init: Boolean) = bool8.primitiveArray(this, init)
fun Pattern<Cnt>.sizedPrimitiveArray(init: Byte) = int8.primitiveArray(this, init)
fun Pattern<Cnt>.sizedPrimitiveArray(init: Short) = int16.primitiveArray(this, init)
fun Pattern<Cnt>.sizedPrimitiveArray(init: Char) = char16.primitiveArray(this, init)
fun Pattern<Cnt>.sizedPrimitiveArray(init: Int) = int32.primitiveArray(this, init)
fun Pattern<Cnt>.sizedPrimitiveArray(init: Long) = int64.primitiveArray(this, init)
fun Pattern<Cnt>.sizedPrimitiveArray(init: Float) = rat32.primitiveArray(this, init)
fun Pattern<Cnt>.sizedPrimitiveArray(init: Double) = rat64.primitiveArray(this, init)

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
