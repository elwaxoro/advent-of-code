package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 9: Stream Processing
 */
class Dec09 : PuzzleDayTester(9, 2017) {

    override fun part1(): Any = garbageCollector().first

    override fun part2(): Any = garbageCollector().second

    private fun garbageCollector(): Pair<Int, Int> = loadToString().let { input ->
        var idx = 0
        var stackDepth = 0
        var isGarbage = false
        var score = 0
        var garbageScore = 0
        while (idx < input.length) {
            val next = input[idx]
            if (isGarbage) {
                when (next) {
                    '>' -> isGarbage = false
                    '!' -> idx++
                    else -> garbageScore++
                }
            } else {
                when (input[idx]) {
                    '{' -> stackDepth++
                    '}' -> {
                        score += stackDepth
                        stackDepth--
                    }
                    ',' -> {} // does nothing
                    '<' -> isGarbage = true
                    else -> throw IllegalStateException("Unknown character $next")
                }
            }
            idx++
        }
        score to garbageScore
    }
}
