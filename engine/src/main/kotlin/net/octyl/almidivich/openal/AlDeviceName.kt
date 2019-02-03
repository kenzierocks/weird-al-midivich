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
