package org.duangsuse.bin.pat

import org.duangsuse.bin.io.Reader
import org.duangsuse.bin.byteReader
import org.duangsuse.bin.io.BufferWriter
import org.duangsuse.bin.type.Buffer
import java.io.InputStream

class JavaClassTests: AbstractJavaClassTests(Reader(testClassFile.byteReader())) {
  override val newFile: BufferWriter = BufferWriter(1000)
  override val fileBytes: Buffer = testClassFile.readBytes()
  override fun Buffer.reader(): Reader = Reader(byteReader())
}
internal val testClassFile: InputStream get() = JavaClassTests::class.java.getResourceAsStream("Tuple.class")
