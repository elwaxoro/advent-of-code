package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.parseCoords

/**
 * Day 25: Code Chronicle
 */
class Dec25 : PuzzleDayTester(25, 2024) {

    override fun part1(): Any = loader().let { parts -> parts.sumOf { a -> parts.filter { b -> a.none { b.contains(it) } }.size } / 2 }

    override fun part2(): Any = "Merry Christmas!"

    private fun loader() = load(delimiter = "\n\n").let { l -> l.map { a -> a.split("\n").parseCoords(filter = setOf('.')) } }
}
