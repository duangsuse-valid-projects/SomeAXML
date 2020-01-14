package org.duangsuse.bin

import java.io.InputStream
import java.io.OutputStream

class InStreamNat8Reader(private val in_stream: InputStream, private val mark_size: Cnt = 0): Nat8Reader, Closeable, MarkReset {
  override fun read(): Nat8 = in_stream.read()
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
class OutStreamNat8Writer(private val out_stream: OutputStream): Nat8Writer, Closeable, Flushable {
  override fun write(x: Nat8) { out_stream.write(x) }
  override fun writeFrom(buffer: Buffer) {
    out_stream.write(buffer, 0, buffer.size)
  }
  override fun writeFrom(buffer: Buffer, indices: IdxRange) {
    out_stream.write(buffer, indices.first, indices.size)
  }
  override fun close() { out_stream.close() }
  override fun flush() { out_stream.flush() }
}
