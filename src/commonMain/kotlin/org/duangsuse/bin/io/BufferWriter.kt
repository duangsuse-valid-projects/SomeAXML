package org.duangsuse.bin.io

import org.duangsuse.bin.type.Cnt

expect class BufferWriter(n: Cnt): Writer {
  fun byteArray(): ByteArray
}