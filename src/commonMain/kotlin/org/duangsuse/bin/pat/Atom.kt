package org.duangsuse.bin.pat

import org.duangsuse.bin.*

val nat8 = object: Pattern.Sized<Nat8> {
  override fun read(s: Reader): Nat8 = s.readNat8()
  override fun write(s: Writer, x: Nat8): Unit = s.writeNat8(x)
  override val size: Cnt = 8/Byte.SIZE_BITS
}
val int8 = object: Pattern.Sized<Int8> {
  override fun read(s: Reader): Int8 = s.readInt8()
  override fun write(s: Writer, x: Int8): Unit = s.writeInt8(x)
  override val size: Cnt = Int8.SIZE_BYTES
}
val int16 = object: Pattern.Sized<Int16> {
  override fun read(s: Reader): Int16 = s.readInt16()
  override fun write(s: Writer, x: Int16): Unit = s.writeInt16(x)
  override val size: Cnt = Int16.SIZE_BYTES
}
val int32 = object: Pattern.Sized<Int32> {
  override fun read(s: Reader): Int32 = s.readInt32()
  override fun write(s: Writer, x: Int32): Unit = s.writeInt32(x)
  override val size: Cnt = Int32.SIZE_BYTES
}
val int64 = object: Pattern.Sized<Int64> {
  override fun read(s: Reader): Int64 = s.readInt64()
  override fun write(s: Writer, x: Int64): Unit = s.writeInt64(x)
  override val size: Cnt = Int64.SIZE_BYTES
}
val rat32 = object: Pattern.Sized<Rat32> {
  override fun read(s: Reader): Rat32 = s.readRat32()
  override fun write(s: Writer, x: Rat32): Unit = s.writeRat32(x)
  override val size: Cnt = 32/Byte.SIZE_BITS
}
val rat64 = object: Pattern.Sized<Rat64> {
  override fun read(s: Reader): Rat64 = s.readRat64()
  override fun write(s: Writer, x: Rat64): Unit = s.writeRat64(x)
  override val size: Cnt = 64/Byte.SIZE_BITS
}

val char = object: Pattern.Sized<Char> {
  override fun read(s: Reader): Char = s.readInt16().toChar()
  override fun write(s: Writer, x: Char): Unit = s.writeInt16(x.toShort())
  override val size: Cnt = Char.SIZE_BYTES
}
val nat16 = object: Pattern.Sized<Nat16> {
  override fun read(s: Reader): Nat16 = s.readInt16().uExt()
  override fun write(s: Writer, x: Nat16): Unit = s.writeInt16(x.toShort())
  override val size: Cnt = 16/8
}
const val signBit = 0x0000_8000
internal fun Int16.uExt(): Int32 = if (this < 0) {
  signBit.or(Int16.MAX_VALUE.minus(this).inv())
} else this.toInt()
