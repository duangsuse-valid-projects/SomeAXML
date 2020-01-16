package org.duangsuse.bin.io

import org.duangsuse.bin.*
import org.duangsuse.bin.type.*

class Writer(private val w: Nat8Writer): org.duangsuse.bin.Writer {
  override var byteOrder: ByteOrder = LANGUAGE_ORDER
  override val count get() = mCount
  private var mCount: Cnt = 0

  override fun writeNat8(x: Nat8) = w.write(x).also { ++mCount }
  override fun writeInt8(x: Int8) = writeNat8(x.toInt())
  override fun writeInt16(x: Int16) = write(Int16.SIZE_BYTES, (-0x0100).toShort(), i16Shl, i16Select, x)
  override fun writeInt32(x: Int32) = write(Int32.SIZE_BYTES, (-0x0100_0000), Int32::shl, i32Select, x)
  override fun writeInt64(x: Int64) = write(Int64.SIZE_BYTES, (-0x0100_0000_0000_0000L), Int64::shl, i64Select, x)

  override fun writeRat32(x: Rat32) = writeInt32(x.toBits())
  override fun writeRat64(x: Rat64) = writeInt64(x.toBits())

  private inline fun <I> write
    (n: Cnt, byte_left: I,
     crossinline shl: Shift<I>, crossinline select: Nat8Select<I>,
     i: I) {
    val bytes = nat8sFromInteger(n, byte_left, shl, select, i)
    if (shouldSwap) {
      val revBuffer = bytes.asIterable().toArray(n); revBuffer.reverse()
      for (b in revBuffer) writeNat8(b)
    } else {
      for (b in bytes) writeNat8(b)
    }
  }
  private val shouldSwap: Boolean get() = byteOrder != LANGUAGE_ORDER

  override fun close() { (w as Closeable).close() }
  override fun flush() { (w as Flushable).flush() }

  private abstract inner class AsNat8WriterDelegate: Nat8Writer by w
  private inner class AsNat8Writer: AsNat8WriterDelegate() {
    override fun write(x: Nat8) = super.write(x).also { ++mCount }
    override fun writeFrom(buffer: Buffer, indices: IdxRange) = super.writeFrom(buffer, indices).also { mCount += indices.size }
  }
  private val nat8Writer by lazy(::AsNat8Writer)
  override fun asNat8Writer(): Nat8Writer = nat8Writer
}