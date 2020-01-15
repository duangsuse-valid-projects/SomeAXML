package org.duangsuse.bin.pat

import org.duangsuse.bin.Cnt
import org.duangsuse.bin.Idx
import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer
import kotlin.reflect.KProperty

open class AnyTuple(size: Cnt)
  : Tuple<Any>(size) {
  @Suppress("UNCHECKED_CAST")
  final override val items: Array<Any> = arrayOfNulls<Any>(size) as Array<Any>
}

inline fun <reified T> Tuple<*>.indexOf(idx: Idx) = CastIndex<T>(idx)
class CastIndex<T>(private val idx: Idx) {
  @Suppress("UNCHECKED_CAST")
  operator fun getValue(self: Tuple<*>, _p: KProperty<*>): T = self[idx] as T
  operator fun setValue(self: Tuple<Any>, _p: KProperty<*>, value: T) { self[idx] = value as Any }
}

inline operator fun <reified T: Any> Pattern<T>.unaryPlus() = object: Pattern<Any> {
  override fun read(s: Reader): Any = this@unaryPlus.read(s)
  override fun write(s: Writer, x: Any) = this@unaryPlus.write(s, x as T)
}
