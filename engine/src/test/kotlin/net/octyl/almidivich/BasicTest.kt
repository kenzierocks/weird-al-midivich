package net.octyl.almidivich

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.octyl.almidivich.message.noteOffMessage
import net.octyl.almidivich.message.noteOnMessage
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
                        for (x in 0 until 10) {
                            midiOutput.write(
                                    noteOnMessage(0, 69, 64)
                            )
                            delay(250)
                            midiOutput.write(
                                    noteOffMessage(0, 69, 64)
                            )
                        }
                    }
                }
    }

}

