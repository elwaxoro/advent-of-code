package org.elwaxoro.advent.y2019

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import org.elwaxoro.advent.splitToInt
import java.lang.IllegalStateException

/**
 * Intercode computer!
 * Created Dec02
 * Expanded Dec05 with input / output lists
 * Expanded Dec07 with coroutines and I/O channels to replace input / output lists
 */
@OptIn(DelicateCoroutinesApi::class)
open class Intercode(val program: List<Int>, val name: String = "Compy") {

    suspend fun run(input: Channel<Int>, output: Channel<Int>, codes: MutableList<Int> = program.toMutableList()) {
        // println("$name: started")
        var idx = 0
        while (codes[idx] != 99) {
            val codeParts = codes[idx].toString().splitToInt()

            when (codeParts.takeLast(2).joinToString("").toInt()) {
                1 -> { // param 1 + param 2, store in param 3 value's address
                    codes[codes[idx + 3]] = codes.mg(codeParts.mode(1), idx + 1) + codes.mg(codeParts.mode(2), idx + 2)
                    idx += 4
                }

                2 -> { // param 1 * param 2, store in param 3 value's address
                    codes[codes[idx + 3]] = codes.mg(codeParts.mode(1), idx + 1) * codes.mg(codeParts.mode(2), idx + 2)
                    idx += 4
                }

                3 -> { // read input, store in param 1 value's address
                    if (input.isClosedForReceive) {
                        println("$name: Error! can't read input from closed channel (no data remains)")
                    } else {
                        val read = input.receive()
                        codes[codes[idx + 1]] = read
                        // println("$name: read $read")
                    }
                    idx += 2
                }

                4 -> { // write output to param 1's address or value's address, depending on mode
                    val write = codes.mg(codeParts.mode(1), idx + 1)
                    output.send(write)
                    // println("$name: write $write")
                    idx += 2
                }

                5 -> { // jump if true: param 1 nonzero, set idx to value of param 2 (no auto advance idx)
                    if (codes.mg(codeParts.mode(1), idx + 1) != 0) {
                        idx = codes.mg(codeParts.mode(2), idx + 2)
                    } else {
                        idx += 3
                    }
                }

                6 -> { // jump if false: param 1 zero, set idx to value of param 2 (no auto advance idx)
                    if (codes.mg(codeParts.mode(1), idx + 1) == 0) {
                        idx = codes.mg(codeParts.mode(2), idx + 2)
                    } else {
                        idx += 3
                    }
                }

                7 -> { // less than: if param 1 < param 2 store 1 in param 3, else store 0
                    if (codes.mg(codeParts.mode(1), idx + 1) < codes.mg(codeParts.mode(2), idx + 2)) {
                        codes[codes[idx + 3]] = 1
                    } else {
                        codes[codes[idx + 3]] = 0
                    }
                    idx += 4
                }

                8 -> { // equals: if param 1 == param 2, store 1 in param 3, else store 0
                    if (codes.mg(codeParts.mode(1), idx + 1) == codes.mg(codeParts.mode(2), idx + 2)) {
                        codes[codes[idx + 3]] = 1
                    } else {
                        codes[codes[idx + 3]] = 0
                    }
                    idx += 4
                }

                else -> throw IllegalStateException("Unknown opcode ${codes[idx]} at idx $idx! Full codes: $codes")
            }
        }
        output.close()
        // println("$name: completed")
    }

    /**
     * Two modes: position mode and immediate mode
     * Position mode: read from codes[idx] as a reference to another position (get value, then go to that idx instead ie: codes[codes[idx]])
     * Immediate mode: read from codes[idx] as a value (get value only)
     */
    private fun List<Int>.mode(argNum: Int): Int =
        when (argNum) {
            1 -> size - 3
            2 -> size - 4
            3 -> size - 5
            4 -> size - 6
            else -> throw IllegalStateException("Tried to get argnum $argNum from $this but that number seems to high?")
        }.let { idx ->
            if (idx < 0) {
                0
            } else {
                this[idx]
            }
        }

    private fun MutableList<Int>.mg(mode: Int, idx: Int): Int =
        when (mode) {
            0 -> this[this[idx]] // position mode
            1 -> this[idx] // immediate mode
            else -> throw IllegalStateException("Unknown mode $mode for idx $idx")
        }
}
