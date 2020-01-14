package org.duangsuse.bin.io

import org.duangsuse.bin.byteReader

class ReaderTests: AbstractReaderTests(Reader(byteReader))
private val byteReader = ReaderTests::class.java.getResourceAsStream("binary").byteReader()
