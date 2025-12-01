package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 12: Leonardo's Monorail
 */
class Dec12 : PuzzleDayTester(12, 2016) {

    override fun part1(): Any = load().solve(
        mutableMapOf(
            "a" to 0,
            "b" to 0,
            "c" to 0,
            "d" to 0,
        )
    ) == 318003

    override fun part2(): Any = load().solve(
        mutableMapOf(
            "a" to 0,
            "b" to 0,
            "c" to 1,
            "d" to 0,
        )
    ) == 9227657

    private fun List<String>.solve(r: MutableMap<String, Int>): Int {
        var i = 0
        while (i < this.size) {
            val s = this[i].split(" ")
            when (s[0]) {
                "cpy" -> {
                    r[s[2]] = r.getOrInt(s[1])
                    i++
                }
                "inc" -> {
                    r[s[1]] = r[s[1]]!! + 1
                    i++
                }
                "dec" -> {
                    r[s[1]] = r[s[1]]!! - 1
                    i++
                }
                "jnz" -> {
                    val x = r.getOrInt(s[1])
                    if (x == 0) {
                        i++
                    } else {
                        i += s[2].toInt()
                    }
                }
            }
        }
        return r["a"]!!
    }

    private fun Map<String, Int>.getOrInt(x: String) = this[x] ?: x.toInt()
}
