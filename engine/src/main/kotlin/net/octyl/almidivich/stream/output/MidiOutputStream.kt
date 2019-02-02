package net.octyl.almidivich.stream.output

import net.octyl.almidivich.message.MidiMessage

/**
 * Represents an output stream of MIDI messages.
 */
interface MidiOutputStream : AutoCloseable {

    /**
     * Write a MIDI message out.
     */
    suspend fun write(message: MidiMessage)

    /**
     * Flush the channel.
     */
    fun flush() {}

    /**
     * Close the channel.
     *
     * Will also [flush] the channel.
     */
    override fun close() {}

}
