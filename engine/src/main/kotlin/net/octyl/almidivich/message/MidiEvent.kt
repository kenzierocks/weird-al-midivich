package net.octyl.almidivich.message

/**
 * A MidiEvent represents the position of a MidiMessage on a track, in ticks.
 *
 * Events are sorted by tick, message is not taken into account.
 */
data class MidiEvent(val message: MidiMessage, val tick: Long) : Comparable<MidiEvent> {
    override fun compareTo(other: MidiEvent) = tick.compareTo(other.tick)
}

fun MidiMessage.at(tick: Long) = MidiEvent(this, tick)
