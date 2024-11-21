package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rowColSwap

/**
 * Day 6: Signals and Noise
 */
class Dec06: PuzzleDayTester(6, 2016) {

    override fun part1(): Any = load().map { it.toList() }.rowColSwap().map { line ->
        line.groupBy { it }.maxBy { it.value.size }.key
    }.joinToString("")

    override fun part2(): Any = load().map { it.toList() }.rowColSwap().map { line ->
        line.groupBy { it }.minBy { it.value.size }.key
    }.joinToString("")
}
