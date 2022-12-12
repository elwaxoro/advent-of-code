package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.*

/**
 * Day 12: Hill Climbing Algorithm
 */
class Dec12 : PuzzleDayTester(12, 2022) {

    /**
     * Use Dijkstra here, since already had it
     * All shortest paths to "E" will solve it, path from "S" to "E"'s distance is the answer
     */
    override fun part1(): Any = loader().flatten().let { nodes ->
        nodes.single { it.name == "E" }.dijkstra()
        // prints a nice little map with the path on it
        // println(nodes.mapNotNull { it.coord }.plus(nodes.single { it.name == "S" }.shortestPath.map { it.coord!!.copyD(it.coord!!.d!!.uppercaseChar()) }).printify())
        nodes.single { it.name == "S" }.shortestDistance
    } == 361

    /**
     * Again with the Dijkstra!
     * Part 1 solved this already, just find the "a" with the shortest distance to "E"
     */
    override fun part2(): Any = loader().flatten().let { nodes ->
        nodes.single { it.name == "E" }.dijkstra()
        nodes.filter { it.name == "a" }.minOf { start ->
            start.shortestDistance
        }
    } == 354

    private fun loader() = load().mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            val value = when (c) {
                'S' -> 97 // swap S for a's code: 97
                'E' -> 122 // swap E for z's code: 122
                else -> c.code
            }
            Node("$c").also {
                it.scratch = value
                it.coord = Coord(x, y, c)
            }
        }
    }.also { nodes ->
        // connect all the nodes together, with edges based on their cost
        nodes.forEachIndexed { y, row ->
            row.forEachIndexed { x, node ->
                if (y > 0) { node.addHillyNeighbor(nodes[y - 1][x]) } // up
                if (y < nodes.lastIndex) { node.addHillyNeighbor(nodes[y + 1][x]) } // down
                if (x > 0) { node.addHillyNeighbor(nodes[y][x - 1]) } // left
                if (x < row.lastIndex) { node.addHillyNeighbor(nodes[y][x + 1]) } // right
            }
        }
    }

    /**
     * If the neighbor's height is no greater than the current height + 1, travel cost is 1
     * Else set travel cost really high
     */
    private fun Node.addHillyNeighbor(neighbor: Node) {
        if (neighbor.scratch <= scratch + 1) {
            addEdge(neighbor, 1)
        } else {
            addEdge(neighbor, 10000)
        }
    }
}
