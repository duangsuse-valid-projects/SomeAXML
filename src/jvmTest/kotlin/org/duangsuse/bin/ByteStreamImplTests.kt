package org.duangsuse.bin

import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import org.duangsuse.bin.io.assertArrayEquals

private val testBin: InputStream get() = InStreamByteReaderTests::class.java.getResourceAsStream("test_binary")
class InStreamByteReaderTests {
  private fun readAbc(s: Nat8Reader) {
    assertEquals('a'.toInt(), s.read())
    assertArrayEquals("bc".toByteArray(), s.takeByte(2))
  }
  @Test fun read() {
    val s = testBin.byteReader()
    readAbc(s)
  }
  @Test fun partialReading() {
    val s = testBin.byteReader(); s.skip(3) //"abc"
    val buf = Buffer(3)
    s.readTo(buf, 0..1)
    assertArrayEquals("AB\u0000".toByteArray(), buf)
    assertEquals('C'.toInt(), s.read())
  }
  @Test fun markReset() {
    val s = testBin.byteReader()
    for (_t in 1..2) s.positional { readAbc(s) }
  }
  @Test fun estimable() {
    val s = testBin.byteReader()
    assertEquals("abcABC".toByteArray().size, s.estimate)
    s.skip(3)
    assertArrayEquals("ABC".toByteArray(), s.takeByte(3))
  }
  @Test fun close() {
    val s = testBin.byteReader()
    s.close()
    assertFails { s.read() }
  }
}

class OutStreamByteWriterTests {
  private lateinit var byteStream: ByteArrayOutputStream
  private fun makeStream(): OutStreamNat8Writer
    = ByteArrayOutputStream(6).also { byteStream = it }.byteWriter()
  @Test fun basicWrite() {
    val s = makeStream()
    s.write('a'.toInt()); s.writeFrom("bc".toByteArray())
    assertArrayEquals("abc".toByteArray(), byteStream.toByteArray())
  }
  // close, flush, writeFrom omitted
}
