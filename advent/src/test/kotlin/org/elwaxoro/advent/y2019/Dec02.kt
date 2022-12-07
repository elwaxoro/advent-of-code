package org.elwaxoro.advent.y2019

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 2: 1202 Program Alarm
 * Note: abstracted / refactored the intercode computer after Dec05 expanded it
 */
class Dec02 : PuzzleDayTester(2, 2019, 2782414, 9820) {

    override fun part1(): Any = Dec2Compy(loadToInt(delimiter = ",")).run(12, 2)

    /**
     * Inspection of a few manual runs shows:
     * increasing the noun by 1 doubles the output
     * increasing the verb by one increases the output by 1
     * So keep cranking up the noun till it goes over, subtract one and switch to verb for the difference
     */
    override fun part2(): Any {
        val target = 19690720
        val computer = Dec2Compy(loadToInt(delimiter = ","))
        var noun = 1
        var verb = 1
        while (computer.run(noun, verb) < target) {
            noun++
        }
        noun--
        verb = 1 + target - computer.run(noun, verb)
        return 100 * noun + verb
    }

    /**
     * Intercode version for Dec02: manually edit the codes before starting, manually read the first code as the output
     * Intercode continues to expand between puzzles, so mods keep it compatible with this puzzle
     */
    class Dec2Compy(program: List<Int>) : Intercode(program) {
        fun run(noun: Int, verb: Int): Int = runBlocking {
            program.toMutableList().let { codes ->
                // setup
                codes[1] = noun
                codes[2] = verb
                // I/O channels are not used for this
                run(Channel(), Channel(), codes)
                codes[0]
            }
        }
    }
}
