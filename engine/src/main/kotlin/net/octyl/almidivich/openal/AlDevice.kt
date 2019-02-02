package net.octyl.almidivich.openal

import com.google.common.base.Preconditions.checkState
import org.lwjgl.openal.ALC10.alcCloseDevice
import org.lwjgl.openal.ALC10.alcCreateContext
import org.lwjgl.openal.ALC10.alcOpenDevice

class AlDevice(val handle: Long) : AutoCloseable {
    companion object {
        fun open(alDeviceName: AlDeviceName) = AlDevice(withAlErrorCheck { alcOpenDevice(alDeviceName.name) })
    }

    fun createContext(vararg attributes: Int): AlContext =
            AlContext(withAlErrorCheck { alcCreateContext(handle, attributes) })

    override fun close() {
        withAlErrorCheck {
            checkState(alcCloseDevice(handle), "Device could not be closed")
        }
    }
}