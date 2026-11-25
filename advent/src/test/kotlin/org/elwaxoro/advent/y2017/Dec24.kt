package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 24: Electromagnetic Moat
 */
class Dec24 : PuzzleDayTester(24, 2017) {

    override fun part1(): Any = loader().let { components ->
        val starts = components.filter { it.first == 0 }
        starts.flatMap { start ->
            buildBridges(components.minus(start), listOf(start))
        }.maxOf { it.sumOf { it.first + it.second } }
    }

    override fun part2(): Any = loader().let { components ->
        val starts = components.filter { it.first == 0 }
        val bridges = starts.flatMap { start ->
            buildBridges(components.minus(start), listOf(start))
        }
        bridges.sortedWith(
            compareByDescending<List<Pair<Int, Int>>> {
                it.size
            }.thenByDescending { it.sumOf { it.first + it.second } }
        ).first().sumOf { it.first + it.second }
    }

    /**
     * Part 1 wants the strongest bridge of any length
     * Part 2 wants the longest THEN strongest bridge
     * Refactored to return all possible bridges
     */
    private fun buildBridges(components: List<Pair<Int, Int>>, built: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> {
        val tail = built.last()
        val candidates = components.filter { it.first == tail.second || it.second == tail.second }
        return if (candidates.isEmpty()) {
            listOf(built)
        } else {
            candidates.flatMap { next ->
                val nextC = components.minus(next)
                if (next.first == tail.second) {
                    // already oriented
                    buildBridges(nextC, built.plus(next))
                } else {
                    // flip it first
                    buildBridges(nextC, built.plus((next.second to next.first)))
                }
            }
        }
    }

    /**
     * Can't be a map, there are multiple matching keys here
     * Can be a list of pairs tho
     */
    private fun loader() = load().map { line ->
        val (i, o) = line.split("/").map { it.toInt() }
        i to o
    }
}
