package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
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

    override fun part2(): Any = loader().organize().sumOf { set ->

        val totalEdges = Dir.entries.sumOf { dir ->         // for each direction (N, S, E, W)
            // find coords that have no set-neighbor in that direction, these are edges
            val sides = set.filter { coord -> !set.contains(coord.move(dir)) }
            // run splitSets to get un-connected edges
            val edges = sides.splitSets()
            edges.size
        }
        totalEdges * set.size
    }

    /**
     * Turbo-lazy idea:
     * 1. Group by letter, then just need to split up disconnected sets
     * 2. For each coord of same letter, find existing sets containing any neighbors
     * 3. Merge those sets into one set, along with coord
     */
    private fun List<Pair<Coord, Char>>.organize(): List<Set<Coord>> = this.groupBy { it.second }.map { it.value.map { it.first } }.map { it.splitSets() }.flatten()

    private fun List<Coord>.splitSets(): List<Set<Coord>> = fold(mutableListOf()) { sets, c ->
        sets.also {
            val matchedSets = c.neighbors().flatMap { n -> sets.filter { it.contains(n) } }
            sets.removeAll(matchedSets)
            sets.add(setOf(c) + matchedSets.flatten())
        }
    }

    private fun loader() = load().mapIndexed { y, line -> line.mapIndexed { x, c -> Coord(x, y) to c } }.flatten()
}
