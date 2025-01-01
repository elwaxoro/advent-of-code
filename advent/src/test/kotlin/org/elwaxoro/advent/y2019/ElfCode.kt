package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.padTo
import org.elwaxoro.advent.takeSplit

/**
 * Dec 2: added addition, multiplication, setup modification
 * Dec 5: added input, output, jump-if-true, jump-if-false, less-than, equals, position and immediate get modes, converted from Int to Long
 * Dec 7: added async support (suspend functions for input / output to allow channels)
 * Dec 9: added relative mode, expanding memory (use setup function to expand manually)
 * Dec 11: added optional exit support, in case input/output are channels
 * Dec 15: added custom input spike to terminate early (Long.MAX_VALUE)
 */
class ElfCode(
    private val originalProgram: List<Long>,
    private var relativeBase: Long = 0,
    private var modes: String = "000",
    private var prog: MutableList<Long> = mutableListOf(),
    private var idx: Long = 0,
) {

    companion object {
        fun memExpander(size: Int): (program: MutableList<Long>) -> Unit = { it.addAll(listOf(0L).padTo(size)) }
    }

    private fun reset() {
        prog = originalProgram.toMutableList()
        modes = "000"
        idx = 0L
    }

    suspend fun runner(
        setup: (program: MutableList<Long>) -> Unit = { },
        input: suspend () -> Long = { 1 },
        output: suspend (out: Long) -> Unit = { println("Output: $it") },
        exit: suspend () -> Unit = { },
    ): List<Long> {
        reset()
        setup.invoke(prog)
        var isRunning = true
        while (isRunning) {
            val (m, op) = "${prog[idx.toInt()]}".padStart(5, padChar = '0').takeSplit(3)
            modes = m
            // println("modified $idx = ${prog[idx.toInt()]} into op $op with modes $modes $prog")
            when (op.toInt()) {
                1 -> { // addition
                    put(3, get(1) + get(2))
                    idx += 4
                }

                2 -> { // multiplication
                    put(3, get(1) * get(2))
                    idx += 4
                }

                3 -> { // input
                    val i = input.invoke()
                    if (i == Long.MAX_VALUE) {
                        // custom input break
                        isRunning = false
                    } else {
                        put(1, i)
                        idx += 2
                    }
                }

                4 -> { // output
                    output.invoke(get(1))
                    idx += 2
                }

                5 -> { // jump-if-true
                    if (get(1) != 0L) {
                        idx = get(2)
                    } else {
                        idx += 3
                    }
                }

                6 -> { // jump-if-false
                    if (get(1) == 0L) {
                        idx = get(2)
                    } else {
                        idx += 3
                    }
                }

                7 -> { // less than
                    if (get(1) < get(2)) {
                        put(3, 1)
                    } else {
                        put(3, 0)
                    }
                    idx += 4
                }

                8 -> { // equals
                    if (get(1) == get(2)) {
                        put(3, 1)
                    } else {
                        put(3, 0)
                    }
                    idx += 4
                }

                9 -> { // relative base change
                    relativeBase += get(1)
                    idx += 2
                }

                99 -> {
                    isRunning = false
                }

                else -> throw IllegalStateException("Unknown opcode $op with mode $modes at idx $idx! Full prog: $prog")
            }
        }
        exit.invoke()
        return prog
    }

    private fun get(param: Int): Long = prog[mode(modes, param, idx + param)]
    private fun put(param: Int, value: Long) {
        prog[mode(modes, param, idx + param)] = value
    }

    private fun mode(modes: String, modeIdx: Int, codeIdx: Long): Int = mode(modes[modes.length - modeIdx].digitToInt(), codeIdx)

    private fun mode(mode: Int, idx: Long): Int =
        when (mode) {
            0 -> prog[idx.toInt()].toInt() // position mode
            1 -> idx.toInt() // immediate mode
            2 -> (prog[idx.toInt()] + relativeBase).toInt() // relative mode
            else -> throw IllegalStateException("unknown mode $mode for idx $idx")
        }
}
