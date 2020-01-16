package org.duangsuse.bin.pat.atom

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Pattern

abstract class Int32ConvertedPattern<T>(source: Pattern<T>): ConvertedPattern<T, Int32>(source)

val int8Cnt: Pattern.Sized<Cnt> = int8.widen8()
val int16Cnt: Pattern.Sized<Cnt> = int16.widen16()

// NOTE: widen/narrow operations are suffixed with bit-width, since generics overloading conflict

fun Pattern.Sized<Int8>.widen8() = object: Int32ConvertedPattern<Int8>(this) {
  override fun from(src: Int8): Int32 = src.toInt()
  override fun to(x: Int32): Int8 = x.toByte()
}
fun Pattern.Sized<Int16>.widen16() = object: Int32ConvertedPattern<Int16>(this) {
  override fun from(src: Int16): Int32 = src.toInt()
  override fun to(x: Int32): Int16 = x.toShort()
}
fun Pattern.Sized<Int64>.narrow64() = object: Int32ConvertedPattern<Int64>(this) {
  override fun from(src: Int64): Int32 = src.toInt()
  override fun to(x: Int32): Int64 = x.toLong()
}
