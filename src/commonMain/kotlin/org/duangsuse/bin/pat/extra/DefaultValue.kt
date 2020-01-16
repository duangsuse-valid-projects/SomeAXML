package org.duangsuse.bin.pat.extra

import org.duangsuse.bin.pat.extra.ZeroValues.boolean
import org.duangsuse.bin.pat.extra.ZeroValues.byte
import org.duangsuse.bin.pat.extra.ZeroValues.short
import org.duangsuse.bin.pat.extra.ZeroValues.int
import org.duangsuse.bin.pat.extra.ZeroValues.long
import org.duangsuse.bin.pat.extra.ZeroValues.float
import org.duangsuse.bin.pat.extra.ZeroValues.double

/** Try for boolean,byte,short,char,int,long,float,double,reference primitives */
@PublishedApi internal inline fun <reified T> defaultValue(): T = when {
  boolean is T -> boolean
  byte is T -> byte
  short is T -> short
  int is T -> int
  long is T -> long
  float is T -> float
  double is T -> double
  null is T -> null
  else -> error("Unsupported ${T::class}, specify initiate explicitly")
} as T

@PublishedApi internal object ZeroValues {
  const val boolean = false
  const val byte = 0.toByte()
  const val short = 0.toShort()
  const val int = 0
  const val long = 0L
  const val float = 0.0F
  const val double = 0.0
}
