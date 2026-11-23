package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 5: A Maze of Twisty Trampolines, All Alike
 */
class Dec05 : PuzzleDayTester(5, 2017) {

    override fun part1(): Any = loadToInt().toMutableList().let { jmps ->
        var idx = 0
        var ctr = 0
        while (idx >= 0 && idx < jmps.size) {
            val newIdx = idx + jmps[idx]
            jmps[idx] = jmps[idx] + 1
            idx = newIdx
            ctr++
        }
        ctr
    }

    override fun part2(): Any = loadToInt().toMutableList().let { jmps ->
        var idx = 0
        var ctr = 0
        while (idx >= 0 && idx < jmps.size) {
            val newIdx = idx + jmps[idx]
            if (jmps[idx] >= 3) {
                jmps[idx] = jmps[idx] - 1
            } else {
                jmps[idx] = jmps[idx] + 1
            }
            idx = newIdx
            ctr++
        }
        ctr
    }
}
