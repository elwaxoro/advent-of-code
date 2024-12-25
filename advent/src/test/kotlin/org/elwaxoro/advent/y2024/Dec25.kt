package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 25: Code Chronicle
 */
class Dec25 : PuzzleDayTester(25, 2024) {

    override fun part1(): Any = loader().let { parts -> parts.sumOf { a -> parts.filter { b -> a.none { b.contains(it) } }.size } / 2 }

    private fun loader() = load(delimiter = "\n\n").let { l -> l.map { a -> a.split("\n").flatMapIndexed { y, s -> s.mapIndexedNotNull { x, c -> Coord(x, y).takeIf { c == '#' } } } } }
}
