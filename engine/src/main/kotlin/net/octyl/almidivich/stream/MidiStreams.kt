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
        midiOutputStream.write(msg)
    }
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
