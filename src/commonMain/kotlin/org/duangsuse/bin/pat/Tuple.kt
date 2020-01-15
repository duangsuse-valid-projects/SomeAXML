package org.duangsuse.bin.pat

import org.duangsuse.bin.Idx
import org.duangsuse.bin.Cnt

import org.duangsuse.bin.Sized
import kotlin.reflect.KProperty

/** Creates an object like [Tuple] with given size */
typealias Allocator<T> = (Cnt) -> T

/** Mutable version of [Pair] */
data class Tuple2<A, B>(var first: A, var second: B)

/** Tuple is an array-like object with `val (x0, x1) = (tup)` destruct and index access support
 *
 * + tuple items are stored in array [items], since Kotlin does not support reified type parameters in class,
 * __it should be overridden and created in subclasses using [size]__
 * + named indices using first-class delegation [Index] are supported, declare them using `var name: Type by index(idx)`
 * + destruct component 1..4 are provided, see [Tuple.component1]
 */
abstract class Tuple<E>(override val size: Cnt): Sized {
  protected abstract val items: Array<E>
  operator fun get(index: Idx) = items[index]
  operator fun set(index: Idx, value: E) { items[index] = value }
  fun toArray(): Array<E> = items

  protected fun <T> index(idx: Idx) = Index<T>(idx)
  class Index<T>(private val idx: Idx) {
    operator fun getValue(self: Tuple<out T>, _p: KProperty<*>): T = self[idx]
    operator fun setValue(self: Tuple<in T>, _p: KProperty<*>, value: T) { self[idx] = value }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    return if (other == null || other !is Tuple<*>) false
    else (size == other.size) && items.contentEquals(other.items)
  }
  override fun hashCode(): Int  = 31 * size + items.contentHashCode()
  override fun toString(): String = "(${describe()})"
  private fun describe(): String = items.joinToString(", ")
}
operator fun <E> Tuple<E>.component1() = this[0]
operator fun <E> Tuple<E>.component2() = this[1]
operator fun <E> Tuple<E>.component3() = this[2]
operator fun <E> Tuple<E>.component4() = this[3]
