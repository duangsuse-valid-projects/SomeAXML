package org.duangsuse.bin.pat

import org.duangsuse.bin.Cnt
import org.duangsuse.bin.indices

/** Creates an object like [Tuple] with given size */
typealias Allocator<T> = (Cnt) -> T

/** Mutable version of [Pair] */
data class Tuple2<A, B>(var first: A, var second: B)

operator fun <E> Tuple<E>.component1() = this[0]
operator fun <E> Tuple<E>.component2() = this[1]
operator fun <E> Tuple<E>.component3() = this[2]
operator fun <E> Tuple<E>.component4() = this[3]

fun <E> Tuple<E>.toList(): List<E> = indices.map(this::get)
