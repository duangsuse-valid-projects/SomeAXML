package org.duangsuse.bin.pat

import org.duangsuse.bin.*

abstract class Int32Pattern: Pattern.Sized<Int32>

fun Pattern.Sized<Int8>.widen() = object: Int32Pattern() {
  override fun read(s: Reader): Int32 = this@widen.read(s).toInt()
  override fun write(s: Writer, x: Int32) = this@widen.write(s, x.toByte())
  override val size: Cnt? get() = this@widen.size
}
fun Pattern.Sized<Int16>.widen() = object: Int32Pattern() {
  override fun read(s: Reader): Int32 = this@widen.read(s).toInt()
  override fun write(s: Writer, x: Int32) = this@widen.write(s, x.toShort())
  override val size: Cnt? get() = this@widen.size
}
fun Pattern.Sized<Int64>.narrow() = object: Int32Pattern() {
  override fun read(s: Reader): Int32 = this@narrow.read(s).toInt()
  override fun write(s: Writer, x: Int32) = this@narrow.write(s, x.toLong())
  override val size: Cnt? get() = this@narrow.size
}
