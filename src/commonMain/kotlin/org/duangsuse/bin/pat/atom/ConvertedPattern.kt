package org.duangsuse.bin.pat.atom

import org.duangsuse.bin.Cnt
import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer
import org.duangsuse.bin.pat.Pattern

/** Converting pattern between [T] read, [T1] actual */
abstract class ConvertedPattern<T, T1>(private val source: Pattern<T>): Pattern.BySized<T1>(source) {
  override fun read(s: Reader): T1 = from(source.read(s))
  override fun write(s: Writer, x: T1) = source.write(s, to(x))
  override fun writeSize(x: T1): Cnt = source.writeSize(to(x))
  protected abstract fun from(src: T): T1
  protected abstract fun to(x: T1): T
}