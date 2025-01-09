package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.*

/**
 * Day 24: Planet of Discord
 */
class Dec24 : PuzzleDayTester(24, 2019) {

    override fun part1(): Any = loader().let { bugs ->
        val bounds = bugs.bounds()
        val cycles = mutableSetOf<Set<Coord>>()
        var next = bugs
        while (!cycles.contains(next)) {
            cycles.add(next)
            next = next.cycle(bounds)
        }
        next.sumOf { bug -> 2 pow (bug.y * 5 + bug.x) }
    }

    override fun part2(): Any = "Another one I did not spend a ton of time with"

    private fun Set<Coord>.cycle(bounds: Pair<Coord, Coord>): Set<Coord> =
        this.flatMap { bug ->
            val bugFree = bug.neighbors().filterNot { this.contains(it) }
            // bugs die unless next to exactly one other bug
            val newBug = bug.takeIf { bugFree.size == 3 }
            // bugs auto-generate into an empty square with one or two bugs next to it
            bugFree.map { nonBug ->
                nonBug.takeIf { nonBug.neighbors().count { this.contains(it) } in (1..2) }
            }.plus(newBug).filterNotNull().filter { bounds.contains(it) }
        }.toSet()

    private fun loader() = load().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c -> Coord(x, y).takeIf { c == '#' } }
    }.toSet()
}