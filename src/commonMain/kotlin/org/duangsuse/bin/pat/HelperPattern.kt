package org.duangsuse.bin.pat

import org.duangsuse.bin.ByteOrder
import org.duangsuse.bin.Cnt
import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer

/** Add argument/return listen for [Pattern] read/write */
abstract class PrePost<T>(private val item: Pattern<T>): Pattern.Sized<T> {
  override fun read(s: Reader): T {
    onReadPre(s)
    return item.read(s).also { onReadPost(s, it) }
  }
  override fun write(s: Writer, x: T) {
    onWritePre(s, x)
    item.write(s, x).also { onWritePost(s) }
  }
  open fun onReadPre(s: Reader) {}
  open fun onReadPost(s: Reader, result: T) {}
  open fun onWritePre(s: Writer, x: T) {}
  open fun onWritePost(s: Writer) {}
  override val size: Cnt? get() = (item as? Pattern.Sized<T>)?.size
}

/** Read/write child [item] in byte order of [newEndian], then restore old order.
 *
 * Thread safety is not guaranteed */
open class EndianSwitch<T>(item: Pattern<T>, private val newEndian: ByteOrder): PrePost<T>(item) {
  private lateinit var oldByteOrder: ByteOrder
  override fun onReadPre(s: Reader) {
    oldByteOrder = s.byteOrder
    s.byteOrder = newEndian
  }
  override fun onReadPost(s: Reader, result: T) {
    s.byteOrder = oldByteOrder
  }
  override fun onWritePre(s: Writer, x: T) {
    oldByteOrder = s.byteOrder
    s.byteOrder = newEndian
  }
  override fun onWritePost(s: Writer) {
    s.byteOrder = oldByteOrder
  }
  class BigEndian<T>(item: Pattern<T>): EndianSwitch<T>(item, ByteOrder.BigEndian)
  class LittleEndian<T>(item: Pattern<T>): EndianSwitch<T>(item, ByteOrder.LittleEndian)
}

fun <T> Pattern<T>.littleEndian() = EndianSwitch.LittleEndian(this)
fun <T> Pattern<T>.bigEndian() = EndianSwitch.BigEndian(this)
