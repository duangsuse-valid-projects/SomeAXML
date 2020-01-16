package org.duangsuse.bin.pat

import org.duangsuse.bin.type.Buffer
import org.duangsuse.bin.io.Reader
import org.duangsuse.bin.io.BufferWriter
import org.duangsuse.bin.pat.basic.Cond
import kotlin.test.Test

abstract class AbstractJavaClassTests(private val s: Reader) {
  @ExperimentalStdlibApi
  @Test fun readWrite() {
    val kFile = ClassFile.read(s)
    println(kFile.constants.joinToString("\n"))
    val compileFName = kFile.constants.map(Cond.TagItem<*,*>::item).filterIsInstance<ConstantInfo.KstUTF8>()
      .find { it.value == "Tuple.kt" }!!
    compileFName.value = "元组.kt"
    ClassFile.write(newFile, kFile) // TODO byte buffer writer for JVM
    println(newFile)
    val newFileReader = newFile.byteArray().reader()
    val newKFile = ClassFile.read(newFileReader)
    println(newKFile.constants.joinToString("\n"))
  }
  abstract val newFile: BufferWriter
  abstract val fileBytes: Buffer
  abstract fun Buffer.reader(): Reader
}