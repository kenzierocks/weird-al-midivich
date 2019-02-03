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

package net.octyl.almidivich.stream

import com.google.common.collect.ImmutableList
import net.octyl.almidivich.message.MidiMessage
import net.octyl.almidivich.stream.input.MidiInputStream
import net.octyl.almidivich.stream.input.of
import net.octyl.almidivich.stream.output.MidiOutputStream


/**
 * Copies all messages from this stream to the output stream.
 *
 * Calling code is responsible for closing the streams.
 */
suspend inline fun MidiInputStream.copyTo(midiOutputStream: MidiOutputStream) {
    for (msg in this) {
        println("Copying $msg")
        midiOutputStream.write(msg)
        println("Done copying.")
    }
    println("All done!")
    midiOutputStream.flush()
}

/**
 * Reads all messages from the stream.
 *
 * Calling code is responsible for closing the stream.
 */
suspend inline fun MidiInputStream.drain() {
    while (read() != null) {
    }
}

/**
 * Builds an input stream, using the output stream API to provide the components.
 */
inline fun MidiInputStream.Companion.build(block: MidiOutputStream.() -> Unit): MidiInputStream {
    return MidiInputStreamBuilder().apply(block).build()
}

@PublishedApi
internal class MidiInputStreamBuilder : MidiOutputStream {
    private val messages = ImmutableList.builder<MidiMessage>()

    override suspend fun write(message: MidiMessage) {
        messages.add(message)
    }

    fun build() = MidiInputStream.of(messages.build())

}
