package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 20: Race Condition
 */
class Dec20 : PuzzleDayTester(20, 2024) {

    override fun part1(): Any = loader().buildPath().countCheats(2)
    override fun part2(): Any = loader().buildPath().countCheats(20)

    private fun Map<Coord, Int>.countCheats(maxDist: Int): Int =
        map { (coord, cost) ->
            coord.enumerateRectangle(maxDist)
                .filter {
                    val dist = coord.taxiDistance(it)
                    dist <= maxDist && getOrDefault(it, 0) - cost - dist >= 100
                }.size
        }.sum()

    private fun List<Coord>.buildPath(): Map<Coord, Int> {
        val start = single { it.d == 'S' }.copyD()
        val end = single { it.d == 'E' }.copyD()
        val path = map { it.copyD() }.toSet()
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

    private fun loader() = load().flatMapIndexed { y, line -> line.mapIndexedNotNull { x, c -> Coord(x, y, c).takeUnless { c == '#' } } }
}
