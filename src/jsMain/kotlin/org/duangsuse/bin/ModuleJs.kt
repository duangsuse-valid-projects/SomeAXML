package org.duangsuse.bin

import org.khronos.webgl.*

actual interface Flushable {
  actual fun flush()
}
actual interface Closeable {
  actual fun close()
}
actual val nativeOrder: ByteOrder = jsNativeOrder()

private fun jsNativeOrder(): ByteOrder {
  val int16Union = ArrayBuffer(2)
  val int8StructView = Uint8Array(int16Union)
  val int16View = Uint16Array(int16Union)
  int8StructView[0] = 0xFF.toByte(); int8StructView[1] = 0x00.toByte()
  return when (int16View[0]) {
    0x00FF.toShort() -> ByteOrder.LittleEndian
    0xFF00.toShort() -> ByteOrder.BigEndian
    else -> error("unknown order")
  }
}
