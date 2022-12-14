package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 14: Regolith Reservoir
 */
class Dec14 : PuzzleDayTester(14, 2022) {

    override fun part1(): Any = loader().dropSand()// == 805

    override fun part2(): Any = loader().addFloor().dropSand()// == 25161

    private fun Set<Coord>.dropSand(): Int = this.let { rocks ->
        val source = Coord(500, 0)
        // println(rocks.plus(source).printify())
        val maxY = rocks.maxOf { it.y }
        val sand = mutableSetOf<Coord>()
        var justStopAlready = false
        val both = rocks.toMutableSet()
        while (!justStopAlready) {
            var grain = source.copyD(null)
            var grainActive = true
            while (grainActive) {
                grain = grain.add(0, 1).takeUnless { both.contains(it) } // go down
                    ?: grain.add(-1, 1).takeUnless { both.contains(it) } // go left
                    ?: grain.add(1, 1).takeUnless { both.contains(it) } // go right
                    ?: run {
                        // settled!
                        both.add(grain)
                        sand.add(grain)
                        grainActive = false
                        if (grain == source) {
                            // CAVE IS FULL OF SAND!
                            justStopAlready = true
                        }
                        grain
                    }
                if (grain.y >= maxY) {
                    // fell off the edge
                    justStopAlready = true
                    grainActive = false
                }
            }
        }
        // println(rocks.plus(sand.map { it.copyD('o') }).printify())
        sand.size
    }

    private fun Set<Coord>.addFloor(): Set<Coord> {
        val maxY = maxOf { it.y }
        return this.plus((500 - maxY - 5..500 + maxY + 5).map { Coord(it, maxY + 2) }).toSet()
    }

    private fun loader() = load().map {
        it.split(" -> ").map(Coord::parse).fold(listOf<Coord>()) { acc, coord ->
            if (acc.isEmpty()) {
                acc.plus(coord)
            } else {
                acc.plus(acc.last().enumerateLine(coord))
            }
        }
    }.flatten().toSet()
}
