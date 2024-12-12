package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 12: Garden Groups
 */
class Dec12 : PuzzleDayTester(12, 2024) {

    override fun part1(): Any = loader().organize().sumOf { set ->
        set.sumOf { c ->
            4 - c.neighbors().count { it in set }
        } * set.size
    }

    override fun part2(): Any {
        return super.part2()
    }

    /**
     * Turbo-lazy idea:
     * 1. Group by letter, then just need to split up disconnected sets
     * 2. For each coord of same letter, find existing sets containing any neighbors
     * 3. Merge those sets into one set, along with coord
     */
    private fun List<Pair<Coord, Char>>.organize(): List<Set<Coord>> =
        this.groupBy { it.second }.map { (_, group) ->
            group.fold(mutableListOf<Set<Coord>>()) { sets, (c, _) ->
                sets.also {
                    val matchedSets = c.neighbors().flatMap { n -> sets.filter { it.contains(n) } }
                    sets.removeAll(matchedSets)
                    val newSet = setOf(c) + matchedSets.flatten()
                    sets.add(newSet)
                }
            }
        }.flatten()

    private fun List<List<Coord>>.organize(): List<Set<Coord>> {

        return TODO()
    }

    private fun loader() = load().mapIndexed { y, line -> line.mapIndexed { x, c -> Coord(x, y) to c } }.flatten()
}
