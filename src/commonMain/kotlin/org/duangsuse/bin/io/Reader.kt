package org.duangsuse.bin.io

import org.duangsuse.bin.*
import org.duangsuse.bin.io.Swap.swap

class Reader(private val r: Nat8Reader): org.duangsuse.bin.Reader {
  override var byteOrder: ByteOrder = nativeOrder
  override val position: Cnt get() = mPosition
  private var mPosition = 0
  private val mPositionStack: MutableList<Cnt> by lazy(::mutableListOf)

  override fun readNat8(): Nat8 = r.read()
  override fun readInt8(): Int8 = readNat8().toByte().also { ++mPosition }
  override fun readInt16(): Int16 = inOrder(read(0.toShort(), i16Shl, i16Or, Int16.SIZE_BITS))
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
}