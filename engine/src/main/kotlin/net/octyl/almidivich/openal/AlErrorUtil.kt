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

import org.lwjgl.openal.AL10.AL_INVALID_ENUM
import org.lwjgl.openal.AL10.AL_INVALID_NAME
import org.lwjgl.openal.AL10.AL_INVALID_OPERATION
import org.lwjgl.openal.AL10.AL_NO_ERROR
import org.lwjgl.openal.AL10.AL_OUT_OF_MEMORY
import org.lwjgl.openal.AL10.alGetError

inline fun <R> withAlErrorCheck(block: () -> R): R {
    clearAlError()
    val result = block()
    throwAlError()
    return result
}

fun clearAlError() {
    onAlError { error ->
        throw IllegalStateException("Cleared AL Error: ${decodeAlError(error)} ($error)")
    }
}

fun throwAlError() {
    onAlError { error ->
        throw IllegalStateException("AL Error: ${decodeAlError(error)} ($error)")
    }
}

inline fun <R> onAlError(block: (Int) -> R): R? {
    val error = alGetError()
    if (error != AL_NO_ERROR) {
        block(error)
    }
    return null
}

fun decodeAlError(error: Int): String = when (error) {
    AL_NO_ERROR -> "NO_ERROR"
    AL_INVALID_NAME -> "INVALID_NAME"
    AL_INVALID_ENUM -> "INVALID_ENUM"
    AL_INVALID_OPERATION -> "INVALID_OPERATION"
    AL_OUT_OF_MEMORY -> "OUT_OF_MEMORY"
    else -> "UNKNOWN"
}