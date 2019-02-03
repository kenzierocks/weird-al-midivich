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

import com.google.common.base.Preconditions.checkState
import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select
import net.octyl.almidivich.message.MidiEvent
import net.octyl.almidivich.message.MidiMessage
import net.octyl.almidivich.sequence.MidiSequence
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Takes a [MidiSequence] and plays it back. This means events will not be emitted
 * from [read] until their tick is reached.
 *
 * The sequence can only be played once with this stream.
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class SequenceMidiInputStream(val sequence: MidiSequence) : MidiInputStream {
    // dispatcher + scope for actor coroutine
    private val dispatcher = Executors.newSingleThreadExecutor(ThreadFactoryBuilder()
            .setNameFormat("sequence-midi-input-stream-%d")
            .setDaemon(true)
            .build())
    private val scope = CoroutineScope(dispatcher.asCoroutineDispatcher()
            + CoroutineName(this::class.java.name))

    // Event queue -- when actor produces events, they go here.
    private var messageQueue: Channel<MidiMessage> = Channel(100)
    private lateinit var messageProducer: SendChannel<SmisCommand>

    override suspend fun read(): MidiMessage? {
        if (messageQueue.isClosedForReceive) {
            return null
        }
        return messageQueue.receive()
    }

    suspend fun start() {
        getMessageProducer().send(SmisCommand.START)
    }

    suspend fun pause() {
        getMessageProducer().send(SmisCommand.PAUSE)
    }

    private fun getMessageProducer(): SendChannel<SmisCommand> {
        if (!this::messageProducer.isInitialized) {
            messageProducer = scope.actor {
                SmisMessageProducer(this, sequence.channel, messageQueue).produceEvents()
            }
        }
        return messageProducer
    }

    override fun close() {
        messageProducer.close()
        dispatcher.shutdown()
    }
}

private class SmisMessageProducer @ObsoleteCoroutinesApi constructor(
        private val cmds: ReceiveChannel<SmisCommand>,
        private val events: ReceiveChannel<MidiEvent>,
        private val outQueue: SendChannel<MidiMessage>
) {
    companion object {
        private const val ACCURATE_SLEEP_MIN = 5
    }

    private var state: SmisCommand = SmisCommand.PAUSE
    private var lastMillis: Long = Long.MIN_VALUE

    @ExperimentalCoroutinesApi
    suspend fun produceEvents() {
        try {
            while (!cmds.isClosedForReceive && !events.isClosedForReceive) {
                select<Unit> {
                    cmds.onReceive {
                        handleCommand(it)
                    }
                    if (state == SmisCommand.START) {
                        events.onReceive {
                            handleEvent(it)
                        }
                    }
                }
            }
            outQueue.close()
        } catch (e: Throwable) {
            outQueue.close(e)
        }
    }

    private suspend fun handleEvent(event: MidiEvent) {
        waitForEventTick(event)
        // update tick for event
        lastMillis = millisTimeMonotonic()
        // post message
        outQueue.send(event.message)
    }

    private suspend fun waitForEventTick(event: MidiEvent) {
        checkState(lastMillis != Long.MIN_VALUE, "event handling while not STARTed")
        // sleep until done
        val currentTime = millisTimeMonotonic()
        val targetTime = lastMillis + event.tick
        val waitTimeMillis = max(targetTime - currentTime, 0)
        if (waitTimeMillis > ACCURATE_SLEEP_MIN) {
            delay(waitTimeMillis)
        }
        // spin-sleep
        while (targetTime > millisTimeMonotonic()) {
            Thread.yield()
        }
    }

    private fun handleCommand(cmd: SmisCommand) {
        when (cmd) {
            SmisCommand.START -> {
                lastMillis = millisTimeMonotonic()
                state = SmisCommand.START
            }
            SmisCommand.PAUSE -> {
                state = SmisCommand.PAUSE
            }
        }
    }

    private fun millisTimeMonotonic() = TimeUnit.NANOSECONDS.toMillis(System.nanoTime())
}

private enum class SmisCommand {
    START,
    PAUSE,
}
