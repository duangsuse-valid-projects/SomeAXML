package org.duangsuse.bin.pat

import org.duangsuse.bin.*

/** Some pattern that can be [read]/[write] on binary streams, maybe [Sized] */
interface Pattern<T> {
  fun read(s: Reader): T
  fun write(s: Writer, x: T)
  interface Sized<T>: Pattern<T>, OptionalSized
}

/** Sequential binary pattern like C's `struct` */
class Seq<TUP: Tuple<T>, T>(private val allocator: Allocator<TUP>, private vararg val items: Pattern<T>): Pattern.Sized<TUP> {
  constructor(creator: Producer<TUP>, vararg items: Pattern<T>): this({ _ -> creator() }, *items)
  override fun read(s: Reader): TUP {
    val tuple = allocator(items.size)
    for ((index, item) in items.withIndex()) tuple[index] = item.read(s)
    return tuple
  }
  override fun write(s: Writer, x: TUP) {
    for ((index, item) in items.withIndex()) item.write(s, x[index])
  }
  override val size: Cnt?
    get() = items.toList()
      .takeIfAllIsInstance<Pattern.Sized<T>>()
      ?.mapTakeIfAllNotNull(OptionalSized::size)
      ?.sum()
}
/** Repeat of one substructure [item], with size depending on actual data stream [sizer] */
class Repeat<T: Any>(private val sizer: Pattern<Cnt>, private val item: Pattern<T>): Pattern<Array<T>> {
  @Suppress("UNCHECKED_CAST")
  override fun read(s: Reader): Array<T> {
    val size = sizer.read(s)
    val ary = arrayOfNulls<Any>(size)
    for (index in ary.indices) ary[index] = item.read(s)
    return ary as Array<T>
  }
  override fun write(s: Writer, x: Array<T>) {
    sizer.write(s, x.size)
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
