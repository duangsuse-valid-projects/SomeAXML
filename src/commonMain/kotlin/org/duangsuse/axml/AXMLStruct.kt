package org.duangsuse.axml

import org.duangsuse.bin.Int32
import org.duangsuse.bin.pat.Tuple

// AXML = Header StringChunk ResourceChunk XmlContent
// Header = (int32 fileMagic) (int32 fileSize)
// StringChunk = (int32 chunkId) (int32 stringPoolSize) (int32 stringCount) (int32 styleCount) (int32 unknown)
//   (int32 stringPoolOffset) (int32 stylePoolOffset) (int32[stringCount] stringOffsets) (int32[styleCount] styleOffsets)
//   (stringContentStart:String[stringPoolSize] chunkStringContent)
//NOTE stringContentStart = stringPoolOffset+8
//NOTE styleContentStart = stylePoolOffset
//ALIGN StringChunk 4
// String = (int16 length) (char16[length] content) [char16:0x0000]

class Header: Tuple<Int32>(2) {
  override val items: Array<Int32> = Array(size) {0}
  var fileMagic: Int32 by index(0)
  var fileSize: Int32 by index(1)
}
