/*
 * This file is part of weird-al-midivich, licensed under the MIT License (MIT).
 *
 * Copyright (c) Kenzie Togami <https://octyl.net>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
