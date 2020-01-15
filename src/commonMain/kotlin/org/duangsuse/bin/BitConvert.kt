package org.duangsuse.bin

typealias Shift<I> = I.(Cnt) -> I
typealias Nat8Select<I> = I.(I) -> Nat8
typealias Nat8Union<I> = I.(Nat8) -> I

/**
 * integral [I] to sequence of [Byte]
 * ```
 * 1234 -> 1,2,3,4
 * ^pop left (shl(8*n)&select)
 * ```
 */
inline fun <I> nat8sFromInteger
  (n: Cnt, byte_left: I,
   crossinline shl: Shift<I>, crossinline select: Nat8Select<I>,
   i: I): Sequence<Nat8> = sequence {
  var accumulator = i
  for (_t in 1..n) {
    yield(accumulator.select(byte_left))
    accumulator = accumulator.shl(Byte.SIZE_BITS)
  }
}

/**
 * sequence of byte to integral
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
inline fun <I> nat8sToIntegral
  (zero: I,
   crossinline shl: Shift<I>, crossinline or: Nat8Union<I>,
   bytes: IntIterator): I {
  var accumulator = zero
  for (byte in bytes) {
    accumulator = accumulator.shl(Byte.SIZE_BITS).or(byte)
  }
  return accumulator
}

/**
 * rotate byte order (unsigned "ushr" is ok)
 * ```
 * 1 2 3 4 ->(rotate) 4 3 2 1
 * pop   ^(ushr&and)     push^(shl&or)
 * ```
 */
inline fun <I> rotateIntegral
  (n: Cnt, byte_right: I,
   crossinline ushr: Shift<I>, crossinline and: Nat8Select<I>,
   crossinline shl: Shift<I>, crossinline or: Nat8Union<I>,
   i: I): I {
  var source = i; var rotated = i
  for (_t in 1..n) {
    val rightmost = source.and(byte_right).also { source = source.ushr(Byte.SIZE_BITS) }
    rotated = rotated.shl(Byte.SIZE_BITS).or(rightmost)
  }
  return rotated
}
