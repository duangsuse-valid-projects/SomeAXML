package org.duangsuse.bin.io

import org.duangsuse.bin.makeAligned
import org.duangsuse.bin.makeBigEndian
import org.duangsuse.bin.positional
import kotlin.test.Test
import kotlin.test.assertEquals

//- offset -   0 1  2 3  4 5  6 7  8 9  A B  C D  E F  0123456789ABCDEF
//0x00000000  7f00 0000 ddcc bb77 beba feca 0000 0000  .......w........
//0x00000010  7f77 bbcc ddca feba beff ffff ffff ffff  .w..............
abstract class AbstractReaderTests(private val s: Reader) {
  private fun readPart(aligned: Boolean = true) {
    assertEquals(0x7F, s.readInt8())
    if (aligned) s.makeAligned(Int.SIZE_BYTES)
    assertEquals(0x77BBCCDD, s.readInt32())
    if (aligned) s.makeAligned(Int.SIZE_BYTES)
    assertEquals(0xCAFEBABEL, s.readInt64())
  }
  @Test fun read() {
    readPart()
  }
  @Test fun positionMarkReset() {
    assertEquals(0, s.position)
    for (_t in 1..2) s.positional {
      assertEquals(0x7F, s.readInt8())
      assertEquals(0x00, s.readNat8())
      assertEquals(2, s.position)
    }
  }
  @Test fun byteOrder() {
    s.skip(0x10)
    s.makeBigEndian()
    readPart(false)
  }
}