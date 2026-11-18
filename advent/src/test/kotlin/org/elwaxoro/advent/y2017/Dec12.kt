package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 12: Digital Plumber
 *  fully connected graph? connected graph partitioning?
 */
class Dec12 : PuzzleDayTester(12, 2017) {

    override fun part1(): Any = partitionNodes().first { it.contains("0") }.size
    override fun part2(): Any = partitionNodes().size

    private fun partitionNodes(): List<Set<String>> = loader().let { network ->
        val graphs = mutableListOf<Set<String>>()

        network.forEach { nodes ->
            val connections = graphs.filter { graph -> nodes.any { it in graph } }
            graphs.removeAll(connections)
            graphs.add(connections.flatten().plus(nodes).toSet())
        }

        graphs
    }

    private fun loader() = load().map { line ->
        line.replace(" <->", ",").split(", ")
    }
}
