package org.duangsuse.bin.pat

import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer
import org.duangsuse.bin.type.Cnt
import org.duangsuse.bin.OptionalSized
import org.duangsuse.bin.Sized as ExactlySized

/** Some pattern that can be [read]/[write] on binary streams, may (byte)[Sized] */
interface Pattern<T> {
  fun read(s: Reader): T
  fun write(s: Writer, x: T)
  /** Determine just-right byte size of writing */
  fun writeSize(x: T): Cnt

  /** Represent patterns that are _sized_,
   *  which means its byte size __can be determined when created__ and __never mutates__ */
  interface Sized<T>: Pattern<T>, OptionalSized
  /** Statically sized patterns represent items in __constant byte construct__, like 32-bit (4 byte) integers */
  interface StaticallySized<T>: Sized<T>, ExactlySized {
    override fun writeSize(x: T): Cnt = size
  }

  /** Delegate class for patterns optional [Sized] */
  abstract class BySized<T>(self: Pattern<*>): Sized<T> {
    override val size: Cnt? = (self as? Sized<*>)?.size
  }
  /** Child-classes may only override [read]/[write],
   *  __byte construct__ are __expected exactly the same__ with [self] */
  abstract class BySizedFully<T>(private val self: Pattern<T>): BySized<T>(self) {
    final override val size: Cnt? = super.size
    final override fun writeSize(x: T): Cnt = self.writeSize(x)
  }
}