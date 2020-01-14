package org.duangsuse.bin.io

import org.duangsuse.bin.*

/** Functions that rotates integral byte order */
object Swap {
  fun swap(i: Int16) = rotateIntegral(Int16.SIZE_BYTES, 0xFF.toShort(), i16UShr, i16And, i16Shl, i16Or, i)
  fun swap(i: Int32) = rotateIntegral(Int32.SIZE_BYTES, 0xFF, Int32::ushr, i32And, Int32::shl, i32Or, i)
  fun swap(i: Int64) = rotateIntegral(Int64.SIZE_BYTES, 0xFFL, Int64::ushr, i64And, Int64::shl, i64Or, i)
}

internal val i16Shl: Shift<Int16> = { c -> toInt().shl(c).toShort() }
internal val i16UShr: Shift<Int16> = { c -> toInt().ushr(c).toShort() }

internal val i16And: Nat8Select<Int16> = { ss -> toInt().and(ss.toInt()) }
internal val i32And: Nat8Select<Int32> = { si -> and(si) }
internal val i64And: Nat8Select<Int64> = { sl -> and(sl).toInt() }

// leftmost byte selector
internal val i16Select: Nat8Select<Int16> = { ss -> toInt().and(ss.toInt()).ushr(Int16.SIZE_BITS-Byte.SIZE_BITS) }
internal val i32Select: Nat8Select<Int32> = { si -> and(si).ushr(Int32.SIZE_BITS-Byte.SIZE_BITS) }
internal val i64Select: Nat8Select<Int64> = { sl -> and(sl).ushr(Int64.SIZE_BITS-Byte.SIZE_BITS).toInt() }

internal val i16Or: Nat8Union<Int16> = { b -> toInt().or(b).toShort() }
internal val i32Or: Nat8Union<Int32> = { b -> or(b) }
internal val i64Or: Nat8Union<Int64> = { b -> or(b.toLong()) }
