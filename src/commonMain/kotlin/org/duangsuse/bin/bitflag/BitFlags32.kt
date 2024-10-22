package org.duangsuse.bin.bitflag

import org.duangsuse.bin.bitSubtract
import org.duangsuse.bin.bitUnion
import org.duangsuse.bin.type.Idx
import org.duangsuse.bin.type.Int32

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