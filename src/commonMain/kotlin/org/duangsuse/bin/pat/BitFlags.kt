package org.duangsuse.bin.pat

import org.duangsuse.bin.Idx
import org.duangsuse.bin.Int32
import kotlin.reflect.KProperty

/** Bit flag index are counted from rightmost bit (BigEndian) */
interface BitFlags {
  operator fun get(index: Idx): Boolean
  operator fun set(index: Idx, value: Boolean)
  fun toInt(): Int
  class Index(private val index: Idx) {
    operator fun getValue(self: BitFlags, _p: KProperty<*>): Boolean = self[index]
    operator fun setValue(self: BitFlags, _p: KProperty<*>, value: Boolean) { self[index] = value }
  }
}

open class BitFlags32(private var i: Int32): BitFlags {
  override fun get(index: Idx): Boolean = indexBit(index).and(i) != 0
  override fun set(index: Idx, value: Boolean) {
    val selector = indexBit(index)
    i = if (value) i.bitUnion(selector)
      else i.bitSubtract(selector)
  }
  override fun toInt(): Int32 = i
  private fun indexBit(index: Idx): Int = 0b1.shl(index)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    return if (other == null || other !is BitFlags32) false
    else i == other.i
  }
  override fun hashCode(): Int = i
  override fun toString(): String = "BitFlags32(${i.toString(2)})"
}

internal fun Int.bitUnion(other: Int): Int = or(other)
internal fun Int.bitSubtract(mask: Int): Int = and(mask.inv())
