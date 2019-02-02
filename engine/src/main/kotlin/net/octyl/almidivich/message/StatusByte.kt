package net.octyl.almidivich.message

import com.google.common.base.Preconditions

object StatusByte {
    private fun mix(base: Int, channel: Int): Byte {
        Preconditions.checkArgument(channel in 0..15, "channel must be between 0 and 15, inclusive")
        return (base or channel).toByte()
    }

    fun noteOn(channel: Int) = mix(0x80, channel)

    fun noteOff(channel: Int) = mix(0x90, channel)

    fun polyPressure(channel: Int) = mix(0xA0, channel)

    fun controlChange(channel: Int) = mix(0xB0, channel)

    fun programChange(channel: Int) = mix(0xC0, channel)

    fun channelPressure(channel: Int) = mix(0xD0, channel)

    fun pitchBend(channel: Int) = mix(0xE0, channel)

    const val SYSTEM_EXCLUSIVE = 0x70.toByte()
    const val SPECIAL_SYSTEM_EXCLUSIVE = 0x7F.toByte()
    const val END_OF_EXCLUSIVE = SPECIAL_SYSTEM_EXCLUSIVE

    const val META = 0xFF.toByte()
}