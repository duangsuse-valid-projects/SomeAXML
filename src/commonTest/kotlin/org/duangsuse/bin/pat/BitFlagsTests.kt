package org.duangsuse.bin.pat

import org.duangsuse.bin.bitflag.BitFlags
import org.duangsuse.bin.bitflag.BitFlags32
import kotlin.test.Test
import kotlin.test.assertEquals

class BitFlagsTests {
  class SomeFlag(i: Int): BitFlags32(i) {
    val one: Boolean by BitFlags.Index(2)
    val two: Boolean by BitFlags.Index(1)
    val three: Boolean by BitFlags.Index(0)
  }
  @Test fun getSet() {
    val flags = BitFlags32(0b1100101) //bit-length=7
    assertEquals(0b1100101.reversedBoolList(), (0..6).map(flags::get))
    flags[1] = true; flags[2] = false
    assertEquals(0b1100011.reversedBoolList(), (0..6).map(flags::get))
  }
  @Test fun indexDelegate() {
    val fs = SomeFlag(0b101)
    assertEquals(listOf(true, false, true), fs.run { listOf(one, two, three) })
  }
  private fun toBool(char: Char): Boolean = char != '0'
  private fun Int.reversedBoolList(): List<Boolean> = toString(2).map(::toBool).reversed()
}