package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 1: Chronal Calibration
 */
class Dec01 : PuzzleDayTester(1, 2018) {

    override fun part1(): Any = loadToInt().sum()

    override fun part2(): Any = loadToInt().let { changes ->
        val seen = mutableSetOf<Int>()
        var frequency = 0
        while (true) {
            changes.forEach { change ->
                frequency += change
                if (seen.contains(frequency)) {
                    return frequency
                } else {
                    seen.add(frequency)
                }
            }
        }
    }
}
