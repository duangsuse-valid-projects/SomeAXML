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
  boolean is T -> boolean as T
  byte is T -> byte as T
  short is T -> short as T
  int is T -> int as T
  long is T -> long as T
  float is T -> float as T
  double is T -> double as T
  null is T -> null as T
  else -> error("Unsupported ${T::class}, specify initiate explicitly")
}

@PublishedApi internal object ZeroValues {
  const val boolean = false
  const val byte = 0.toByte()
  const val short = 0.toShort()
  const val int = 0
  const val long = 0L
  const val float = 0.0F
  const val double = 0.0
}
