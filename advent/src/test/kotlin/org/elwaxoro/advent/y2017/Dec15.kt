package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 15: Dueling Generators
 */
class Dec15 : PuzzleDayTester(15, 2017) {

    val aStart = 722L
    val bStart = 354L
    val aFactor = 16807L
    val bFactor = 48271L
    val divisor = 2147483647L

    override fun part1(): Any = generator(aStart, aFactor).zip(generator(bStart, bFactor)).take(40_000_000).count(::judge)

    override fun part2(): Any = generator(aStart, aFactor, 4L).zip(generator(bStart, bFactor, 8L)).take(5_000_000).count(::judge)

    /**
     * part 1 was easy, part 2 just added the filter
     * lucked out using Long from the start instead of Int
     */
    fun generator(start: Long, factor: Long, filter: Long = 1) = generateSequence(start) { prev ->
        (prev * factor) % divisor
    }.drop(1).filter { it % filter == 0L }

    /**
     * only tweak really needed here from part 1 to part 2 was an efficient way to compare the bottom 16 bits
     */
    fun judge(pair: Pair<Long, Long>): Boolean = (pair.first and 0xFFFFL) == (pair.second and 0xFFFFL)
}
