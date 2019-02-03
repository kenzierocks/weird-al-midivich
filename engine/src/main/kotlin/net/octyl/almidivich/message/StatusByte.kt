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

package net.octyl.almidivich.message

import com.google.common.base.Preconditions

object StatusByte {
    private fun mix(base: Int, channel: Int): Byte {
        Preconditions.checkArgument(channel in 0..15, "channel must be between 0 and 15, inclusive")
        return (base or channel).toByte()
    }

    fun noteOff(channel: Int) = mix(0x80, channel)

    fun noteOn(channel: Int) = mix(0x90, channel)

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