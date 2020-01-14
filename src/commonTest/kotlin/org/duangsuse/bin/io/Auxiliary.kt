package org.duangsuse.bin.io

import kotlin.test.assertEquals

internal fun <E> assertArrayEquals(expected: Array<E>, actual: Array<E>) = assertEquals(expected.toList(), actual.toList())
internal fun assertArrayEquals(expected: ByteArray, actual: ByteArray) = assertArrayEquals(expected.toTypedArray(), actual.toTypedArray())
