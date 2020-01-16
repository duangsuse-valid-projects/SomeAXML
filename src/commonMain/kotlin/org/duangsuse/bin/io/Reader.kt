package org.duangsuse.bin.io

import org.duangsuse.bin.*
import org.duangsuse.bin.io.Swap.swap

class Reader(private val r: Nat8Reader): org.duangsuse.bin.Reader {
  override var byteOrder: ByteOrder = nativeOrder
  override val position get() = mPosition
  private var mPosition: Cnt = 0
  private val mPositionStack: MutableList<Cnt> by lazy(::mutableListOf)

  override fun readNat8(): Nat8 = r.read().also { ++mPosition }
  override fun readInt8(): Int8 = readNat8().toByte()
  override fun readInt16(): Int16 = inOrder(read(0.toShort(), i16Shl, i16Or, Int16.SIZE_BYTES))
  override fun readInt32(): Int32 = inOrder(read(0, Int32::shl, i32Or, Int32.SIZE_BYTES))
  override fun readInt64(): Int64 = inOrder(read(0L, Int64::shl, i64Or, Int64.SIZE_BYTES))
  override fun readRat32(): Rat32 = Rat32.fromBits(readInt32())
  override fun readRat64(): Rat64 = Rat64.fromBits(readInt64())

  private inline fun <I> read
    (zero: I,
     crossinline shl: Shift<I>, crossinline or: Nat8Union<I>,
     n: Cnt): I {
    val bytes = r.takeNat8(n); mPosition += n
    return nat8sToIntegral(zero, shl, or, bytes.iterator())
  }
  private val shouldSwap: Boolean get() = byteOrder != LANGUAGE_ORDER
  private fun inOrder(i: Int16) = if (shouldSwap) swap(i) else i
  private fun inOrder(i: Int32) = if (shouldSwap) swap(i) else i
  private fun inOrder(i: Int64) = if (shouldSwap) swap(i) else i

  override val estimate: Cnt get() = r.estimate
  override fun skip(n: Cnt) { r.skip(n); mPosition += n }

  override fun mark() { (r as MarkReset).mark(); mPositionStack.add(mPosition) }
  override fun reset() { (r as MarkReset).reset(); mPosition = mPositionStack.removeLast() }
  override fun close() { (r as Closeable).close() }

  private abstract inner class AsNat8ReaderDelegate: Nat8Reader by r
  private inner class AsNat8Reader: AsNat8ReaderDelegate() {
    override fun read(): Nat8 = super.read().also { ++mPosition }
    override fun readTo(buffer: Buffer, indices: IdxRange) = super.readTo(buffer, indices).also { mPosition += indices.size }
    override fun skip(n: Cnt) = super.skip(n).also { mPosition += n }
  }
  private val nat8Reader by lazy(::AsNat8Reader)
  override fun asNat8Reader(): Nat8Reader = nat8Reader
}