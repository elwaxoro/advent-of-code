package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.drainToList

/**
 * Day 9: Sensor Boost
 */
class Dec09: PuzzleDayTester(9, 2019) {

    /**
     * Diagnostic mode: will output a pair if an instruction isn't working, a single value if everything checks out
     * ex: [203, 0] means "read and store" isn't working
     */
    override fun part1(): Any = runTheCompy(1) == 3906448201

    /**
     * Find the distress signal coordinates!
     * Not sure why coordinates are a single number instead of a pair or more but whatever
     */
    override fun part2(): Any = runTheCompy(2) == 59785L

    private fun runTheCompy(input: Long): Long = IntercodeV9(loadToLong(delimiter = ",")).let { compy ->
        runBlocking {
            compy.input.send(input)
            compy.expandMem(500)
            compy.run()
            compy.output.drainToList().first()
        }
    }
}
