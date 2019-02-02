package net.octyl.almidivich.stream.output

import net.octyl.almidivich.message.MidiMessage
import net.octyl.almidivich.openal.AlDevice
import net.octyl.almidivich.openal.AlDeviceName

class AlMidiOutputStream private constructor(
        private val alDevice: AlDevice
) : MidiOutputStream {

    companion object {
        fun builder() = Builder()
    }

    class Builder internal constructor() {

        private var alDeviceName: AlDeviceName? = AlDeviceName.default

        fun alDeviceName(alDeviceName: AlDeviceName): Builder = apply { this.alDeviceName = alDeviceName }

        fun build(): AlMidiOutputStream {
            val alDeviceName = checkNotNull(alDeviceName) { "alDevice is required" }

            return AlMidiOutputStream(AlDevice.open(alDeviceName))
        }

    }

    // TODO - Implement SF2 or similar, with copying to OpenAL.

    override suspend fun write(message: MidiMessage) {
        TODO("No sound sources available.")
    }

    override fun close() {
        alDevice.close()
    }

}