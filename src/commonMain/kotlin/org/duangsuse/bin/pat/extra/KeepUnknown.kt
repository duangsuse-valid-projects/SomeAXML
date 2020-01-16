package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Pattern

/** Keep original array for __ALL BYTES REST__ in stream, may used for partial data extracting */
object Keep: Pattern<ByteArray> {
  override fun read(s: Reader): ByteArray = s.asNat8Reader().takeByte(s.estimate)
  override fun write(s: Writer, x: ByteArray): Unit = s.asNat8Writer().writeFrom(x)
  override fun writeSize(x: ByteArray): Cnt = x.size
}

/** [IllegalStateException] will be thrown when called */
object Unknown: Pattern<Nothing> {
  override fun read(s: Reader): Nothing = error("unknown data part @${s.position}")
  override fun write(s: Writer, x: Nothing) = impossible()
  override fun writeSize(x: Nothing): Cnt = impossible()
}
operator fun Pattern<Nothing>.unaryPlus() = object: Pattern<Any> {
  override fun read(s: Reader): Nothing = this@unaryPlus.read(s)
  override fun write(s: Writer, x: Any) = impossible()
  override fun writeSize(x: Any): Cnt = impossible()
}
