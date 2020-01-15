package org.duangsuse.bin.io

import org.duangsuse.bin.Buffer
import org.duangsuse.bin.Writer
import org.duangsuse.bin.makeLittleEndian
import org.duangsuse.bin.writePadding
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class AbstractWriterTests(private val s: Writer) {
  private fun writePart() {
    s.writeInt8(0x7F)
    s.writePadding(3)
    s.writeInt32(0x77BBCCDD)
    s.writeInt64(0x7AFEBABEL)
  }
  @Test fun basicWrite() {
    writePart() // zeros are removed since Python's package struct writes no padding outputting big endian
    assertEquals(fileBytes.drop(16).take(16).removeZeros(), bytes.take(16).removeZeros())
  }
  @Test fun count() {
    writePart()
    assertEquals(16, s.count)
  }
  private fun List<Byte>.removeZeros() = filterNot { it == 0x00.toByte() }
  @Test fun byteOrder() {
    s.makeLittleEndian()
    writePart()
    assertEquals(fileBytes.take(16), bytes.take(16))
  }
  abstract val bytes: Buffer
  abstract val fileBytes: Buffer
}