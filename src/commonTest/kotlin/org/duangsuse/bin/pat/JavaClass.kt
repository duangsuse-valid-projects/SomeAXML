package org.duangsuse.bin.pat

import org.duangsuse.bin.*
import org.duangsuse.bin.pat.atom.*
import org.duangsuse.bin.pat.basic.*
import org.duangsuse.bin.pat.extra.*
import org.duangsuse.bin.pat.extra.EndianSwitch.BigEndian

// Java class file format serializer

// u1=nat8; u2=nat16; u4=nat32; u8=int64
typealias U1 = Nat8
private val u1 = nat8
typealias U2 = Nat16
private val u2 = nat16
typealias U4 = Nat32
private val u4 = nat32
typealias U8 = Int64
private val u8 = int64

class ClassFile: AnyTuple(4) {
  companion object: BigEndian<ClassFile>(Seq(::ClassFile,
    +(u4 magic 0xcafebabeL),
    +Version,
    +ConstantInfo.primitiveArray(u2 padding (-1), Pair(-1,0)),
    +Keep // TODO implement rest
  ))
  class Version: IntTuple(2) {
    companion object: Seq<Version, Nat16>(::Version,
      u2, u2)
    var minorVersion by index(0)
    var majorVersion by index(1)
  }
  var magic by indexOf<U4>(0)
  val version by indexOf<Version>(1)
  val constants by indexOf<Array<Pair<Idx, ConstantInfo>>>(2)
}

sealed class ConstantInfo(size: Cnt): AnyTuple(size) {
  companion object: AnyCond(u1,
    +Unknown, +KstUTF8, +Unknown, +ConstantValue.KstInt, +ConstantValue.KstFloat, +ConstantValue.KstLong, +ConstantValue.KstDouble,
    +KstClassRef, +KstString, +KstFieldRef, +KstMethodRef,
    +KstInterfaceMethodRef, +KstNameAndType, +Unknown, +Unknown,
    +KstMethodHandle, +KstMethodType, +Unknown, +KstInvokeDynamic)
  class KstUTF8: ConstantInfo(1) {
    companion object: AnySeq<KstUTF8>(::KstUTF8, +u2.sizedByteArray())
    var rawString by indexOf<ByteArray>(0)
    @ExperimentalStdlibApi
    var value get() = rawString.decodeToString()
      set(s) { rawString = s.encodeToByteArray() }
    @ExperimentalStdlibApi
    override fun toString(): String = value
  }
  sealed class ConstantValue<T>: ConstantInfo(1) {
    abstract var value: T
    override fun toString(): String = "Constant($value)"
    class KstInt : ConstantValue<Int32>() {
      companion object : AnySeq<KstInt>(::KstInt, +int32)
      override var value by indexOf<Int32>(0)
    }
    class KstFloat : ConstantValue<Rat32>() {
      companion object : AnySeq<KstFloat>(::KstFloat, +rat32)
      override var value by indexOf<Rat32>(0)
    }
    class KstLong : ConstantValue<Int64>() {
      companion object : AnySeq<KstLong>(::KstLong, +int64)
      override var value by indexOf<Int64>(0)
    }
    class KstDouble : ConstantValue<Rat64>() {
      companion object : AnySeq<KstDouble>(::KstDouble, +rat64)
      override var value by indexOf<Rat64>(0)
    }
  }
  class KstClassRef: ConstantInfo(1) {
    companion object: AnySeq<KstClassRef>(::KstClassRef, +u2)
    var index by indexOf<U2>(0)
  }
  class KstString: ConstantInfo(1) {
    companion object: AnySeq<KstString>(::KstString, +u2)
    var index by indexOf<U2>(0)
  }
  class KstFieldRef: ConstantInfo(2) {
    companion object: AnySeq<KstFieldRef>(::KstFieldRef, +u2, +u2)
    var refClass by indexOf<U2>(0)
    var refNameType by indexOf<U2>(1)
  }
  class KstMethodRef: ConstantInfo(2) {
    companion object: AnySeq<KstMethodRef>(::KstMethodRef, +u2, +u2)
    var refClass by indexOf<U2>(0)
    var refNameType by indexOf<U2>(1)
  }
  class KstInterfaceMethodRef: ConstantInfo(2) {
    companion object: AnySeq<KstInterfaceMethodRef>(::KstInterfaceMethodRef, +u2, +u2)
    var refClass by indexOf<U2>(0)
    var refNameType by indexOf<U2>(1)
  }
  class KstNameAndType: ConstantInfo(2) {
    companion object: AnySeq<KstNameAndType>(::KstNameAndType, +u2, +u2)
    var refName by indexOf<U2>(0)
    var refDescriptor by indexOf<U2>(1)
  }
  class KstMethodHandle: ConstantInfo(2) {
    companion object: AnySeq<KstMethodHandle>(::KstMethodHandle, +u1, +u2)
    var refKind by indexOf<U1>(0)
    var refIndex by indexOf<U2>(1)
  }
  class KstMethodType: ConstantInfo(1) {
    companion object: AnySeq<KstMethodType>(::KstMethodType, +u2)
    var refDescriptor by indexOf<U2>(0)
  }
  class KstInvokeDynamic: ConstantInfo(2) {
    companion object: AnySeq<KstInvokeDynamic>(::KstInvokeDynamic, +u2, +u2)
    var refBootstrapMethodAttr by indexOf<U2>(0)
    var refNameAndType by indexOf<U2>(1)
  }
}
