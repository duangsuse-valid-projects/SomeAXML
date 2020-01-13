package org.duangsuse.bin.io

import org.duangsuse.bin.*

object Swap {
  fun swap(i: Int16) = rotateIntegral(Int16.SIZE_BYTES, 0xFF.toShort(), i16UShr, i16And, i16Shl, i16Or, i)
  fun swap(i: Int32) = rotateIntegral(Int32.SIZE_BYTES, 0xFF, Int32::ushr, i32And, Int32::shl, i32Or, i)
  fun swap(i: Int64) = rotateIntegral(Int64.SIZE_BYTES, 0xFFL, Int64::ushr, i64And, Int64::shl, i64Or, i)
}

internal val i16Shl: Shift<Int16> = { c -> toInt().shl(c).toShort() }
internal val i16UShr: Shift<Int16> = { c -> toInt().ushr(c).toShort() }

internal val i16And: ByteSelect<Int16> = { ss -> toInt().and(ss.toInt()).toByte() }
internal val i32And: ByteSelect<Int32> = { si -> and(si).toByte() }
internal val i64And: ByteSelect<Int64> = { sl -> and(sl).toByte() }

internal val i16Or: ByteUnion<Int16> = { b -> toInt().or(b.toInt()).toShort() }
internal val i32Or: ByteUnion<Int32> = { b -> and(b.toInt()) }
internal val i64Or: ByteUnion<Int64> = { b -> and(b.toLong()) }
