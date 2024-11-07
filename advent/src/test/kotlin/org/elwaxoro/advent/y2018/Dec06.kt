package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.*

/**
 * Day 6: Chronal Coordinates
 */
class Dec06 : PuzzleDayTester(6, 2018) {

    private val names = (('a'..'z') + ('A'..'Z') + ('0'..'9')).toList()

    override fun part1(): Any = loader().let { grid ->
        val newGrid = grid.toMutableList()
        val (min, max) = grid.bounds()
        (min.x..max.x).forEach { x ->
            (min.y..max.y).forEach { y ->
                val c = Coord(x, y)
                val d = grid.groupBy { it.taxiDistance(c) }
                val minDistance = d.minBy { it.key }
                if (minDistance.value.size == 1) {
                    newGrid.add(c.copyD(minDistance.value.single().d))
                } else {
                    newGrid.add(c.copyD('.'))
                }
            }
        }
        val groups = newGrid.groupBy { it.d }
        val contained = groups.filter { (_, coords) -> coords.none { it.x <= min.x || it.y <= min.y || it.x >= max.x || it.y >= max.y } }
        contained.maxOf { it.value.size } - 1
    }

    /**
     * major assumption that just happened to work out: I don't need to search outside the bounds of the starting coords
     */
    override fun part2(): Any= loader().let { grid ->
        val (min, max) = grid.bounds()
        val maxDist = 10000
        (min.x..max.x).sumOf { x ->
            (min.y..max.y).mapNotNull { y ->
                val c = Coord(x, y)
                grid.sumOf { it.taxiDistance(c) }.takeIf { it < maxDist }
            }.size
        }
    }

    private fun loader() = load().mapIndexed { index, s -> Coord.parse(s).copyD(names[index]) }
}
