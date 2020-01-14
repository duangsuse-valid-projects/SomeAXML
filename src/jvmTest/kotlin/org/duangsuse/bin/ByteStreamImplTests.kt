package org.duangsuse.bin

import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.test.assertEquals
import kotlin.test.assertFails

class InStreamByteReaderTests {
  private val testBin: InputStream get() = this::class.java.getResourceAsStream("test_binary")
  private fun makeS(): InStreamNat8Reader = testBin.byteReader()

  private val bytesBc = "bc".toByteArray()
  private fun readAbc(s: Nat8Reader) {
    assertEquals('a'.toInt(), s.read())
    assertArrayEquals(bytesBc, s.takeByte(2))
  }

  @Test fun read() {
    val s = makeS()
    readAbc(s)
    fun readPartial() {
      val buf = Buffer(3)
      s.readTo(buf, 0..1)
      assertArrayEquals("AB\u0000".toByteArray(), buf)
      assertEquals('C'.toInt(), s.read())
    }
    readPartial()
  }
  @Test fun markReset() {
    val s = makeS()
    for (_t in 1..2) s.positional { readAbc(s) }
  }
  @Test fun estimable() {
    val s = makeS()
    assertEquals("abcABC".toByteArray().size, s.estimate)
    s.skip(3)
    assertArrayEquals("ABC".toByteArray(), s.takeByte(3))
  }
  @Test fun close() {
    val s = makeS()
    s.close()
    assertFails { s.read() }
  }
}
class OutStreamByteWriterTests {
  private lateinit var byteStream: ByteArrayOutputStream
  private fun makeS(): OutStreamNat8Writer = ByteArrayOutputStream(6).also { byteStream = it }.byteWriter()
  @Test fun basicWrite() {
    val s = makeS()
    s.write('a'.toInt()); s.writeFrom("bc".toByteArray())
    assertArrayEquals("abc".toByteArray(), byteStream.toByteArray())
  }
  // close, flush, writeFrom omitted
}

internal fun <E> assertArrayEquals(expected: Array<E>, actual: Array<E>) = assertEquals(expected.toList(), actual.toList())
internal fun assertArrayEquals(expected: ByteArray, actual: ByteArray) = assertArrayEquals(expected.toTypedArray(), actual.toTypedArray())
