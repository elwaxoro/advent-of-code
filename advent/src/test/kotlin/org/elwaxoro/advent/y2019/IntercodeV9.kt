package org.elwaxoro.advent.y2019

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import org.elwaxoro.advent.padTo
import org.elwaxoro.advent.splitToInt
import java.lang.IllegalStateException

/**
 * Intercode computer VERSION 9!!!!
 * Created Dec02
 * Expanded Dec05 with input / output lists
 * Expanded Dec07 with coroutines and I/O channels to replace input / output lists
 * Rewritten Dec09 with relative access and expandable memory
 *
 * NOTE: this version can only be run once after construction
 */
@OptIn(ExperimentalCoroutinesApi::class)
open class IntercodeV9(
    val initialMem: List<Long>,
    val name: String = "Compy",
    val input: Channel<Long> = Channel(capacity = Channel.UNLIMITED),
    val output: Channel<Long> = Channel(capacity = Channel.UNLIMITED)
) {

    var mem: MutableList<Long> = initialMem.toMutableList()
    var idx = 0
    var relativeBase = 0
    var instruction: Int = 0
    var insParts = listOf<Int>()

    fun expandMem(target: Int, value: Long = 0) {
        mem.addAll(listOf(value).padTo(target))
    }

    /**
     * mem get: refer to the split bits of the current instruction for what mode to use
     * then get the value from memory
     */
    private fun mg(param: Int): Long = mem[mw(param)]

    /**
     * Mem write: refer to the split bits of the current instruction for what mode to use
     * then get the index that should be used
     */
    private fun mw(param: Int): Int =
        when (insParts.mode(param)) {
            0 -> mem[idx + param].int() // position mode
            1 -> idx + param // immediate mode
            2 -> mem[idx + param].int() + relativeBase // relative mode
            else -> throw IllegalStateException("Unknown mode for idx $idx")
        }

    /**
     * Int converter but double checks if the value is getting compressed or not
     */
    private fun Long.int(): Int {
        if (this >= Int.MAX_VALUE) {
            println("DANGER! casting $this to int, will truncate to ${this.toInt()}")
        }
        return this.toInt()
    }

    suspend fun run() {
        while (true) {
            //println(mem.joinToString(","))
            instruction = mem[idx].int()
            insParts = instruction.toString().splitToInt()
            val insCode = insParts.takeLast(2).joinToString("").toInt()
            //println("process: $instruction ($insCode) at idx $idx [$relativeBase]")

            when (insCode) {
                1 -> { // param 1 + param 2, store in param 3
                    mem[mw(3)] = mg(1) + mg(2)
                    idx += 4
                }

                2 -> { // param 1 * param 2, store in param 3
                    mem[mw(3)] = mg(1) * mg(2)
                    idx += 4
                }

                3 -> { // read input, store in param 1 value's address
                    if (input.isClosedForReceive) {
                        println("$name: Error! can't read input from closed channel (no data remains)")
                    } else {
                        val read = input.receive()
                        mem[mw(1)] = read
                        //println("$name: read $read")
                    }
                    idx += 2
                }

                4 -> { // write output to param 1's address or value's address, depending on mode
                    val write = mg(1)
                    output.send(write)
                    //println("$name: write $write")
                    idx += 2
                }

                5 -> { // jump if true: param 1 nonzero, set idx to value of param 2 (no auto advance idx)
                    if (mg(1) != 0L) {
                        idx = mg(2).int()
                    } else {
                        idx += 3
                    }
                }

                6 -> { // jump if false: param 1 zero, set idx to value of param 2 (no auto advance idx)
                    if (mg(1) == 0L) {
                        idx = mg(2).int()
                    } else {
                        idx += 3
                    }
                }

                7 -> { // less than: if param 1 < param 2 store 1 in param 3, else store 0
                    if (mg(1) < mg(2)) {
                        mem[mw(3)] = 1
                    } else {
                        mem[mw(3)] = 0
                    }
                    idx += 4
                }

                8 -> { // equals: if param 1 == param 2, store 1 in param 3, else store 0
                    if (mg(1) == mg(2)) {
                        mem[mw(3)] = 1
                    } else {
                        mem[mw(3)] = 0
                    }
                    idx += 4
                }

                9 -> { // relative base modifier: param 1 is added to the current base
                    relativeBase += mg(1).int()
                    idx += 2
                }

                99 -> {
                    break
                }

                else -> throw IllegalStateException("Unknown opcode ${mem[idx]} at idx $idx! Full codes: $mem")
            }
        }
        output.close()
        //println("$name: completed")
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
}
