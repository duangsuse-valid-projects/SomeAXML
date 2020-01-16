package org.duangsuse.bin.pat

import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer
import org.duangsuse.bin.OptionalSized

/** Some pattern that can be [read]/[write] on binary streams, maybe [Sized] */
interface Pattern<T> {
  fun read(s: Reader): T
  fun write(s: Writer, x: T)
  interface Sized<T>: Pattern<T>, OptionalSized
}