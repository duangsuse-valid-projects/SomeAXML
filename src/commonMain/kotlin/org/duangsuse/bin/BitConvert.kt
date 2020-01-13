package org.duangsuse.bin

typealias Shift<I> = I.(Cnt) -> I
typealias ByteSelect<I> = I.(I) -> Byte
typealias ByteUnion<I> = I.(Byte) -> I

/**
 * integral [I] to sequence of [Byte]
 * ```
 * 1234 -> 1,2,3,4
 * ^pop left (shl(8*n)&and)
 * ```
 */
inline fun <I> integralToBytes
  (n: Cnt, byte_left: I,
   crossinline shl: Shift<I>, crossinline and: ByteSelect<I>,
   i: I): Sequence<Byte> = sequence {
  var accumulator = i
  for (_t in 1..n) {
    yield(accumulator.and(byte_left))
    accumulator = accumulator.shl(Byte.SIZE_BITS)
  }
}

/**
 * sequence of Byte to integral
 * ```
 * 1,2,3,4 -> 1234:
 * (shl&or...&or)^push right
 * 1,2,3,4 -> 0(zero: I)
 * 1,2,3,4 -> 0 shl(8) or 1
 * 2,3,4 -> 1 shl(8) or 2
 * 3,4 -> 12 shl(8) or 3
 * ...
 * ```
 */
inline fun <I> bytesToIntegral
  (zero: I,
   crossinline shl: Shift<I>, crossinline or: ByteUnion<I>,
   bytes: ByteIterator): I {
  var accumulator = zero
  for (byte in bytes) {
    accumulator = accumulator.shl(Byte.SIZE_BITS).or(byte)
  }
  return accumulator
}

/**
 * rotate byte order (unsigned ushr is ok)
 * ```
 * 1 2 3 4 ->(rotate) 4 3 2 1
 * pop   ^(ushr&and)     push^(shl&or)
 * ```
 */
inline fun <I> rotateIntegral
  (n: Cnt, byte_right: I, crossinline ushr: Shift<I>, crossinline and: ByteSelect<I>,
   crossinline shl: Shift<I>, crossinline or: ByteUnion<I>,
   i: I): I {
  var source = i; var rotated = i
  for (_t in 1..n) {
    val rightmost = source.and(byte_right).also { source = source.ushr(Byte.SIZE_BITS) }
    rotated = rotated.shl(Byte.SIZE_BITS).or(rightmost)
  }
  return rotated
}
