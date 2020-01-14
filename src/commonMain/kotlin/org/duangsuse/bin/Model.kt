package org.duangsuse.bin

/** Something like stream with [estimate] and [skip] subtracts it */
interface Estimable {
  val estimate: Cnt
  fun skip(n: Cnt)
}
interface Nat8Reader: Estimable {
  fun read(): Nat8
  fun readTo(buffer: Buffer)
  fun readTo(buffer: Buffer, indices: IdxRange)
}
interface Nat8Writer {
  fun write(x: Nat8)
  fun writeFrom(buffer: Buffer)
  fun writeFrom(buffer: Buffer, indices: IdxRange)
}

interface Reader: ReadControl, DataReader
interface Writer: WriteControl, DataWriter

interface MarkReset {
  fun mark() fun reset()
}
expect interface Closeable {
  fun close()
}
expect interface Flushable {
  fun flush()
}
expect class StreamEnd(): Exception

interface ReadControl: Estimable, MarkReset, Closeable {
  val position: Cnt
}
interface WriteControl: Flushable, Closeable

interface DataReader: ByteOrdered {
  fun readNat8():Nat8
  fun readInt8():Int8 fun readInt16():Int16
  fun readInt32():Int32 fun readInt64():Int64
  fun readRat32():Rat32 fun readRat64():Rat64
}
interface DataWriter: ByteOrdered {
  fun writeNat8(x:Nat8)
  fun writeInt8(x:Int8) fun writeInt16(x:Int16)
  fun writeInt32(x:Int32) fun writeInt64(x:Int64)
  fun writeRat32(x:Rat32) fun writeRat64(x:Rat64)
}

enum class ByteOrder {
  BigEndian, LittleEndian
}
interface ByteOrdered {
  var byteOrder: ByteOrder
}
expect val nativeOrder: ByteOrder
val LANGUAGE_ORDER = ByteOrder.BigEndian
