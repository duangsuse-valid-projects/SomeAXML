package org.duangsuse.bin

/** Natural number, Reference to JDK 's InputStream */
typealias Nat8 = Int
typealias Nat16 = Int

typealias Int8 = Byte
typealias Int16 = Short
typealias Int32 = Int
typealias Int64 = Long

/** Relational number */
typealias Rat32 = Float
typealias Rat64 = Double

typealias Buffer = ByteArray

typealias Idx = Int
typealias IdxRange = IntRange
typealias Cnt = Int
typealias Producer<R> = () -> R
typealias ActionOn<T, R> = T.() -> R
