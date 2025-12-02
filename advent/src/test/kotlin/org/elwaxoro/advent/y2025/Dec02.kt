package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 2: Gift Shop
 */
class Dec02: PuzzleDayTester(2, 2025) {

    override fun part1(): Any = loader().countInvalid("""(.+)\1""".toRegex()) == 29818212493

    override fun part2(): Any = loader().countInvalid("""(.+)\1+""".toRegex()) == 37432260594

    private fun List<LongRange>.countInvalid(regex: Regex): Long = map { range -> range.filter { regex.matches("$it") } }.flatten().sum()

    private fun loader() = loadToString().split(",").map { it.split("-").let { LongRange(it[0].toLong(), it[1].toLong()) } }
}
