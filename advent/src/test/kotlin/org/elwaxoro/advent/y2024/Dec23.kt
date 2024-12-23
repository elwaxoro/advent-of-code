package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: LAN Party
 */
class Dec23: PuzzleDayTester(23, 2024) {

    override fun part1(): Any = loader().let { network ->
        network.keys.filter { it.startsWith('t') }.flatMap { t ->
            val connections = network.getValue(t)
            connections.flatMap { c ->
                network.getValue(c).filter { connections.contains(it) }.map { setOf(t, c, it) }
            }
        }.distinct().size
    }

    private fun loader() = load().flatMap {
        val (a, b) = it.split("-")
        listOf(a to b, b to a)
    }.groupBy { it.first }.mapValues { it.value.map { it.second }.toSet() }
}
