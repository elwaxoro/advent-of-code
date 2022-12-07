package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.Node
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 6: Universal Orbit Map
 */
class Dec06: PuzzleDayTester(6, 2019, 295936, 457) {

    /**
     * the depth of a node is the sum of its direct and indirect orbits, so lets traverse and add up the depths!
     */
    override fun part1(): Any = loader().let { tree ->
        tree["COM"]!!.calculateAllDepths()
        tree.values.sumOf { it.scratch }
    }

    override fun part2(): Any = loader().let { tree ->
        // build path from YOU->COM, remove YOU
        // build path from SAN->COM, remove SAN
        // remove all dupes in the paths, count the size!
        tree["YOU"]!!.traceRoot().drop(1).plus(tree["SAN"]!!.traceRoot().drop(1)).groupBy { it }.filter { it.value.size == 1 }.size
    }

    private fun loader() = load().map {
        val split = it.split(")")
        split[0] to split[1]
    }.let { orbits ->
        val tree = mutableMapOf("COM" to Node("COM"))
        orbits.forEach { orbit ->
            val left = tree.getOrPut(orbit.first) { Node(orbit.first) }
            val right = tree.getOrPut(orbit.second) { Node(orbit.second) }
            right.parent = left
            left.addEdge(right)
        }
        tree
    }
}
