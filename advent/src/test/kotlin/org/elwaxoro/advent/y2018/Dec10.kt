package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.bounds
import org.elwaxoro.advent.printify

/**
 * Day 10: The Stars Align
 */
class Dec10 : PuzzleDayTester(10, 2018) {

    /**
     * No idea how to tell if the coords are spelling a word or not, sooo
     * assuming smallest cluster of coords is the solution
     * this assumption turned out to be true, on loop 10124 so the loop size is cut down from the original 25000
     */
    override fun part1(): Any = loader().let { pairs ->
        var minSize = Int.MAX_VALUE
        var bestIteration = 0
        var bestCoords = listOf<Coord>()
        (1..10200).fold(pairs) { acc, idx ->
            acc.map { (xy, vel) ->
                xy.add(vel) to vel
            }.also {
                val coords = it.map { it.first }
                val bounds = coords.bounds()
                val size = bounds.first.taxiDistance(bounds.second)
                if (size < minSize) {
                    minSize = size
                    bestIteration = idx
                    bestCoords = coords
                }
            }
        }
        println("Min size is $minSize at loop $bestIteration")
        bestCoords.printify()
    }

    /**
     * part 2 is solved already by the output from part 1
     */
    override fun part2(): Any {
        return "See part 1 output"
    }

    private fun loader(): List<Pair<Coord, Coord>> = load().map { line ->
        line.replace("position=<", "")
            .replace("velocity=<", ", ")
            .replace(">", "")
            .split(",")
            .map { it.trim().toInt() }
    }.map { (x, y, vx, vy) ->
        Coord(x, y) to Coord(vx, vy)
    }.toList()
}
