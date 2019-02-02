package net.octyl.almidivich.stream.input

import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import net.octyl.almidivich.message.MidiMessage


fun MidiInputStream.Companion.of(channel: ReceiveChannel<MidiMessage>): MidiInputStream =
        ChannelMidiInputStream(channel)

private class ChannelMidiInputStream(private val channel: ReceiveChannel<MidiMessage>) : MidiInputStream {
    override suspend fun read(): MidiMessage? {
        return try {
            channel.receive()
        } catch (e: ClosedReceiveChannelException) {
            null
        }
    }

    override fun iterator() = channel.iterator()
}
