package org.duangsuse.bin.pat

import org.duangsuse.bin.Buffer
import org.duangsuse.bin.io.Writer
import org.duangsuse.bin.io.Reader
import org.duangsuse.bin.byteReader
import org.duangsuse.bin.byteWriter
import java.io.ByteArrayOutputStream
import java.io.InputStream

class JavaClassTests: AbstractJavaClassTests(Reader(testClassFile.byteReader())) {
  private val buffer = ByteArrayOutputStream(500)
  override val newFile: Writer = Writer(buffer.byteWriter())
  override val newFileReader: Reader get() = Reader(buffer.toByteArray().inputStream().byteReader()) // TODO buffer input stream
  override val fileBytes: Buffer = testClassFile.readBytes()
}
internal val testClassFile: InputStream get() = JavaClassTests::class.java.getResourceAsStream("Tuple.class")
