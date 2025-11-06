package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord4D
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 25: Four-Dimensional Adventure
 */
class Dec25: PuzzleDayTester(25, 2018) {

    override fun part1(): Any = loader().fold(emptyList<List<Coord4D>>()) { clusters, p ->
        clusters.partition { cluster -> cluster.any { it.isNear(p) } }.let { (near, far) ->
            far.plusElement(near.flatten().plus(p))
        }
    }.size == 394

    override fun part2(): Any = "Merry Christmas!"

    private fun Coord4D.isNear(that: Coord4D): Boolean = manhattan(that) <= 3

    private fun loader() = load().map(Coord4D::parse)
}
