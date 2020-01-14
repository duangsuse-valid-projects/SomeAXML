package org.duangsuse.bin

import java.nio.ByteOrder.nativeOrder as javaNativeOrder
import java.nio.ByteOrder.*

actual interface Closeable: java.io.Closeable
actual interface Flushable: java.io.Flushable
actual class StreamEnd: Exception("unexpected EOF")
actual val nativeOrder: ByteOrder = when (javaNativeOrder()) {
  BIG_ENDIAN -> ByteOrder.BigEndian
  LITTLE_ENDIAN -> ByteOrder.LittleEndian
  else -> error("unknown order")
}
