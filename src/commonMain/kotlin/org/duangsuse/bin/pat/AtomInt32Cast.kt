package org.duangsuse.bin.pat

import org.duangsuse.bin.*

abstract class Int32Pattern: Pattern.Sized<Int32>

val int8Cnt: Pattern.Sized<Cnt> = int8.widen8()
val int16Cnt: Pattern.Sized<Cnt> = int16.widen16()

// NOTE: widen/narrow operations are suffixed with bit-width, since generics overloading conflict

fun Pattern.Sized<Int8>.widen8() = object: Int32Pattern() {
  override fun read(s: Reader): Int32 = this@widen8.read(s).toInt()
  override fun write(s: Writer, x: Int32) = this@widen8.write(s, x.toByte())
  override val size: Cnt? get() = this@widen8.size
}
fun Pattern.Sized<Int16>.widen16() = object: Int32Pattern() {
  override fun read(s: Reader): Int32 = this@widen16.read(s).toInt()
  override fun write(s: Writer, x: Int32) = this@widen16.write(s, x.toShort())
  override val size: Cnt? get() = this@widen16.size
}
fun Pattern.Sized<Int64>.narrow64() = object: Int32Pattern() {
  override fun read(s: Reader): Int32 = this@narrow64.read(s).toInt()
  override fun write(s: Writer, x: Int32) = this@narrow64.write(s, x.toLong())
  override val size: Cnt? get() = this@narrow64.size
}
