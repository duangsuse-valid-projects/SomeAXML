package org.duangsuse.bin.io

import org.duangsuse.bin.byteReader
import java.io.InputStream

class ReaderTests: AbstractReaderTests(Reader(testBinFile.byteReader()))
internal val testBinFile: InputStream get() = ReaderTests::class.java.getResourceAsStream("binary")
