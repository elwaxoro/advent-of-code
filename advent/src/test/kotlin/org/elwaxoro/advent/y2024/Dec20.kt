package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 20: Race Condition
 * Assumptions: path has no forks
 */
class Dec20 : PuzzleDayTester(20, 2024) {

    override fun part1(): Any = loader().countCheats(2)
    override fun part2(): Any = loader().countCheats(20)

    private fun Map<Char, List<Pair<Coord, Char>>>.countCheats(maxDist: Int): Int {
        val start = this['S']!!.first().first.copyD()
        val end = this['E']!!.first().first.copyD()
        val rawPath = this['.']!!.map { it.first.copyD() }.plus(start).plus(end).toSet()
        val sortedPath = buildPath(start, end, rawPath)
        val requiredSavings = 100
        return sortedPath.map { (coord, cost) ->
            coord.add(maxDist, maxDist)
                .enumerateRectangle(coord.add(maxDist * -1, maxDist * -1))
                .filter {
                    val dist = coord.taxiDistance(it)
                    dist <= maxDist && (sortedPath[it] ?: 0) - cost - dist >= requiredSavings
                }.size
        }.sum()
    }

    private fun buildPath(start: Coord, end: Coord, path: Set<Coord>): Map<Coord, Int> {
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

    private fun loader() = load().mapIndexed { y, line -> line.mapIndexed { x, c -> Coord(x, y) to c } }.flatten().groupBy { it.second }
}
