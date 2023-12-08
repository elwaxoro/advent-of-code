package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.lcm
import java.math.BigInteger

/**
 * Haunted Wasteland
 */
class Dec08: PuzzleDayTester(8, 2023) {

    /**
     * 19637
     * Traverse the map from AAA to ZZZ, counting the steps, ezpz
     */
    override fun part1(): Any = loader().let { (directions, nodes) ->
        var key = "AAA"
        var step = 0
        while (key != "ZZZ") {
            val node = nodes[key]!!
            val direction = directions[step % directions.length]
            key = node.first.takeIf { direction == 'L' } ?: node.second
            step++
        }
        step
    }

    /**
     * 8811050362409
     * Nearly-infinite runtime (I assume) trying to loop until everything lands on a Z node at the same time
     * Testing of the input shows that each key completes a loop at different intervals
     * Keep track of each key's loop size, then find the lowest common multiple for all of them
     */
    override fun part2(): Any = loader().let { (directions, nodes) ->
        var keys = nodes.filter { it.key.endsWith("A") }.keys.toList()
        val distances = mutableListOf<BigInteger>()
        var step = 0
        while (keys.isNotEmpty()) {
            keys = keys.mapNotNull { key ->
                if (key.endsWith("Z")) {
                    distances.add(BigInteger.valueOf(step.toLong()))
                    null
                } else {
                    val node = nodes[key]!!
                    val direction = directions[step % directions.length]
                    node.first.takeIf { direction == 'L' } ?: node.second
                }
            }
            step++
        }
        distances.lcm()
    }

    private fun loader() = load(delimiter = "\n\n").let { (d, n) ->
        val directions = d.trim()
        val nodes = n.split("\n").associate {
            val (a, b, c) = it.replace("\\W+".toRegex(), " ").split(" ")
            a to (b to c)
        }
        directions to nodes
    }
}
