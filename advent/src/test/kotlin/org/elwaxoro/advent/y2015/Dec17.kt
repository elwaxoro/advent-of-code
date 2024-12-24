package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.combinations

/**
 * Day 17: No Such Thing as Too Much
 */
class Dec17 : PuzzleDayTester(17, 2015) {
    override fun part1(): Any = loader().let { c -> (1..c.size).sumOf { c.comboNog(it) } }
    override fun part2(): Any = loader().let { c -> (1..c.size).firstNotNullOf { l -> c.comboNog(l).takeIf { it > 0 } } }

    private fun List<Int>.comboNog(len: Int): Int = combinations(len).filter { it.sum() == 150 }.count()
    private fun loader() = load().map { it.toInt() }
}
