package org.duangsuse.bin.io

import org.duangsuse.bin.Buffer
import org.duangsuse.bin.byteWriter
import java.io.ByteArrayOutputStream

class WriterTests private constructor(private val outs: ByteArrayOutputStream)
  : AbstractWriterTests(Writer(outs.byteWriter())) {
  constructor(): this(ByteArrayOutputStream(100))

  override val bytes: Buffer get() = outs.toByteArray()
  override val fileBytes: Buffer = testBinFile.readBytes()
}