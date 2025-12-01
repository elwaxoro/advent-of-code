package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs
import kotlin.math.sign

/**
 * Day 1: Secret Entrance
 *
 * Insight: it doesn't actually matter if the dial is kept strictly between 0-99, it can be negative as long as application of "% 100" is consistent
 */
class Dec01 : PuzzleDayTester(1, 2025) {

    override fun part1(): Any = loader().spinDial(50).count { it == 0 } == 969

    override fun part2(): Any = loader().fold(50 to 0) { (dial, zeroes), i ->
        List(abs(i)) { i.sign }.spinDial(dial).let { innerSpin ->
            innerSpin.last() to (zeroes + innerSpin.drop(1).count { it == 0 })
        }
    }.second == 5887

    private fun List<Int>.spinDial(initial: Int): List<Int> = runningFold(initial) { acc, i -> (acc + i) % 100 }

    private fun loader() = load().map { it.replace("R", "").replace("L", "-").toInt() }
}
