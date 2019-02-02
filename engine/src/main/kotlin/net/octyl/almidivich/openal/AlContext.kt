package net.octyl.almidivich.openal

import com.google.common.base.Preconditions.checkState
import org.lwjgl.openal.ALC10.alcDestroyContext
import org.lwjgl.openal.ALC10.alcGetCurrentContext
import org.lwjgl.openal.ALC10.alcMakeContextCurrent
import org.lwjgl.system.MemoryUtil.NULL

class AlContext(val handle: Long) : AutoCloseable {

    companion object {
        fun current() = alcGetCurrentContext().takeUnless { it == NULL }?.let(::AlContext)

        fun clearCurrent() = withAlErrorCheck {
            checkState(alcMakeContextCurrent(NULL), "Could not clear context")
        }
    }

    fun makeCurrent() {
        withAlErrorCheck {
            checkState(alcMakeContextCurrent(handle), "Could not make this the current context")
        }
    }

    override fun close() {
        if (alcGetCurrentContext() == handle) {
            clearCurrent()
        }
        withAlErrorCheck {
            alcDestroyContext(handle)
        }
    }

}
