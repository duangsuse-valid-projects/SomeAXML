package org.duangsuse.bin.io

import org.duangsuse.bin.*
import org.duangsuse.bin.io.Swap.swap

class Reader(private val r: ByteReader): org.duangsuse.bin.Reader {
  override var byteOrder: ByteOrder = nativeOrder
  override val position: Cnt get() = mPosition
  private var mPosition = 0
  private val mPositionStack: MutableList<Cnt> by lazy(::mutableListOf)

  override fun readInt8(): Int8 = r.read()
  override fun readInt16(): Int16 = inOrder(read(0.toShort(), i16Shl, i16Or, Int16.SIZE_BITS))
  override fun readInt32(): Int32 = inOrder(read(0, Int32::shl, i32Or, Int32.SIZE_BYTES))
  override fun readInt64(): Int64 = inOrder(read(0L, Int64::shl, i64Or, Int64.SIZE_BYTES))
  override fun readRat32(): Rat32 = Rat32.fromBits(readInt32())
  override fun readRat64(): Rat64 = Rat64.fromBits(readInt64())

  private inline fun <I> read
    (zero: I,
     crossinline shl: Shift<I>, crossinline or: ByteUnion<I>,
     n: Cnt): I {
    val bytes = r.takeByte(n); mPosition += n
    return bytesToIntegral(zero, shl, or, bytes.iterator())
  }
  private val shouldSwap: Boolean get() = byteOrder != nativeOrder
  private fun inOrder(i: Int16) = if (shouldSwap) swap(i) else i
  private fun inOrder(i: Int32) = if (shouldSwap) swap(i) else i
  private fun inOrder(i: Int64) = if (shouldSwap) swap(i) else i

  override val estimate: Cnt get() = r.estimate
  override fun skip(n: Cnt) { r.skip(n) }

  override fun mark() { (r as MarkReset).mark(); mPositionStack.add(mPosition) }
  override fun reset() { (r as MarkReset).reset(); mPosition = mPositionStack.removeLast() }
  override fun close() { (r as Closeable).close() }
}