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

package net.octyl.almidivich.stream.input

import kotlinx.coroutines.channels.ChannelIterator
import net.octyl.almidivich.message.MidiMessage

/**
 * Represents an input stream of MIDI messages.
 */
interface MidiInputStream : AutoCloseable {

    companion object {}

    /**
     * Read the next message, or `null` if there is none.
     */
    suspend fun read(): MidiMessage?

    /**
     * Close the channel.
     */
    override fun close() {}

    operator fun iterator(): ChannelIterator<MidiMessage> = object : ChannelIterator<MidiMessage> {

        private var next: MidiMessage? = null
        private var done: Boolean = false

        override suspend fun hasNext(): Boolean {
            if (done) {
                return false
            }
            if (next == null) {
                next = read()
                if (next == null) {
                    done = true
                    return false
                }
            }
            return true
        }

        override suspend fun next(): MidiMessage {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            return next!!.also { next = null }
        }

    }

}
