package org.duangsuse.bin.pat

import org.duangsuse.bin.Idx
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