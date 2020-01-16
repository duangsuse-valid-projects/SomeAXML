package org.duangsuse.bin.pat.basic

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Pattern
import org.duangsuse.bin.pat.atom.*
import org.duangsuse.bin.type.Cnt

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
