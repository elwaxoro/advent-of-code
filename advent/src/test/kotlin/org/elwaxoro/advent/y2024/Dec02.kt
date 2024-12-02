package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.dropAt

/**
 * Day 2: Red-Nosed Reports
 */
class Dec02 : PuzzleDayTester(2, 2024) {

    override fun part1(): Any = loader().filter { it.isSafe() }.size

    override fun part2(): Any = loader().filter { report -> report.isSafe() || report.indices.any { report.dropAt(it).isSafe() } }.size

    private fun List<Int>.isSafe(): Boolean = (this[0] < this[1]).let { increasing -> this.zipWithNext { a, b -> ((b - a).takeIf { increasing } ?: (a - b)) in 1..3 }.all { it } }

    private fun loader() = load().map { line -> line.split(" ").map { it.toInt() } }
}
