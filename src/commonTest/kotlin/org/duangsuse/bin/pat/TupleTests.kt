package org.duangsuse.bin.pat

import org.duangsuse.bin.Cnt
import kotlin.test.Test
import kotlin.test.assertEquals

class TupleTests {
  open class IntTuple(size: Cnt): Tuple<Int>(size) {
    override val items: Array<Int> = Array(size) {0}
    var fst: Int by index(0)
    var snd: Int by index(1)
    var trd: Int by index(2)
  }
  class IntTriple: IntTuple(3)
  @Test fun index() {
    val tup = IntTuple(3)
    tup[0] = 9; tup[1] = 9; tup[2] = 6
    assertEquals(listOf(9, 9, 6), tup.toList())
  }
  @Test fun valueDelegate() {
    val tup = IntTriple()
    tup.fst = 6; tup.snd = 2; tup.trd = 1
    assertEquals(listOf(6, 2, 1), tup.toList())
  }
  @Test fun destruct() {
    val tup = IntTriple()
    tup.trd = 9
    val (a, b, c) = tup
    assertEquals(listOf(0, 0, 9), listOf(a, b, c))
  }
}