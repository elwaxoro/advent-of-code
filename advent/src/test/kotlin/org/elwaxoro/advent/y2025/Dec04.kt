package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 4: Printing Department
 */
class Dec04: PuzzleDayTester(4, 2025) {

    override fun part1(): Any = loader().reachable().size == 1416

    override fun part2(): Any = loader().let { rolls ->
        var reduced = rolls
        do {
            val reachable = reduced.reachable()
            reduced = reduced - reachable
        } while (reachable.isNotEmpty())
        rolls.size - reduced.size
    } == 9086

    private fun Set<Coord>.reachable(): Set<Coord> = filter { roll -> roll.neighbors9(includeSelf = false).flatten().count { it in this } < 4 }.toSet()

    private fun loader() = load().mapIndexed { y, row -> row.mapIndexedNotNull { x, c -> Coord(x, y).takeIf { c == '@' } } }.flatten().toSet()
}
