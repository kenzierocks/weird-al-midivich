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