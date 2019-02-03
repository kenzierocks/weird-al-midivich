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

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkPositionIndex
import net.octyl.almidivich.message.StatusByte.noteOff
import net.octyl.almidivich.message.StatusByte.noteOn

data class MidiMessage internal constructor(val raw: ByteArray, val type: MidiMessageType) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MidiMessage

        if (!raw.contentEquals(other.raw)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = raw.contentHashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString() = type.toString(raw)
}


fun normalMessage(status: Byte, data: ByteArray, length: Int = data.size) =
        midiMessage(MidiMessageType.NORMAL, status, data, length)

fun noteOnMessage(channel: Int, note: Int, velocity: Int) =
        normalMessage(noteOn(channel), ByteArray(2).also {
            it[0] = note.toByte()
            it[1] = velocity.toByte()
        })

fun noteOffMessage(channel: Int, note: Int, velocity: Int) =
        normalMessage(noteOff(channel), ByteArray(2).also {
            it[0] = note.toByte()
            it[1] = velocity.toByte()
        })

fun sysexMessage(status: Byte, data: ByteArray, length: Int = data.size) =
        midiMessage(MidiMessageType.SYSEX, status, data, length)

fun metaMessage(status: Byte, data: ByteArray, length: Int = data.size) =
        midiMessage(MidiMessageType.META, status, data, length)

fun midiMessage(type: MidiMessageType, status: Byte, data: ByteArray, length: Int = data.size): MidiMessage {
    checkPositionIndex(length, data.size, "length")
    checkArgument(type.validStatusByte(status), "status must be one of %s", type.validStatusDescription)

    return midiMessageUnchecked(ByteArray(length + 1).also {
        it[0] = status
        data.copyInto(it, destinationOffset = 1, endIndex = length)
    }, type)
}

/**
 * Unsafe constructor. Bytes are not checked for validity.
 * This is primarily intended for real-time play support.
 */
fun midiMessageUnchecked(raw: ByteArray, type: MidiMessageType) = MidiMessage(raw, type)
