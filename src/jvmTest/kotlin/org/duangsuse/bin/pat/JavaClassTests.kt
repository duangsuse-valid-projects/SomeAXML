package org.duangsuse.bin.pat

import org.duangsuse.bin.io.Reader
import org.duangsuse.bin.byteReader
import java.io.InputStream

class JavaClassTests: AbstractJavaClassTests(Reader(testClassFile.byteReader()))
internal val testClassFile: InputStream get() = JavaClassTests::class.java.getResourceAsStream("Tuple.class")
