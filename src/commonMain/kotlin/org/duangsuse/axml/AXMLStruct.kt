package org.duangsuse.axml

import org.duangsuse.bin.Int32
import org.duangsuse.bin.pat.*

// AXML = Header StringChunk ResourceChunk XmlContent

// String = (int16 length) (char16[length] content) [char16:0x0000]

// Header = (int32 fileMagic) (int32 fileSize)
class Header: IntTuple(2) {
  companion object: Seq<Header, Int32>(::Header, int32, int32)
  var fileMagic by index(0)
  var fileSize by index(1)
}

// StringChunk = (int32 chunkId) (int32 stringPoolSize) (int32 stringCount) (int32 styleCount) (int32 unknown)
//   (int32 stringPoolOffset) (int32 stylePoolOffset) (int32[stringCount] stringOffsets) (int32[styleCount] styleOffsets)
//   (stringContentStart:String[stringPoolSize] chunkStringContent)
//NOTE stringContentStart = stringPoolOffset+8
//NOTE styleContentStart = stylePoolOffset
//ALIGN StringChunk 4
class StringChunk: AnyTuple(11) {
  companion object: Seq<StringChunk, Any>(::StringChunk, +int32, +int32, +int32, +int32, +int32, +bool8)
  var chunkId by indexOf<Int32>(0)
}

