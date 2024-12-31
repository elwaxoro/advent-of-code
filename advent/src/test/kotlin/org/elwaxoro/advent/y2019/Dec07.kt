package org.elwaxoro.advent.y2019

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.permutations

/**
 * Day 7: Amplification Circuit
 */
class Dec07 : PuzzleDayTester(7, 2019) {

    /**
     * Create all permutations of the list [0,1,2,3,4], then find the max of running each list
     * Running a list by looping each item, feeding output of the previous into the next
     */
    override fun part1(): Any = runBlocking {
        ElfCode(loadToLong(delimiter = ",")).let { elf ->
            (0..4L).permutations().maxOf { phaseSettings ->
                phaseSettings.fold(0L) { acc, setting ->
                    val o = mutableListOf<Long>()
                    val i = mutableListOf(setting, acc)
                    elf.runner({}, { i.removeFirst() }, { o.add(it) })
                    o.last()
                }
            }
        }
    } == 79723L

    override fun part2(): Any = runBlocking {
        val amps = listOf("A", "B", "C", "D", "E")
        val code = loadToLong(delimiter = ",")
        (5..9L).permutations().maxOf { phaseSettings ->
            val inputs = mutableMapOf<String, Channel<Long>>()
            val outputs = mutableMapOf<String, Channel<Long>>()
            // connect the outputs to the inputs
            amps.plus("A").zipWithNext { a, b ->
                outputs[a] = Channel(capacity = Channel.UNLIMITED)
                inputs[b] = outputs.getValue(a)
            }
            // load initial values
            amps.mapIndexed { i, a -> inputs.getValue(a).send(phaseSettings[i]) }
            inputs.getValue("A").send(0)
            // start all the amps, wait for them all to complete
            amps.map { amp -> async { ElfCode(code).runner({}, { inputs.getValue(amp).receive() }, { outputs.getValue(amp).send(it) }) } }.map { it.join() }
            // fetch the final output from amp E
            outputs.getValue("E").receive()
        }
    } == 70602018L
}
