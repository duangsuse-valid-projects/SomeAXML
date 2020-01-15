package org.duangsuse.bin.pat

import org.duangsuse.bin.uExt
import kotlin.test.Test
import kotlin.test.assertEquals

class AtomTests {
  @Test fun uExt16() {
    val xs = (-10..10).toList().map(Int::toShort)
    val ys = xs.map { it.uExt().toShort() }
    assertEquals(xs, ys)
  }
  @Test fun uExt32() {
    val xs = (-10..10).toList()
    val ys = xs.map { it.uExt().toInt() }
    assertEquals(xs, ys)
  }
}