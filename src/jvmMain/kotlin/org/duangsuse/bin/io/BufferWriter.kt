package org.duangsuse.bin.io

import org.duangsuse.bin.byteWriter
import org.duangsuse.bin.type.Cnt
import java.io.ByteArrayOutputStream

actual class BufferWriter private constructor(private val buffer: ByteArrayOutputStream): Writer(buffer.byteWriter()) {
  actual constructor(n: Cnt): this(ByteArrayOutputStream(n))
  actual fun byteArray(): ByteArray = buffer.toByteArray()
}