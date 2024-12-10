package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 10: Hoof It
 */
class Dec10 : PuzzleDayTester(10, 2024) {

    override fun part1(): Any = loader().explore().sumOf { it.peaks().size }

    override fun part2(): Any = loader().explore().sumOf { it.paths.size }

    /**
     * Explore backwards from peaks to trailheads
     * Returns list of trailheads
     */
    private fun Map<Coord, Node>.explore(): List<Node> {
        val seek = values.filter { it.isPeak() }.toMutableList()
        while (seek.isNotEmpty()) {
            // skip trailheads, those paths are complete
            seek.removeFirst().takeIf { !it.isTrailhead() }?.let { node ->
                node.coord.neighbors().forEach { neighborCoord ->
                    // ignore out-of-bounds neighbors
                    this[neighborCoord]?.let { neighborNode ->
                        // only go down
                        if (node.elevation - 1 == neighborNode.elevation) {
                            // brute force everything and let the set manage unique paths
                            neighborNode.paths.addAll(node.paths.map { it.plus(neighborCoord) })
                            seek.add(neighborNode)
                        }
                    }
                }
            }
        }
        return values.filter { it.isTrailhead() }
    }

    private fun loader() = load().mapIndexed { y, line -> line.mapIndexed { x, c -> Node.parse(x, y, c) } }.flatten().associateBy { it.coord }

    private data class Node(
        val elevation: Int,
        val coord: Coord,
        val paths: MutableSet<List<Coord>> = mutableSetOf()
    ) {
        companion object {
            fun parse(x: Int, y: Int, c: Char) = Node(c.digitToInt(), Coord(x, y)).also {
                if (it.isPeak()) {
                    it.paths.add(listOf(it.coord))
                }
            }
        }

        fun isPeak() = elevation == 9
        fun isTrailhead() = elevation == 0
        fun peaks() = paths.map { it.first() }.distinct()
    }
}
