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

import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import net.octyl.almidivich.message.MidiMessageType
import net.octyl.almidivich.message.midiMessageUnchecked
import net.octyl.almidivich.stream.output.JavaSoundMidiOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun loadReceiver(): Receiver {
    val info = MidiSystem.getMidiDeviceInfo()
    info.forEach { i ->
        println("Midi device: ${i.name} (${i.description}) from ${i.vendor}, version ${i.version}")
    }
    return info.mapNotNull {
        val device = MidiSystem.getMidiDevice(it)
        when {
            device.maxReceivers == 0 -> null
            else -> device
        }
    }.first().also {
        println("Using: ${it.deviceInfo.name}")
        it.open()
    }.receiver
}

fun main() {
    val player = MidiSystem.getSequencer()
    player.open()
    JavaSoundMidiOutputStream.builder()
            .receiver(loadReceiver())
            .build()
            .use { midiOutput ->
                val file = JFileChooser().run {
                    isAcceptAllFileFilterUsed = false
                    addChoosableFileFilter(FileNameExtensionFilter("MIDI files", "mid", "midi"))
                    showOpenDialog(null)
                    selectedFile ?: throw IllegalStateException("No file for me?")
                }
                val sequence = MidiSystem.getSequence(file)
                player.sequence = sequence
                ForwardingReceiver(midiOutput).use {
                    player.transmitter.receiver = it
                    player.start()
                    while (player.isRunning) {
                        Thread.sleep(10)
                    }
                    println("Done playing, exiting now.")
                }
            }
    player.close()
}

class ForwardingReceiver(private val midiOutput: JavaSoundMidiOutputStream) : Receiver {
    private val pool = Executors.newCachedThreadPool(ThreadFactoryBuilder()
            .setNameFormat("midi-messages-%d")
            .setDaemon(true)
            .build())
    private val scope = CoroutineScope(pool.asCoroutineDispatcher())

    override fun close() {
        println("Killing pool")
        pool.shutdown()
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
            println("Force killing pool")
            pool.shutdownNow()
            pool.awaitTermination(5, TimeUnit.SECONDS)
        }
        midiOutput.close()
    }

    override fun send(message: MidiMessage, timeStamp: Long) {
        val type = when (message) {
            is ShortMessage -> MidiMessageType.NORMAL
            is SysexMessage -> MidiMessageType.SYSEX
            is MetaMessage -> MidiMessageType.META
            else -> {
                val status = message.status.toByte()
                MidiMessageType.values().first {
                    it.validStatusByte(status)
                }
            }
        }
        if (pool.isShutdown) {
            return
        }
        scope.launch {
            try {
                midiOutput.write(midiMessageUnchecked(
                        message.message,
                        type
                ))
            } catch (e: IllegalStateException) {
                System.err.println("${Thread.currentThread().name}:")
                e.printStackTrace()
            }
        }
    }

}