package org.duangsuse.bin.io

import org.duangsuse.bin.makeAligned
import kotlin.test.Test
import kotlin.test.assertEquals

//- offset -   0 1  2 3  4 5  6 7  8 9  A B  C D  E F  0123456789ABCDEF
//0x00000000  7f00 0000 ddcc bb77 beba feca 0000 0000  .......w........
//0x00000010  77ff eedd cafe babe ffff ffff ffff ffff  w...............
abstract class AbstractReaderTests(private val s: Reader) {
  @Test fun read() {
    assertEquals(0x7F, s.readInt8())
    s.makeAligned(Int.SIZE_BYTES)
    assertEquals(0x77BBCCDD, s.readInt32())
    s.makeAligned(Int.SIZE_BYTES)
    assertEquals(0xCAFEBABEL, s.readInt64())
  }
  @Test fun positionMarkReset() {}
  @Test fun byteOrder() {}
}