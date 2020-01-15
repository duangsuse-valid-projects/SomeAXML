package org.duangsuse.bin.pat

import org.duangsuse.bin.Buffer
import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer
import kotlin.test.Test

abstract class AbstractJavaClassTests(private val s: Reader) {
  @ExperimentalStdlibApi
  @Test fun readWrite() {
    val kFile = ClassFile.read(s)
    println(kFile.constants.joinToString("\n"))
    val compileFName = kFile.constants.map(Pair<*,*>::second).filterIsInstance<ConstantInfo.KstUTF8>()
      .find { it.value == "Tuple.kt" }!!
    compileFName.value = "元组.kt"
    ClassFile.write(newFile, kFile) // TODO byte buffer writer for JVM
    println(newFile)
    val newKFile = ClassFile.read(newFileReader)
    println(newKFile.constants.joinToString("\n"))
  }
  abstract val newFile: Writer
  abstract val newFileReader: Reader
  abstract val fileBytes: Buffer
}