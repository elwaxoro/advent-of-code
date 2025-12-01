package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs
import kotlin.math.sign

/**
 * Day 1: Secret Entrance
 */
class Dec01 : PuzzleDayTester(1, 2025) {

    override fun part1(): Any = loader().runningFold(50) { acc, i -> (acc + i) % 100 }.count { it == 0 } == 969

    override fun part2(): Any = loader().let { input ->
        var d = 50
        input.flatMap { i ->
            (1..abs(i)).map {
                d = (d + i.sign) % 100
                d
            }
        }.count { it == 0 }
    } == 5887

    private fun loader() = load().map { it.replace("R", "").replace("L", "-").toInt() }
}
