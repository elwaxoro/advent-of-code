package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Snowverload
 */
class Dec25: PuzzleDayTester(25, 2023) {

    override fun part1(): Any = loader().let { g ->
        // TODO someday :'(
        // it's been a fun AoC!
    }

    private fun loader() = load().map { it.replace(":", "").split(" ") }.fold(mutableMapOf<String, MutableList<String>>()) { map, split ->
        map.also {
            val u = split.first()
            val vs = split.drop(1)
            // connect everything in both directions
            vs.forEach { v ->
                map.getOrPut(u) { mutableListOf() }.add(v)
                map.getOrPut(v) { mutableListOf() }.add(u)
            }
        }
    }
}
