package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs
import kotlin.math.sign

/**
 * Day 1: Secret Entrance
 *
 * Idea: keep a list of all dial positions, look for zeroes at the end
 * Insight: it doesn't matter if the dial is kept between 0-99
 * Just keep dial position list as a running sum then apply % 100 at the end to count up the zeros
 */
class Dec01 : PuzzleDayTester(1, 2025) {

    override fun part1(): Any = loader().spinDial(50).countZeroes() == 969

    /**
     * for each input - apply it as a repeated list of +1, +1, +1, ... or -1, -1, -1, ...
     */
    override fun part2(): Any = loader().fold(50 to 0) { (dial, zeroes), i ->
        List(abs(i)) { i.sign }.spinDial(dial).let { innerSpin ->
            innerSpin.last() to (zeroes + innerSpin.drop(1).countZeroes())
        }
    }.second == 5887

    private fun List<Int>.spinDial(initial: Int): List<Int> = runningFold(initial) { acc, i -> acc + i }

    private fun List<Int>.countZeroes() = count { it % 100 == 0 }

    private fun loader() = load().map { it.replace("R", "").replace("L", "-").toInt() }
}
