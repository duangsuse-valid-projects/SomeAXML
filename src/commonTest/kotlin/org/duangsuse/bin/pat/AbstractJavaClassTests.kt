package org.duangsuse.bin.pat

import org.duangsuse.bin.Reader
import kotlin.test.Test

abstract class AbstractJavaClassTests(private val s: Reader) {
  @Test fun read() {
    val res = ClassFile.read(s)
    println(res)
  }
}