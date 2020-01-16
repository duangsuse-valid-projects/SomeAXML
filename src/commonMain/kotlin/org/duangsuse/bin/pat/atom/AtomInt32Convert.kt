package org.duangsuse.bin.pat.atom

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Pattern

abstract class Int32ConvertPattern<T>(private val source: Pattern<T>): Pattern.BySized<Int32>(source) {
  override fun read(s: Reader): Int32 = toInt(source.read(s))
  override fun write(s: Writer, x: Int32) = source.write(s, fromInt(x))
  protected abstract fun toInt(src: T): Int32
  protected abstract fun fromInt(x: Int32): T
}

val int8Cnt: Pattern.Sized<Cnt> = int8.widen8()
val int16Cnt: Pattern.Sized<Cnt> = int16.widen16()

// NOTE: widen/narrow operations are suffixed with bit-width, since generics overloading conflict

fun Pattern.Sized<Int8>.widen8() = object: Int32ConvertPattern<Int8>(this) {
  override fun toInt(src: Int8): Int32 = src.toInt()
  override fun fromInt(x: Int32): Int8 = x.toByte()
}
fun Pattern.Sized<Int16>.widen16() = object: Int32ConvertPattern<Int16>(this) {
  override fun toInt(src: Int16): Int32 = src.toInt()
  override fun fromInt(x: Int32): Int16 = x.toShort()
}
fun Pattern.Sized<Int64>.narrow64() = object: Int32ConvertPattern<Int64>(this) {
  override fun toInt(src: Int64): Int32 = src.toInt()
  override fun fromInt(x: Int32): Int64 = x.toLong()
}
