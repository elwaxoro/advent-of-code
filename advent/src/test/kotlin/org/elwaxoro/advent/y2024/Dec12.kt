package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 12: Garden Groups
 */
class Dec12 : PuzzleDayTester(12, 2024) {

    /**
     * Turbo-lazy idea:
     * 1. Group by letter, then just need to split up disconnected sets
     * 2. For each coord of same letter, find existing sets containing any neighbors
     * 3. Merge those sets into one set, along with coord: this is the new region
     */
    override fun part1(): Any = loader().organize().sumOf { set -> set.sumOf { c -> 4 - c.neighbors().count { it in set } } * set.size }

    /**
     * Same as part 1 with one extra organizing step:
     * 1. organize coords into distinct sets by letter (regions)
     * 2. for each region look in each direction (N, S, E, W) and keep coords with no region-neighbor in that direction: these are all the potential edge coords facing that direction
     * 3. organize these coords: each distinct set is an edge disconnected others facing the same way
     */
    override fun part2(): Any = loader().organize().sumOf { set -> Dir.entries.sumOf { dir -> set.filter { coord -> !set.contains(coord.move(dir)) }.splitSets().size } * set.size }

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
