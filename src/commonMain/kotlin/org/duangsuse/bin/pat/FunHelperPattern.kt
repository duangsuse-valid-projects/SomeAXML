package org.duangsuse.bin.pat

import org.duangsuse.bin.*

// atomic helper patterns that should not inherited in like companion objects

fun <T> mapped(item: Pattern.Sized<T>, map: Map<T, T>) = object: Pattern.Sized<T> {
  private val revMap = map.reverseMap()
  override fun read(s: Reader): T = map.getValue(item.read(s))
  override fun write(s: Writer, x: T): Unit = item.write(s, revMap.getValue(x))
  override val size: Cnt? get() = item.size
}
fun <BIT_FL: BitFlags> bitFlags(creator: (Int32) -> BIT_FL) = object: Pattern.Sized<BIT_FL> {
  override fun read(s: Reader): BIT_FL = creator(s.readInt32())
  override fun write(s: Writer, x: BIT_FL): Unit = s.writeInt32(x.toInt())
  override val size: Cnt = Int32.SIZE_BYTES
}
fun <T> offset(n: Cnt, item: Pattern<T>) = object: Pattern.Sized<Tuple2<Buffer, T>> {
  override fun read(s: Reader): Tuple2<Buffer, T> {
    val savedBuffer = s.asNat8Reader().takeByte(n)
    return Tuple2(savedBuffer, item.read(s))
  }
  override fun write(s: Writer, x: Tuple2<Buffer, T>) {
    s.asNat8Writer().writeFrom(x.first)
    item.write(s, x.second)
  }
  override val size: Cnt? get() = (item as? Pattern.Sized)?.size?.plus(n)
}

inline fun <reified T> Pattern<T>.array(init: T, sizer: Pattern<Cnt>): Pattern<Array<T>>
  = object: Pattern<Array<T>> {
  override fun read(s: Reader): Array<T> {
    val size = sizer.read(s)
    val ary: Array<T> = Array(size) {init}
    for (i in ary.indices) ary[i] = this@array.read(s)
    return ary
  }
  override fun write(s: Writer, x: Array<T>) {
    sizer.write(s, x.size)
    for (item in x) this@array.write(s, item)
  }
}
fun Pattern<Cnt>.sizedByteArray() = object: Pattern<ByteArray> {
  override fun read(s: Reader): ByteArray {
    val n = this@sizedByteArray.read(s)
    return s.asNat8Reader().takeByte(n)
  }
  override fun write(s: Writer, x: ByteArray) {
    this@sizedByteArray.write(s, x.size)
    s.asNat8Writer().writeFrom(x)
  }
}
