package org.duangsuse.bin

import java.io.InputStream
import java.io.OutputStream
import java.io.File
import java.net.URL

fun InputStream.byteReader(mark_size: Cnt = 0): InStreamNat8Reader = InStreamNat8Reader(this, mark_size)
fun OutputStream.byteWriter(): OutStreamNat8Writer = OutStreamNat8Writer(this)
fun File.byteReader(): InStreamNat8Reader = inputStream().byteReader()
fun File.byteWriter(): OutStreamNat8Writer = outputStream().byteWriter()
fun URL.byteReader(): InStreamNat8Reader = openConnection().getInputStream().byteReader()
