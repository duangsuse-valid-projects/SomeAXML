package org.duangsuse.bin.pat.basic

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Allocator
import org.duangsuse.bin.pat.Pattern
import org.duangsuse.bin.pat.Tuple
import org.duangsuse.bin.type.Cnt
import org.duangsuse.bin.OptionalSized
import org.duangsuse.bin.type.Producer

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

// TODO replace fake Array<T> with abstract items array, combine with primitiveArray
// TODO open fun postprocessing for created item

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

/** Conditional sub-patterns like C's tagged `union`, decided on actual data stream with [tag] */
open class Cond<TAG, E>(private val tag: Pattern<TAG>, private vararg val conditions: Case<TAG, E>)
  : Pattern.Sized<Cond.TagItem<TAG, E>> {
  interface Tagged<out TAG> { val tag: TAG }
  interface Case<out TAG, out E>: Tagged<TAG>, Pattern<@kotlin.UnsafeVariance E> // for TagItem
  data class TagItem<out TAG, out E>(override val tag: TAG, val item: E): Tagged<TAG> {
    override fun toString(): String = "$item#$tag"
  }
  private val tagMap = conditions.asIterable().mapBy(Tagged<TAG>::tag)
  override fun read(s: Reader): TagItem<TAG, E> {
    val caseTag = tag.read(s)
    val case = tagMap[caseTag] ?: error("unknown case tag $caseTag")
    return TagItem(caseTag, case.read(s))
  }
  override fun write(s: Writer, x: TagItem<TAG, E>) {
    tag.write(s, x.tag)
    val case = tagMap.getValue(x.tag)
    case.write(s, x.item)
  }
  override fun writeSize(x: TagItem<TAG, E>): Cnt = tagMap.getValue(x.tag).writeSize(x.item)
  /** byte size can be unified when all [conditions] has same static size */
  override val size: Cnt?
    get() = conditions.toList()
      .takeIfAllIsInstance<Pattern.Sized<E>>()
      ?.mapTakeIfAllNotNull(OptionalSized::size)
      ?.toSet()?.singleOrNull()
}
