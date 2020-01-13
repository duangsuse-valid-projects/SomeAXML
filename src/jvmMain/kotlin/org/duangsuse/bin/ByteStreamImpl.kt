package org.duangsuse.bin

import java.io.InputStream
import java.io.OutputStream

class InStreamByteReader(private val in_stream: InputStream, private val mark_size: Cnt = 0): ByteReader, Closeable, MarkReset {
  override fun read(): Byte = in_stream.read().toByte()
  override fun readTo(buffer: Buffer) {
    in_stream.read(buffer, 0, buffer.size)
  }
  override fun readTo(buffer: Buffer, indices: IdxRange) {
    in_stream.read(buffer, indices.first, indices.size)
  }
  override fun mark() { in_stream.mark(mark_size) }
  override fun reset() { in_stream.reset() }
  override val estimate: Cnt get() = in_stream.available()
  override fun skip(n: Cnt) { in_stream.skip(n.toLong()) }
  override fun close() { in_stream.close() }
}
class OutStreamByteWriter(private val out_stream: OutputStream): ByteWriter, Closeable, Flushable {
  override fun write(byte: Byte) { out_stream.write(byte.toInt()) }
  override fun writeFrom(buffer: Buffer) {
    out_stream.write(buffer, 0, buffer.size)
  }
  override fun writeFrom(buffer: Buffer, indices: IdxRange) {
    out_stream.write(buffer, indices.first, indices.size)
  }
  override fun close() { out_stream.close() }
  override fun flush() { out_stream.flush() }
}
