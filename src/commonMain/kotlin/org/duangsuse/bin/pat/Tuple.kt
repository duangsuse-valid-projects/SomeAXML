package org.duangsuse.bin.pat

import org.duangsuse.bin.Cnt
import org.duangsuse.bin.Idx
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface Sized {
  val size: Cnt
}
abstract class Tuple<E>(override val size: Cnt): Sized {
  protected abstract val items: Array<E>
  operator fun get(index: Idx) = items[index]
  operator fun set(index: Idx, value: E) { items[index] = value }

  protected fun <T> index(idx: Idx) = object {
    operator fun getValue(_k: KClass<*>, _p: KProperty<*>, self: Tuple<out T>): T = self[idx]
    operator fun setValue(_k: KClass<*>, _p: KProperty<*>, self: Tuple<in T>, value: T) { self[idx] = value }
  }
}
fun <E> Tuple<E>.component1() = this[0]
fun <E> Tuple<E>.component2() = this[1]
fun <E> Tuple<E>.component3() = this[2]
fun <E> Tuple<E>.component4() = this[3]
