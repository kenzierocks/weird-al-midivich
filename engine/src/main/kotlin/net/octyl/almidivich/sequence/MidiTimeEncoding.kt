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

package net.octyl.almidivich.sequence

import com.google.common.base.Preconditions.checkState
import java.util.concurrent.TimeUnit
import javax.sound.midi.Sequence

data class MidiTimeEncoding internal constructor(val division: Float, val resolution: Int) {

    /**
     * @return TPQ or 1 under SMPTE
     */
    val ticksPerQuarternote: Int
        get() = if (division == Sequence.PPQ) resolution else 1

    /**
     * Retrieve microseconds per tick. The time per tick may be tempo dependent,
     * if internally stored as ticks per quarter note.
     *
     * @param tempo
     * the current tempo of the track, for properly computing the
     * time, in microseconds per beat
     * @return microsecond length of each tick
     */
    fun getMicrosecondsPerTick(tempo: Int): Int {
        return when (division) {
            Sequence.PPQ ->
                // have res == ticks per beat
                // have tempo == micros per beat
                // want: micros per tick
                // tempo / res == (m/b)*(b/t) == (m/t)
                tempo / resolution
            else ->
                // SMPTE formatted data, tempo has no effect, I think?
                // have div == frames per second
                // have MPS == micros per second
                // have res == ticks per frame
                // want: micros per tick
                // MPS / (div * res) == (m/s)/((f/s)*(t/f)) == (m/s)/(t/s) ==
                // (m/s)*(s/t) == (m/t)
                (MICROS_PER_SEC / (division * resolution)).toInt()
        }
    }

    companion object {

        private val MICROS_PER_SEC = TimeUnit.SECONDS.toMicros(1)

        fun createTpq(tpq: Int): MidiTimeEncoding {
            return createSmpte(Sequence.PPQ, tpq)
        }

        fun createSmpte(smpteDivision: Float, subframes: Int): MidiTimeEncoding {
            checkState(smpteDivision == Sequence.PPQ
                    || smpteDivision == Sequence.SMPTE_24
                    || smpteDivision == Sequence.SMPTE_25
                    || smpteDivision == Sequence.SMPTE_30DROP
                    || smpteDivision == Sequence.SMPTE_30, "invalid smpteDivision %s", smpteDivision)
            return MidiTimeEncoding(smpteDivision, subframes)
        }
    }

}
