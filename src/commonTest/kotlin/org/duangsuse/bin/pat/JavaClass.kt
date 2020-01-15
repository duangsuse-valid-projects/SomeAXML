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

class ClassFile: AnyTuple(2) {
  companion object: BigEndian<ClassFile>(Seq(::ClassFile,
    +(nat32 magic 0xcafebabeL),
    +Version
  ))
  class Version: IntTuple(2) {
    companion object: Seq<Version, Nat16>(::Version,
      u2, u2)
    val minorVersion by index(0)
    val majorVersion by index(1)
  }
}
