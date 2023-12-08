package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.lcm
import org.elwaxoro.advent.PuzzleDayTester
import java.math.BigInteger

/**
 * Haunted Wasteland
 */
class Dec08: PuzzleDayTester(8, 2023) {

    /**
     * 19637
     */
    override fun part1(): Any = loader().let { (directions, nodes) ->
        var key = "AAA"
        var dirIdx = 0
        while (key != "ZZZ") {
            val node = nodes[key]!!
            val direction = directions[dirIdx % directions.length]
            key = node.first.takeIf { direction == 'L' } ?: node.second
            dirIdx++
        }
        dirIdx
    }

    /**
     * 8811050362409
     */
    override fun part2(): Any = loader().let { (directions, nodes) ->
        var keys = nodes.filter { it.key.endsWith("A") }.keys.map { it to it }
        val distMap = mutableMapOf<String, BigInteger>()
        var dirIdx = 0
        while (distMap.size < keys.size) {
            keys = keys.map { (start, current) ->
                if (current.endsWith("Z")) {
                    distMap[start] = BigInteger.valueOf(dirIdx.toLong())
                }
                val node = nodes[current]!!
                val direction = directions[dirIdx % directions.length]
                start to (node.first.takeIf { direction == 'L' } ?: node.second)
            }
            dirIdx++
        }
        distMap.values.lcm()
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
