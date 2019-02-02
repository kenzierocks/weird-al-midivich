package net.octyl.almidivich.openal

import org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER
import org.lwjgl.openal.ALC10.ALC_DEVICE_SPECIFIER
import org.lwjgl.openal.ALC10.alcGetString
import org.lwjgl.openal.ALC10.alcIsExtensionPresent
import org.lwjgl.openal.ALUtil
import org.lwjgl.system.MemoryUtil.NULL

data class AlDeviceName(val name: String) {
    companion object {
        val default by lazy {
            alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER)?.let(::AlDeviceName)
        }
    }
}

fun alDeviceNames(): List<AlDeviceName> {
    if (!alcIsExtensionPresent(NULL, "ALC_ENUMERATION_EXT")) {
        return listOf()
    }
    return ALUtil.getStringList(NULL, ALC_DEVICE_SPECIFIER)?.map(::AlDeviceName) ?: listOf()
}
