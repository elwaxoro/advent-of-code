package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 10: Hoof It
 */
class Dec10 : PuzzleDayTester(10, 2024) {

    override fun part1(): Any = loader().explore().sumOf { it.peaks.size }

    override fun part2(): Any = loader().explore().sumOf { it.paths.size }

    /**
     * Explore backwards from peaks to trailheads
     * Returns list of trailheads
     */
    private fun Map<Coord, Node>.explore(): List<Node> {
        val seek = filter { it.value.isPeak() }.map {
            it.value.peaks.add(it.key)
            it.value.paths.add(listOf(it.key))
            it.key to it.value
        }.toMutableList()

        while (seek.isNotEmpty()) {
            seek.removeFirst().takeIf { !it.second.isTrailhead() }?.let { (coord, node) ->
                coord.neighbors().forEach { n ->
                    this[n]?.let { nn ->
                        if (nn.elevation + 1 == node.elevation) {
                            nn.peaks.addAll(node.peaks)
                            nn.paths.addAll(node.paths.map { it.plus(n) })
                            seek.add(n to nn)
                        }
                    }
                }
            }
        }
        return values.filter { it.isTrailhead() }
    }

    private fun loader() = load().mapIndexed { y, line -> line.mapIndexed { x, c -> Coord(x, y) to Node(c.digitToInt()) } }.flatten().toMap()

    private data class Node(
        val elevation: Int,
        val peaks: MutableSet<Coord> = mutableSetOf(),
        val paths: MutableSet<List<Coord>> = mutableSetOf()
    ) {
        fun isPeak() = elevation == 9
        fun isTrailhead() = elevation == 0
    }
}
