package net.octyl.almidivich.stream.input

import net.octyl.almidivich.message.MidiMessage


fun MidiInputStream.Companion.of(midiEvents: Iterator<MidiMessage>): MidiInputStream =
        IteratorMidiInputStream(midiEvents)

fun MidiInputStream.Companion.of(midiEvents: Iterable<MidiMessage>) = of(midiEvents.iterator())

private class IteratorMidiInputStream(private val midiEvents: Iterator<MidiMessage>) : MidiInputStream {

    override suspend fun read() = when {
        midiEvents.hasNext() -> midiEvents.next()
        else -> null
    }

}