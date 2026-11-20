package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.hexCharTo4BitBinary

/**
 * Day 14: Disk Defragmentation
 * 128x128 grid, each spot is free or used
 * state of the grid is tracked by knot hashes (arg)
 * each row of 128 is hashed (0 = free, 1 = used)
 * input looks like: knot hash of <input>-<rownumber>
 */
class Dec14 : PuzzleDayTester(14, 2017) {

    val input = "jzgqcdpd"

    override fun part1(): Any = expandHash().sumOf { it.count { c -> c == '1' } }

    /**
     * Convert the grid of binary into coords (only track 1s throw 0s away)
     * a coord is connected to its neighbors if they exist
     * use the exact code from Dec12 to build connected graphs
     */
    override fun part2(): Any = expandHash().let { rows ->
        val graphs = mutableListOf<Set<Coord>>()
        val coords = rows.flatMapIndexed { y, row -> row.mapIndexedNotNull { x, c -> Coord(x, y).takeUnless { c == '1' } } }.toSet()
        coords.forEach { coord ->
            val nodes = coord.neighbors().plus(coord).filter { coords.contains(it) }
            val connections = graphs.filter { graph -> nodes.any { it in graph } }
            graphs.removeAll(connections)
            graphs.add(connections.flatten().plus(nodes).toSet())
        }
        graphs.size
    }

    private fun expandHash(): List<String> =
        (0..127).map { row ->
            Dec10.knotHash("$input-$row").map { c -> hexCharTo4BitBinary(c) }.joinToString("")
        }
}
