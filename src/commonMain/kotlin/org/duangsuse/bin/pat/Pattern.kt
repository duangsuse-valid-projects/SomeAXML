package org.duangsuse.bin.pat

import org.duangsuse.bin.Cnt
import org.duangsuse.bin.Idx
import org.duangsuse.bin.ActionOn
import org.duangsuse.bin.Reader
import org.duangsuse.bin.Writer

interface OptionalSized {
  val size: Cnt?
}
/** Some pattern that can be [read]/[write] on binary streams, maybe [Sized] */
interface Pattern<T> {
  fun read(s: Reader): T
  fun write(s: Writer, x: T)
  interface Sized<T>: Pattern<T>, OptionalSized
}

/** Sequential binary pattern like C's `struct` */
class Seq<T>(private val allocator: Allocator<T>, private vararg val items: Pattern<T>): Pattern.Sized<Tuple<T>> {
  override fun read(s: Reader): Tuple<T> {
    val result = allocator(items.size)
    for ((index, item) in items.withIndex()) result[index] = item.read(s)
    return result
  }
  override fun write(s: Writer, x: Tuple<T>) {
    for ((index, item) in items.withIndex()) item.write(s, x[index])
  }
  override val size: Cnt?
    get() = items.toList()
      .takeIfAllIsInstance<Pattern.Sized<T>>()
      ?.mapTakeIfAllNotNull(OptionalSized::size)
      ?.sum()
}
/** Repeat of one substructure [item], with size depending on actual data stream [readSize] */
class Repeat<T: Any>(private val item: Pattern<T>, private val readSize: ActionOn<Reader, Cnt>): Pattern<Array<T>> {
  @Suppress("UNCHECKED_CAST")
  override fun read(s: Reader): Array<T> {
    val result = arrayOfNulls<Any?>(s.readSize())
    for (index in result.indices) result[index] = item.read(s)
    return result as Array<T>
  }
  override fun write(s: Writer, x: Array<T>) {
    for (element in x) item.write(s, element)
  }
}
/** Conditional sub-patterns [conditions] like C's `union` can be decided depending on actual data stream with [flag] */
class Cond<T>(private val flag: Pattern<Idx>, private vararg val conditions: Pattern<T>): Pattern.Sized<Pair<Idx, T>> {
  override fun read(s: Reader): Pair<Idx, T> {
    val caseNo = flag.read(s)
    return Pair(caseNo, conditions[caseNo].read(s))
  }
  override fun write(s: Writer, x: Pair<Idx, T>) {
    val (caseNo, state) = x
    flag.write(s, caseNo)
    conditions[caseNo].write(s, state)
  }
  /** byte size can be unified when all [conditions] has same static size */
  override val size: Cnt?
    get() = conditions.toList()
      .takeIfAllIsInstance<Pattern.Sized<T>>()
      ?.mapTakeIfAllNotNull(OptionalSized::size)
      ?.toSet()?.singleOrNull()
}


internal inline fun <reified T> Collection<*>.takeIfAllIsInstance(): List<T>?
  = filterIsInstance<T>().takeIf { it.size == this.size }

internal fun <T, R: Any> Collection<T>.mapTakeIfAllNotNull(op: (T) -> R?): List<R>?
  = mapNotNull(op).takeIf { it.size == this.size }
