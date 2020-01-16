package org.duangsuse.bin

import org.duangsuse.bin.type.Buffer
import org.duangsuse.bin.type.Cnt
import org.duangsuse.bin.type.IdxRange
import org.duangsuse.bin.type.Nat8
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

class ArrayViewNat8Reader(private val view: Int8Array): Nat8Reader, StackMarkReset<Cnt>(), Closeable {
  constructor(buffer: ArrayBuffer): this(Int8Array(buffer))
  override var position = 0
  override fun read(): Nat8 = view[position++].uExt()
  override fun readTo(buffer: Buffer, indices: IdxRange) {
    val subview = view.offset(position)
    for (index in indices) buffer[index] = subview[index]
    position += indices.size
  }
  override val estimate: Cnt get() = view.byteLength-position
  override fun skip(n: Cnt) { position += n }
  override fun close() {}
}

class ArrayViewNat8Writer(private val view: Int8Array): Nat8Writer, Flushable, Closeable {
  constructor(buffer: ArrayBuffer): this(Int8Array(buffer))
  private var position = 0
  override fun write(x: Nat8) { view[position++] = x.toByte() }
  override fun writeFrom(buffer: Buffer, indices: IdxRange) {
    view.set(buffer.sliceArray(indices).toTypedArray(), position)
    position += indices.size
  }
  override fun flush() {}
  override fun close() {}
}

private fun Int8Array.offset(pos: Cnt): Int8Array = subarray(pos, length)
