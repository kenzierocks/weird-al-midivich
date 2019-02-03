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

package net.octyl.almidivich.stream.output

import net.octyl.almidivich.message.MidiMessage
import net.octyl.almidivich.message.MidiMessageType
import javax.sound.midi.MetaMessage
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage

/**
 * Simple MIDI output stream that relies on the Java Sound MIDI system.
 */
class JavaSoundMidiOutputStream(
        private val receiver: Receiver
) : MidiOutputStream {

    companion object {
        fun builder() = Builder()
    }

    class Builder internal constructor() {
        private var receiver: Receiver? = null

        fun receiver(receiver: Receiver): Builder = apply { this.receiver = receiver }

        fun build(): JavaSoundMidiOutputStream {
            val receiver = checkNotNull(receiver) { "receiver cannot be null" }

            return JavaSoundMidiOutputStream(receiver)
        }

    }

    override suspend fun write(message: MidiMessage) {
        receiver.send(message.toJava(), -1)
    }

    override fun close() {
        receiver.close()
    }

}

private fun MidiMessage.toJava(): javax.sound.midi.MidiMessage {
    return when (type) {
        MidiMessageType.NORMAL -> ShortMessage(
                raw[0].toUByteToInt(),
                (raw.getOrNull(1)?.toUByteToInt() ?: 0),
                (raw.getOrNull(2)?.toUByteToInt() ?: 0))
        MidiMessageType.SYSEX -> SysexMessage(
                raw[0].toUByteToInt(), raw.copyOfRange(1, raw.size), raw.size - 1)
        MidiMessageType.META -> MetaMessage(
                raw[0].toUByteToInt(), raw.copyOfRange(1, raw.size), raw.size - 1)
    }
}

/**
 * Treat this byte value as an unsigned value before converting to [Int].
 */
private fun Byte.toUByteToInt() = toInt() and 0xFF
