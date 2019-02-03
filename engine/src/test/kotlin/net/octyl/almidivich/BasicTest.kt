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

package net.octyl.almidivich

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.octyl.almidivich.message.MidiEvent
import net.octyl.almidivich.message.noteOffMessage
import net.octyl.almidivich.message.noteOnMessage
import net.octyl.almidivich.sequence.InMemoryMidiSequence
import net.octyl.almidivich.stream.copyTo
import net.octyl.almidivich.stream.input.SequenceMidiInputStream
import net.octyl.almidivich.stream.output.JavaSoundMidiOutputStream
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BasicTest {
    @Test
    @DisplayName("can play midi notes")
    fun playMidiNotes() {
        JavaSoundMidiOutputStream.builder()
                .receiver(loadReceiver())
                .build()
                .use { midiOutput ->
                    runBlocking {
                        for (y in 0 until 3) {
                            for (x in 0 until 12) {
                                midiOutput.write(noteOnMessage(0, 69 + x, 64))
                                delay(25)
                                midiOutput.write(noteOnMessage(0, 69 + (11 - x), 64))
                                delay(25)
                                midiOutput.write(noteOffMessage(0, 69 + x - y * 12, 64))
                                delay(25)
                                midiOutput.write(noteOffMessage(0, 69 + (11 - x), 64))
                            }
                        }
                    }
                }
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @Test
    @DisplayName("can play midi notes via sequence")
    fun playMidiSequence() {
        JavaSoundMidiOutputStream.builder()
                .receiver(loadReceiver())
                .build()
                .use { midiOutput ->
                    runBlocking {
                        SequenceMidiInputStream(InMemoryMidiSequence(sequence {
                            for (y in 0 until 3) {
                                for (x in 0 until 12) {
                                    yield(MidiEvent(noteOnMessage(0, 69 + x, 64), 0L))
                                    yield(MidiEvent(noteOnMessage(0, 69 + (11 - x), 64), 25L))
                                    yield(MidiEvent(noteOffMessage(0, 69 + x - y * 12, 64), 25L))
                                    yield(MidiEvent(noteOffMessage(0, 69 + (11 - x), 64), 25L))
                                }
                            }
                        }.toList())).use { source ->
                            source.start()
                            source.copyTo(midiOutput)
                        }
                    }
                }
    }
}


