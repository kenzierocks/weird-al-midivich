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
            return next!!
        }

    }

}
