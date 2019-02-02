package net.octyl.almidivich.util

val Byte.hex2: String
    get() = String.format("0x%02x", this)
val Int.hex2: String
    get() = String.format("0x%02x", this)