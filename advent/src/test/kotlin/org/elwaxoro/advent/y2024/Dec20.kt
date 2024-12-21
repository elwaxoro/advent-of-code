package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 20: Race Condition
 */
class Dec20 : PuzzleDayTester(20, 2024) {

    override fun part1(): Any = loader().buildPath().countCheats(2)
    override fun part2(): Any = loader().buildPath().countCheats(20)

    private fun Map<Coord, Int>.countCheats(maxDist: Int): Int = map { (coord, cost) ->
        coord.enumerateRectangle(maxDist).filter {
            val dist = coord.taxiDistance(it)
            dist <= maxDist && getOrDefault(it, 0) - cost - dist >= 100
        }.size
    }.sum()

    private fun Triple<Coord, Coord, Set<Coord>>.buildPath(): Map<Coord, Int> = let { (start, end, path) ->
        var cost = 0
        var coord = start
        val sortedPath = mutableMapOf(start to 0)
        while (coord != end) {
            cost++
            coord = coord.neighbors().single { n -> path.contains(n) && !sortedPath.containsKey(n) }
            sortedPath[coord] = cost
        }
        return sortedPath
    }

    private fun loader() = load().let { lines ->
        var start = Coord()
        var end = Coord()
        val track = lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                when (c) {
                    'S' -> Coord(x, y).also { start = it }
                    'E' -> Coord(x, y).also { end = it }
                    '.' -> Coord(x, y)
                    else -> null
                }
            }
        }.toSet()
        Triple(start, end, track)
    }
}
