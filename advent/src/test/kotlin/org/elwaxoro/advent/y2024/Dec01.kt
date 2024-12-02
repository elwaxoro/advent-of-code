package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs

/**
 * Day 1: Historian Hysteria
 */
class Dec01 : PuzzleDayTester(1, 2024) {

    override fun part1(): Any = loader().let { (a, b) -> a.sorted().zip(b.sorted()).sumOf { abs(it.first - it.second) } }

    override fun part2(): Any = loader().let { (a, b) -> a.sumOf { c -> c * b.count { c == it } } }

    private fun loader() = load().map { it.split(Regex("\\W+")).map { it.toInt() }.let { it[0] to it[1] } }.let { pairs ->
        val a = pairs.map { it.first }
        val b = pairs.map { it.second }
        a to b
    }
}
