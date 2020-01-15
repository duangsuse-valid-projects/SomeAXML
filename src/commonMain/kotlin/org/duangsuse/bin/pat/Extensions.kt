package org.duangsuse.bin.pat

import org.duangsuse.bin.*

/** Perform unsigned extension, left-padding with zeros without moving its sign bit */
fun Int16.uExt(): Int32 = if (this < 0) {
  0x0001_0000 + this
} else this.toInt()
fun Int32.uExt(): Int64 = if (this < 0) {
  0x0000_0001_0000_0000L + this
} else this.toLong()

fun <E> Tuple<E>.toList(): List<E> = indices.map(this::get)

internal fun Int.bitUnion(other: Int): Int = or(other)
internal fun Int.bitSubtract(mask: Int): Int = and(mask.inv())
