package org.duangsuse.bin.pat.atom

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.Pattern
import org.duangsuse.bin.pat.uExt

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

val bool8 = object: Pattern.Sized<Boolean> {
  override fun read(s: Reader): Boolean = s.readNat8() != 0
  override fun write(s: Writer, x: Boolean): Unit = s.writeNat8(if (x) 1 else 0)
  override val size: Cnt = 8/Byte.SIZE_BITS
}
val char16 = object: Pattern.Sized<Char> {
  override fun read(s: Reader): Char = s.readInt16().toChar()
  override fun write(s: Writer, x: Char): Unit = s.writeInt16(x.toShort())
  override val size: Cnt = Char.SIZE_BYTES
}
val nat16 = object: Pattern.Sized<Nat16> {
  override fun read(s: Reader): Nat16 = s.readInt16().uExt()
  override fun write(s: Writer, x: Nat16): Unit = s.writeInt16(x.toShort())
  override val size: Cnt = 16/Byte.SIZE_BITS
}
val nat32 = object: Pattern.Sized<Nat32> {
  override fun read(s: Reader): Nat32 = s.readInt32().uExt()
  override fun write(s: Writer, x: Nat32): Unit = s.writeInt32(x.toInt())
  override val size: Cnt = 32/Byte.SIZE_BITS
}