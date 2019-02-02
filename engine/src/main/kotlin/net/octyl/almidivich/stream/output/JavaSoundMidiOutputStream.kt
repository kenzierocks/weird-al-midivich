package net.octyl.almidivich.stream.output

import net.octyl.almidivich.message.MidiMessage
import net.octyl.almidivich.message.MidiMessageType
import javax.sound.midi.MetaMessage
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage

/**
 * Simple MIDI output stream that relies on the Java Sound MIDI system.
 */
class JavaSoundMidiOutputStream(
        private val receiver: Receiver
) : MidiOutputStream {

    companion object {
        fun builder() = Builder()
    }

    class Builder internal constructor() {
        private var receiver: Receiver? = null

        fun receiver(receiver: Receiver): Builder = apply { this.receiver = receiver }

        fun build(): JavaSoundMidiOutputStream {
            val receiver = checkNotNull(receiver) { "receiver cannot be null" }

            return JavaSoundMidiOutputStream(receiver)
        }

    }

    override suspend fun write(message: MidiMessage) {
        receiver.send(message.toJava(), -1)
    }

    override fun close() {
        receiver.close()
    }

}

private fun MidiMessage.toJava(): javax.sound.midi.MidiMessage {
    return when (type) {
        MidiMessageType.NORMAL -> ShortMessage(
                raw[0].toUByteToInt(),
                (raw.getOrNull(1)?.toUByteToInt() ?: 0),
                (raw.getOrNull(2)?.toUByteToInt() ?: 0))
        MidiMessageType.SYSEX -> SysexMessage(
                raw[0].toUByteToInt(), raw.copyOfRange(1, raw.size), raw.size - 1)
        MidiMessageType.META -> MetaMessage(
                raw[0].toUByteToInt(), raw.copyOfRange(1, raw.size), raw.size - 1)
    }
}

/**
 * Treat this byte value as an unsigned value before converting to [Int].
 */
private fun Byte.toUByteToInt() = toInt() and 0xFF
