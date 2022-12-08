package org.elwaxoro.advent.y2019

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.drainToList
import org.elwaxoro.advent.toChannel

/**
 * Day 5: Sunny with a Chance of Asteroids
 * Note: expanded Intercode with all requirements for today's puzzle
 */
class Dec05 : PuzzleDayTester(5, 2019) {
    override fun part1(): Any = Dec5Compy(loadToInt(delimiter = ",")).run(1) == 11193703
    override fun part2(): Any = Dec5Compy(loadToInt(delimiter = ",")).run(5) == 12410607

    /**
     * Intercode version for Dec05
     * Intercode continues to expand between puzzles, so mods keep it compatible with this puzzle
     * Provide input as singleton list converted to an unlimited channel
     * Output comes as an unlimited channel, but we only care about the last item
     */
    class Dec5Compy(program: List<Int>) : Intercode(program) {

        fun run(input: Int): Int = runBlocking {
            val output = Channel<Int>(capacity = Channel.UNLIMITED)
            run(listOf(input).toChannel(), output, program.toMutableList())
            // compy might output multiple items, just get the last one
            output.drainToList().last()
        }
    }
}
