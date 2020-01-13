package org.duangsuse.bin

import java.io.InputStream
import java.io.OutputStream
import java.io.File
import java.net.URL

fun InputStream.byteReader(mark_size: Cnt = 0): InStreamByteReader = InStreamByteReader(this)
fun OutputStream.byteWriter(): OutStreamByteWriter = OutStreamByteWriter(this)
fun File.byteReader(): InStreamByteReader = inputStream().byteReader()
fun File.byteWriter(): OutStreamByteWriter = outputStream().byteWriter()
fun URL.byteReader(): InStreamByteReader = openConnection().getInputStream().byteReader()
