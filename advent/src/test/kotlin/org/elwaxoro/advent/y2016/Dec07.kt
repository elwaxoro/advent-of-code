package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 7: Internet Protocol Version 7
 */
class Dec07: PuzzleDayTester(7 , 2016) {

    /**
     * TLS (transport-layer snooping) = ABBA in supernet, no ABBA in hypernet
     */
    override fun part1(): Any = loader().filter { (supernet, hypernet) ->
        supernet.any { it.isABBA() } && hypernet.none { it.isABBA() }
    }.size // 105

    /**
     * SSL (super-secret listening) = ABA in supernet, BAB in hypernet
     */
    override fun part2(): Any = loader().filter { (supernet, hypernet) ->
        // find all the ABAs, convert to BABs, find all the BABs
        supernet.flatMap { it.findABA() }.map { it.bab() }.any { bab -> hypernet.any { it.contains(bab) }}
    }.size

    private fun String.bab(): String = "${this[1]}${this[0]}${this[1]}"

    private fun String.findABA(): List<String> = this.windowed(3, 1).filter { it[0] == it[2] && it[0] != it[1] }

    private fun String.isABBA(): Boolean = this.windowed(4, 1).any { it[0] == it[3] && it[1] == it[2] && it[0] != it[1] }

    private fun loader() = load().map { address ->
        val supernet = mutableListOf<String>()
        val hypernet = mutableListOf<String>()
        var current = ""
        address.forEach { c ->
            when (c) {
                '[' -> {
                    supernet.add(current)
                    current = ""
                }
                ']' -> {
                    hypernet.add(current)
                    current = ""
                }
                else -> current += c
            }
        }
        if (current.isNotEmpty()) {
            supernet.add(current)
        }
        supernet to hypernet
    }
}
