package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.Coord3D
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.combinations

/**
 * Day 8: Playground
 */
class Dec08 : PuzzleDayTester(8, 2025) {

    private val junctions = loader()
    private val distancePairs =  junctions.combinations(2).map { (a, b) ->
        (a to b) to a.distance(b)
    }.sortedBy { it.second }

    override fun part1(): Any =
        distancePairs.take(1000)
            .fold(mutableListOf<Set<Coord3D>>()) { sets, pair -> sets.addMerge(pair.first) }
            .sortedByDescending { it.size }
            .take(3)
            .fold(1L) { acc, set -> acc * set.size } == 24360L

    override fun part2(): Any {
        val sets = mutableListOf<Set<Coord3D>>()
        var last: Pair<Coord3D, Coord3D> = (Coord3D() to Coord3D())
        distancePairs.toList().takeWhile { (pair, _) -> 
            last = pair
            sets.addMerge(pair)
            sets.first().size != junctions.size
        }
        return last.first.x.toLong() * last.second.x == 2185817796
    }

    private fun MutableList<Set<Coord3D>>.addMerge(pair: Pair<Coord3D, Coord3D>): MutableList<Set<Coord3D>> {
        val r1 = filter { pair.first in it }
        val r2 = filter { pair.second in it }
        val r3 = listOf(setOf(pair.first, pair.second))
        removeAll(r1)
        removeAll(r2)
        removeAll(r3)
        add((r1.flatten() + r2.flatten() + r3.flatten()).toSet())
        return this
    }

    private fun loader() = load().map(Coord3D::parse)
}
