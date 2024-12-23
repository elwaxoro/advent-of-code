package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: LAN Party
 */
class Dec23: PuzzleDayTester(23, 2024) {

    override fun part1(): Any = loader().let { network ->
        val ts = network.keys.filter { it.startsWith('t') }
        ts.map { t ->
            val connected = network.getValue(t)
            connected.map { c ->
                val cc = network.getValue(c).filter { connected.contains(it) }
                cc.map { setOf(t, c, it) }
            }.flatten()
        }.flatten().distinct().size
    }

    private fun loader() = load().flatMap {
        val (a, b) = it.split("-")
        listOf(a to b, b to a)
    }.groupBy { it.first }.mapValues { it.value.map { it.second } }
}
