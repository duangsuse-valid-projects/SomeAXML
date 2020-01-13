package org.duangsuse.bin.io

import org.duangsuse.bin.*

class Writer(private val w: ByteWriter): org.duangsuse.bin.Writer {
  override var byteOrder: ByteOrder = LANGUAGE_ORDER
  override fun writeInt8(x: Int8) = w.write(x)

  override fun writeInt16(x: Int16) = write(Int16.SIZE_BYTES, (-0x7f00).toShort(), i16Shl, i16And, x)
  override fun writeInt32(x: Int32) = write(Int32.SIZE_BYTES, (-0x7f00_0000), Int32::shl, i32And, x)
  override fun writeInt64(x: Int64) = write(Int64.SIZE_BYTES, (-0x7f00_0000_0000_0000L), Int64::shl, i64And, x)

  override fun writeRat32(x: Rat32) = writeInt32(x.toBits())
  override fun writeRat64(x: Rat64) = writeInt64(x.toBits())

  private inline fun <I> write
    (n: Cnt, byte_left: I,
     crossinline shl: Shift<I>, crossinline and: ByteSelect<I>,
     i: I) {
    val bytes = integralToBytes(n, byte_left, shl, and, i)
    if (shouldSwap) {
      val buffer = bytes.toArray(n)
      buffer.reverse()
      for (b in buffer) writeInt8(b)
    } else {
      for (b in bytes) writeInt8(b)
    }
  }
  private val shouldSwap: Boolean get() = byteOrder != LANGUAGE_ORDER

  override fun close() { (w as Closeable).close() }
  override fun flush() { (w as Flushable).flush() }
}