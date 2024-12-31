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
    override fun part1(): Any = runBlocking {
        val output = mutableListOf<Long>()
        ElfCode(loadToLong(delimiter = ",")).runner (ElfCode.memExpander(500), { 1 }, { output.add(it) })
        output.single()
    } == 3906448201L

    /**
     * Find the distress signal coordinates!
     * Not sure why coordinates are a single number instead of a pair or more but whatever
     */
    override fun part2(): Any = runBlocking {
        val output = mutableListOf<Long>()
        ElfCode(loadToLong(delimiter = ",")).runner (ElfCode.memExpander(500), { 2 }, { output.add(it) })
        output.single()
    } == 59785L
}
