package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 5: Sunny with a Chance of Asteroids
 */
class Dec05 : PuzzleDayTester(5, 2019) {

    override fun part1(): Any = runBlocking {
        ElfCode(loadToLong(delimiter = ",")).let { elf ->
            val out = mutableListOf<Long>()
            elf.runner(output = { out.add(it) })
            out.last()
        }
    } == 11193703L

    override fun part2(): Any = runBlocking {
        ElfCode(loadToLong(delimiter = ",")).let { elf ->
            val out = mutableListOf<Long>()
            elf.runner(input = { 5 }, output = { out.add(it) })
            out.last()
        }
    } == 12410607L
}
