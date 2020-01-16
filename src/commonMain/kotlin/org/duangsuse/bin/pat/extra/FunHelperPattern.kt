package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Tuple2
import org.duangsuse.bin.pat.Pattern

// atomic helper patterns that should not inherited in like companion objects

fun <T, T1> Pattern<T>.converted(from: (T) -> T1, to: (T1) -> T) = object: Pattern.Sized<T1> {
  override fun read(s: Reader): T1 = this@converted.read(s).let(from)
  override fun write(s: Writer, x: T1) = this@converted.write(s, x.let(to))
  override val size: Cnt? get() = (this@converted as? Pattern.Sized)?.size
}
infix fun <T, T1> Pattern.Sized<T>.mapped(map: Map<T, T1>) = object: Pattern.Sized<T1> {
  private val revMap = map.reverseMap()
  override fun read(s: Reader): T1 = map.getValue(this@mapped.read(s))
  override fun write(s: Writer, x: T1): Unit = this@mapped.write(s, revMap.getValue(x))
  override val size: Cnt? get() = this@mapped.size
}
fun <T> Pattern<T>.offset(n: Cnt) = object: Pattern.Sized<Tuple2<Buffer, T>> {
  override fun read(s: Reader): Tuple2<Buffer, T> {
    val savedBuffer = s.asNat8Reader().takeByte(n)
    return Tuple2(savedBuffer, this@offset.read(s))
  }
  override fun write(s: Writer, x: Tuple2<Buffer, T>) {
    s.asNat8Writer().writeFrom(x.first)
    this@offset.write(s, x.second)
  }
  override val size: Cnt? get() = (this@offset as? Pattern.Sized)?.size?.plus(n)
}
fun <T> Pattern.Sized<T>.magic(value: T, onError: (T) -> Nothing) = object: Pattern.Sized<T> {
  override fun read(s: Reader): T = this@magic.read(s).also { if (it != value) onError(it) }
  override fun write(s: Writer, x: T) { this@magic.write(s, x) }
  override val size: Cnt? get() = this@magic.size
}

inline fun <reified T> Pattern<T>.primitiveArray(init: T, sizer: Pattern<Cnt>): Pattern<Array<T>>
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
}
