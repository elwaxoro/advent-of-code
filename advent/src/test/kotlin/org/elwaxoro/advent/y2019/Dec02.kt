package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 2: 1202 Program Alarm
 */
class Dec02 : PuzzleDayTester(2, 2019) {

    override fun part1(): Any = runBlocking {
        ElfCode(loadToLong(delimiter = ",")).runner(setup = { prog ->
            prog[1] = 12
            prog[2] = 2
        })
    }[0] == 2782414L

    override fun part2(): Any = runBlocking {
        ElfCode(loadToLong(delimiter = ",")).let { elf ->
            var noun = 0L
            var verb = 0L
            val target = 19690720L
            var output = 0L
            while (output <= target) {
                noun++
                output = elf.runner(setup = { prog ->
                    prog[1] = noun
                    prog[2] = verb
                })[0]
            }
            noun--
            output = 0
            while (output < target) {
                verb++
                output = elf.runner(setup = { prog ->
                    prog[1] = noun
                    prog[2] = verb
                })[0]
            }
            100 * noun + verb
        }
    } == 9820L
}
