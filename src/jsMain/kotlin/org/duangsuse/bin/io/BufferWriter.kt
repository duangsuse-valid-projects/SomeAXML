package org.duangsuse.bin.io

import org.duangsuse.bin.ArrayViewNat8Writer
import org.duangsuse.bin.type.Cnt
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get

actual class BufferWriter private constructor(private val buffer: ArrayBuffer): Writer(ArrayViewNat8Writer(buffer)) {
  actual constructor(n: Cnt): this(ArrayBuffer(n))
  actual fun byteArray(): ByteArray = buffer.toByteArray()
  private fun ArrayBuffer.toByteArray(): ByteArray {
    val view = Int8Array(this)
    val ary = ByteArray(view.length)
    for (i in ary.indices) ary[i] = view[i]
    return ary
  }
}