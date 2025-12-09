package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.permutations
import org.elwaxoro.advent.toRectangle2D
import java.awt.Polygon
import java.awt.geom.Area

/**
 * Day 9: Movie Theater
 */
class Dec09 : PuzzleDayTester(9, 2025) {

    override fun part1(): Any = loader().permutations(2).maxOf { (a, b) -> a.areaI(b) } == 4777967538

    override fun part2(): Any = loader().let { coords ->
        // Polygon contains doesn't work for this, but Area does ARRRG
        val polygon = Area(coords.fold(Polygon()) { acc, c -> acc.apply { addPoint(c.x, c.y) } })
        coords.permutations(2).filter { (a, b) ->
            polygon.contains((a to b).toRectangle2D())
        }.map { (a, b) -> a.areaI(b) }.max()
    } == 1439894345L

    private fun loader() = load().map(Coord::parse)
}
