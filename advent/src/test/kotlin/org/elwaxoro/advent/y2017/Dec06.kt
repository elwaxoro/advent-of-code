package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 6: Memory Reallocation
 */
class Dec06 : PuzzleDayTester(6, 2017) {

    private val input = """4	10	4	1	8	4	9	14	5	1	14	15	0	15	3	5"""

    private val solution = loopFinder5000()

    override fun part1(): Any = solution.first

    override fun part2(): Any = solution.second

    private fun loopFinder5000(): Pair<Int, Int> = input.split('\t').map { it.toInt() }.toMutableList().let { mem ->
        val seen = mutableListOf(mem.joinToString(" "))
        var ctr = 0
        while (true) {
            ctr++
            val max = mem.max()
            val maxIdx = mem.indexOf(max)
            mem[maxIdx] = 0

            (maxIdx + 1..maxIdx + max).forEach { idx ->
                mem[idx % mem.size]++
            }
            val new = mem.joinToString(" ")
            if (seen.contains(new)) {
                break
            } else {
                seen.add(new)
            }
        }
        ctr to ctr - seen.indexOf(mem.joinToString(" "))
    }
}
