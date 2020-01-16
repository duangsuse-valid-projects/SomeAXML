package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Tuple2
import org.duangsuse.bin.pat.Pattern
import org.duangsuse.bin.pat.atom.*
import org.duangsuse.bin.type.Buffer
import org.duangsuse.bin.type.Cnt

// atomic helper patterns that should not inherited in like companion objects

infix fun <T, T1> Pattern<T>.mappedBy(map: Map<T, T1>) = object: ConvertedPattern<T, T1>(this) {
  private val revMap = map.reverseMap()
  override fun from(src: T): T1 = map.getValue(src)
  override fun to(x: T1): T = revMap.getValue(x)
}
fun <T> Pattern<T>.magic(value: T, onError: (T) -> Nothing) = object: Pattern.BySizedFully<T>(this) {
  override fun read(s: Reader): T = this@magic.read(s).also { if (it != value) onError(it) }
  override fun write(s: Writer, x: T) { this@magic.write(s, x) }
}
infix fun <T> Pattern<T>.offset(n: Cnt) = object: Pattern.BySized<Tuple2<Buffer, T>>(this) {
  override fun read(s: Reader): Tuple2<Buffer, T> {
    val savedBuffer = s.asNat8Reader().takeByte(n)
    return Tuple2(savedBuffer, this@offset.read(s))
  }
  override fun write(s: Writer, x: Tuple2<Buffer, T>) {
    s.asNat8Writer().writeFrom(x.first)
    this@offset.write(s, x.second)
  }
  override fun writeSize(x: Tuple2<Buffer, T>): Cnt = x.first.size + this@offset.writeSize(x.second)
  override val size: Cnt? = super.size?.plus(n)
}
