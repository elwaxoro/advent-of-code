package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.*

/**
 * Day 3: Crossed Wires
 */
class Dec03: PuzzleDayTester(3, 2019, 260, 15612) {

    private val start = Coord(0,0,'o')

    override fun part1(): Any = load().map { it.split(",") }.let { wires ->
        val wire1 = wires.first().toSegments()
        val wire2 = wires.last().toSegments()
        val intersections = wire1.intersections(wire2, setOf(start))
        intersections.minOf { it.taxiDistance(start) }
    }

    override fun part2(): Any = load().map { it.split(",") }.let { wires ->
        val wire1 = wires.first().toSegments()
        val wire2 = wires.last().toSegments()
        val intersections = wire1.intersections(wire2, setOf(start))
        val wire1map = wireIntersectCostMap(wire1, intersections)
        val wire2map = wireIntersectCostMap(wire2, intersections)
        listOf(wire1map, wire2map).merge { value, existing ->
            value + (existing ?: 0)
        }.minOf { it.value }
    }

    private fun wireIntersectCostMap(wire: List<Coord>, intersections: Set<Coord>): Map<Coord, Int> {
        val costMap = mutableMapOf<Coord, Int>()
        var runningCost = 0
        wire.windowed(2).map { it.first() to it.last() }.forEach { segment ->
            val cost = segment.first.taxiDistance(segment.second)
            val hits = intersections.filter { !costMap.containsKey(it) && segment.contains(it) }.distinct()
            hits.forEach { hit ->
                costMap[hit] = runningCost + segment.first.taxiDistance(hit)
            }
            runningCost += cost
        }
        return costMap
    }

    private fun List<String>.toSegments() = fold(mutableListOf(start)) { acc, s ->
        acc.also { it.add(acc.last().move(Dir.fromUDLR(s[0]), s.drop(1).toInt())) }
    }
}
