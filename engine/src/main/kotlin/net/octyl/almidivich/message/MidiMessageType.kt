/*
 * This file is part of weird-al-midivich, licensed under the MIT License (MIT).
 *
 * Copyright (c) Kenzie Togami <https://octyl.net>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.octyl.almidivich.message

import net.octyl.almidivich.message.StatusByte.channelPressure
import net.octyl.almidivich.message.StatusByte.controlChange
import net.octyl.almidivich.message.StatusByte.noteOff
import net.octyl.almidivich.message.StatusByte.noteOn
import net.octyl.almidivich.message.StatusByte.pitchBend
import net.octyl.almidivich.message.StatusByte.polyPressure
import net.octyl.almidivich.message.StatusByte.programChange
import net.octyl.almidivich.util.hex2

enum class MidiMessageType(internal val validStatusByte: (Byte) -> Boolean,
                           internal val validStatusDescription: String) {
    NORMAL((0..15).flatMap { c ->
        listOf(noteOn(c),
                noteOff(c),
                polyPressure(c),
                controlChange(c),
                programChange(c),
                channelPressure(c),
                pitchBend(c))
    }.toSet(), desc = "0x8x to 0xEx") {
        override fun toString(data: ByteArray): String {
            val channel = (data[0].toInt() and 0x0F).hex2
            val data1 = data.getOrNull(1)?.hex2
            val data2 = data.getOrNull(2)?.hex2
            return when (data[0].toInt() and 0xF0) {
                0x80 -> "noteOff[channel=$channel,note=$data1,velocity=$data2]"
                0x90 -> "noteOn[channel=$channel,note=$data1,velocity=$data2]"
                0xA0 -> "polyPressure[channel=$channel,note=$data1,pressure=$data2]"
                0xB0 -> "controlChange[channel=$channel,controller=$data1,value=$data2]"
                0xC0 -> "programChange[channel=$channel,patch=$data1]"
                0xD0 -> "channelPressure[channel=$channel,pressure=$data1]"
                0xE0 -> {
                    val value = data[1].toInt() and (data[2].toInt() shl 7)
                    "pitchBend[channel=$channel,value=$value]"
                }
                else -> "unknown[data=${data.contentToString()}]"
            }
        }
    },
    SYSEX(StatusByte.SYSTEM_EXCLUSIVE, StatusByte.SPECIAL_SYSTEM_EXCLUSIVE) {
        override fun toString(data: ByteArray) =
                "sysex[data=${data.contentToString()}]"
    },
    META(StatusByte.META) {
        override fun toString(data: ByteArray) =
                "meta[data=${data.contentToString()}]"
    },
    ;

    constructor(vararg validStatusBytes: Byte) : this(setOf<Byte>(*validStatusBytes.toTypedArray()))

    constructor(validStatusBytes: Set<Byte>, desc: String = validStatusBytes.toString()) : this(
            validStatusBytes::contains,
            desc
    )

    abstract fun toString(data: ByteArray): String
}