package org.duangsuse.bin.pat

import org.duangsuse.bin.Cnt
import org.duangsuse.bin.Idx
import org.duangsuse.bin.indices
import kotlin.reflect.KProperty

interface Sized {
  val size: Cnt
}
abstract class Tuple<E>(override val size: Cnt): Sized {
  protected abstract val items: Array<E>
  operator fun get(index: Idx) = items[index]
  operator fun set(index: Idx, value: E) { items[index] = value }

  protected fun <T> index(idx: Idx) = Index<T>(idx)
  class Index<T>(private val idx: Idx) {
    operator fun getValue(self: Tuple<out T>, _p: KProperty<*>): T = self[idx]
    operator fun setValue(self: Tuple<in T>, _p: KProperty<*>, value: T) { self[idx] = value }
  }
}
operator fun <E> Tuple<E>.component1() = this[0]
operator fun <E> Tuple<E>.component2() = this[1]
operator fun <E> Tuple<E>.component3() = this[2]
operator fun <E> Tuple<E>.component4() = this[3]
fun <E> Tuple<E>.toList(): List<E> = indices.map(this::get)
