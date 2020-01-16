package org.duangsuse.bin.pat.basic

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Allocator
import org.duangsuse.bin.pat.Pattern
import org.duangsuse.bin.pat.Tuple

/** Sequential binary pattern like C's `struct` */
open class Seq<TUP: Tuple<T>, T>(private val allocator: Allocator<TUP>, private vararg val items: Pattern<T>): Pattern.Sized<TUP> {
  constructor(creator: Producer<TUP>, vararg items: Pattern<T>): this({ _ -> creator() }, *items)
  override fun read(s: Reader): TUP {
    val tuple = allocator(items.size)
    for ((index, item) in items.withIndex()) tuple[index] = item.read(s)
    return tuple
  }
  override fun write(s: Writer, x: TUP) {
    for ((index, item) in items.withIndex()) item.write(s, x[index])
  }
  override fun writeSize(x: TUP): Cnt = items.zip(x.toArray()).map { it.first.writeSize(it.second) }.sum()
  override val size: Cnt?
    get() = items.toList()
      .takeIfAllIsInstance<Pattern.Sized<T>>()
      ?.mapTakeIfAllNotNull(OptionalSized::size)
      ?.sum()
}
/** Repeat of one substructure [item], with size depending on actual data stream [sizer] */
open class Repeat<T: Any>(private val sizer: Pattern<Cnt>, private val item: Pattern<T>): Pattern<Array<T>> {
  override fun read(s: Reader): Array<T> {
    val size = sizer.read(s)
    val ary = arrayOfNulls<Any>(size)
    for (index in ary.indices) ary[index] = item.read(s)
    @Suppress("unchecked_cast") return ary as Array<T>
  }
  override fun write(s: Writer, x: Array<T>) {
    sizer.write(s, x.size)
    for (element in x) item.write(s, element)
  }
  override fun writeSize(x: Array<T>): Cnt = sizer.writeSize(x.size) + x.map(item::writeSize).sum()
}
/** Conditional sub-patterns [conditions] like C's `union` can be decided depending on actual data stream with [flag] */
open class Cond<T>(private val flag: Pattern<Idx>, private vararg val conditions: Pattern<T>): Pattern.Sized<Pair<Idx, T>> {
  override fun read(s: Reader): Pair<Idx, T> {
    val caseNo = flag.read(s)
    return Pair(caseNo, conditions[caseNo].read(s))
  }
  override fun write(s: Writer, x: Pair<Idx, T>) {
    val (caseNo, state) = x
    flag.write(s, caseNo)
    conditions[caseNo].write(s, state)
  }
  override fun writeSize(x: Pair<Idx, T>): Cnt = conditions[x.first].writeSize(x.second)
  /** byte size can be unified when all [conditions] has same static size */
  override val size: Cnt?
    get() = conditions.toList()
      .takeIfAllIsInstance<Pattern.Sized<T>>()
      ?.mapTakeIfAllNotNull(OptionalSized::size)
      ?.toSet()?.singleOrNull()
}
