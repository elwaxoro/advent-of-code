package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.mergeAllOverlap

/**
 * Day 5: Cafeteria
 */
class Dec05 : PuzzleDayTester(5, 2025) {

    override fun part1(): Any = loader().let { (ranges, inputs) -> inputs.count { input -> ranges.any { input in it } } } == 885

    override fun part2(): Any = loader().let { (ranges, _) -> ranges.toMutableSet().mergeAllOverlap().sumOf { it.last - it.first + 1 } } == 348115621205535

    private fun loader() = load(delimiter = "\n\n").let { (ranges, inputs) ->
        ranges.split("\n").map { r ->
            val (start, end) = r.split("-")
            LongRange(start.toLong(), end.toLong())
        } to inputs.split("\n").map(String::toLong)
    }
}
