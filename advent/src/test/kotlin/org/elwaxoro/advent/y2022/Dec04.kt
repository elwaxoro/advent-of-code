package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 4: Camp Cleanup
 */
class Dec04: PuzzleDayTester(4, 2022) {

    /**
     * One range completely overlaps another range if their intersection is exactly the size of the smaller range
     */
    override fun part1(): Any = rangeLoader().count { (a, b) ->
        a.intersect(b).size == minOf(a.count(), b.count())
    }// == 651

    /**
     * Partial overlap is just a non-empty intersection
     */
    override fun part2(): Any = rangeLoader().count { (a, b) ->
        a.intersect(b).isNotEmpty()
    }// == 956

    private fun rangeLoader(): List<List<IntRange>> = load().map { line ->
        line.split(",").map { range ->
            range.split("-").map { it.toInt() }.let { (start, end) ->
                IntRange(start, end)
            }
        }
    }
}
