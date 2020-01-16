package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.ByteOrder
import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer
import org.duangsuse.bin.pat.Pattern

/** Add on call/return listener for [Pattern] read/write */
abstract class PrePost<T>(private val item: Pattern<T>): Pattern.BySizedFully<T>(item) {
  final override fun read(s: Reader): T {
    onReadPre(s)
    return item.read(s).also { onReadPost(s, it) }
  }
  final override fun write(s: Writer, x: T) {
    onWritePre(s, x)
    item.write(s, x).also { onWritePost(s) }
  }

  protected open fun onReadPre(s: Reader) {}
  protected open fun onReadPost(s: Reader, result: T) {}
  protected open fun onWritePre(s: Writer, x: T) {}
  protected open fun onWritePost(s: Writer) {}
}

/** Read/write child [item] in byte order of [newEndian], then restore old order.
 *
 * NOTE: Thread safety __IS NOT__ guaranteed */
sealed class EndianSwitch<T>(item: Pattern<T>, private val newEndian: ByteOrder): PrePost<T>(item) {
  private lateinit var old: ByteOrder
  override fun onReadPre(s: Reader) {
    old = s.byteOrder
    s.byteOrder = newEndian
  }
  override fun onReadPost(s: Reader, result: T) {
    s.byteOrder = old
  }
  override fun onWritePre(s: Writer, x: T) {
    old = s.byteOrder
    s.byteOrder = newEndian
  }
  override fun onWritePost(s: Writer) {
    s.byteOrder = old
  }
  open class BigEndian<T>(item: Pattern<T>): EndianSwitch<T>(item, ByteOrder.BigEndian)
  open class LittleEndian<T>(item: Pattern<T>): EndianSwitch<T>(item, ByteOrder.LittleEndian)
}
