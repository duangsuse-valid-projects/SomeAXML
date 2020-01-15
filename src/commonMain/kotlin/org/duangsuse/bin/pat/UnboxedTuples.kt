package org.duangsuse.bin.pat

import org.duangsuse.bin.Idx
import org.duangsuse.bin.Cnt

typealias IndexedProducer<R> = (Idx) -> R

// NOTE: this is not truly "unboxed" primitive arrays, but it's easier to apply optimizations by a optimistic JVM
//   It's important that Tuple<E> should not be defined too clearly about implementation details
//   for large amount element storage, `Pattern.array` is provided.

// JVM Z,B,S,C,I,J,F,D

open class BooleanTuple(size: Cnt, init: IndexedProducer<Boolean> = {false})
  : Tuple<Boolean>(size) {
  final override val items: Array<Boolean> = Array(size, init)
}

open class ByteTuple(size: Cnt, init: IndexedProducer<Byte> = {0.toByte()})
  : Tuple<Byte>(size) {
  final override val items: Array<Byte> = Array(size, init)
}
open class ShortTuple(size: Cnt, init: IndexedProducer<Short> = {0.toShort()})
  : Tuple<Short>(size) {
  final override val items: Array<Short> = Array(size, init)
}
open class CharTuple(size: Cnt, init: IndexedProducer<Char> = {'\u0000'})
  : Tuple<Char>(size) {
  final override val items: Array<Char> = Array(size, init)
}
open class IntTuple(size: Cnt, init: IndexedProducer<Int> = {0})
  : Tuple<Int>(size) {
  final override val items: Array<Int> = Array(size, init)
}
open class LongTuple(size: Cnt, init: IndexedProducer<Long> = {0L})
  : Tuple<Long>(size) {
  final override val items: Array<Long> = Array(size, init)
}

open class FloatTuple(size: Cnt, init: IndexedProducer<Float> = {0.0F})
  : Tuple<Float>(size) {
  final override val items: Array<Float> = Array(size, init)
}
open class DoubleTuple(size: Cnt, init: IndexedProducer<Double> = {0.0})
  : Tuple<Double>(size) {
  final override val items: Array<Double> = Array(size, init)
}
