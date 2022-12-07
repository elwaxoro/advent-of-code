package org.elwaxoro.advent.y2019

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.permutations

/**
 * Day 7: Amplification Circuit
 */
class Dec07 : PuzzleDayTester(7, 2019, 79723, 70602018) {

    /**
     * Create all permutations of the list [0,1,2,3,4], then find the max of running each list
     * Running a list by looping each item, feeding output of the previous into the next
     */
    override fun part1(): Any = Dec7Part1Compy(loadToInt(delimiter = ",")).let { compy ->
        (0..4).toList().permutations().maxOf { phaseSettings ->
            phaseSettings.fold(0) { acc, setting ->
                compy.run(listOf(setting, acc))
            }
        }
    }

    private class Dec7Part1Compy(program: List<Int>) : Intercode(program) {
        @OptIn(ExperimentalCoroutinesApi::class)
        fun run(input: List<Int>): Int = runBlocking {
            val output = Channel<Int>(capacity = Channel.UNLIMITED)
            run(input.toChannel(), output, program.toMutableList())
            // compy might output multiple items, just get the last one
            var ret = output.receive()
            while (!output.isClosedForReceive) {
                ret = output.receive()
            }
            ret
        }
    }

    override fun part2(): Any = runBlocking {
        val amps = listOf("A", "B", "C", "D", "E")
        val code = loadToInt(delimiter = ",")
        (5..9).toList().permutations().maxOf { phaseSetting ->
            // kept getting input and output channels mixed up somehow, so lets be extremely methodical here
            val inputMap = mutableMapOf<String, Channel<Int>>()
            val outputMap = mutableMapOf<String, Channel<Int>>()

            // load the output channels
            amps.map { name ->
                outputMap[name] = Channel(capacity = Channel.UNLIMITED)
            }
            // connect the outputs to the inputs
            amps.zipWithNext { x, y ->
                if (x == "A") {
                    inputMap[x] = outputMap["E"]!!
                }
                inputMap[y] = outputMap[x]!!
            }
            // load the initial input values and fire it all up!
            amps.mapIndexed { idx, name ->
                inputMap[name]!!.send(phaseSetting[idx])
                if (name == "A") {
                    inputMap[name]!!.send(0)
                }

                async {
                    // println("Starting $name, input: ${inputMap[name]!!.hashCode()} output: ${outputMap[name]!!.hashCode()}")
                    Intercode(code, name = name).run(inputMap[name]!!, outputMap[name]!!)
                }
            }.map { it.join() } // wait for everyone to finish

            // collect the final amp E output
            outputMap["E"]!!.receive()
        }
    }
}
