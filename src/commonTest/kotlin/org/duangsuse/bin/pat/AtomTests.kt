package org.duangsuse.bin.pat

import kotlin.test.Test
import kotlin.test.assertEquals

class AtomTests {
  @Test fun uExt() {
    val xs = (-10..10).toList().map(Int::toShort)
    val ys = xs.map { it.uExt().toShort() }
    assertEquals(xs, ys)
  }
}