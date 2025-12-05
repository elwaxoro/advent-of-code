package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.mergeAllOverlap
import kotlin.math.max
import kotlin.math.min

/**
 * Day 20: Firewall Rules
 */
class Dec20 : PuzzleDayTester(20, 2016) {

    override fun part1(): Any = loader().let { ranges ->
        var ip = 0L
        while (true) {
            ranges.firstOrNull { it.contains(ip) }?.let {
                ip = it.last + 1L
            } ?: break
        }
        ip
    } == 19449262L

    /**
     * Idea: ranges overlap, merge them completely before looking for IPs
     */
    override fun part2(): Any = loader().let { ranges ->
        val reduced = ranges.toMutableSet().mergeAllOverlap()

        reduced.sortedBy { it.first }.zipWithNext().sumOf { (a, b) ->
            (b.first - a.last - 1).takeIf { it > 0 } ?: 0
        }
    } == 119L

    private fun loader() = load().map {
        val (a, b) = it.split("-").map(String::toLong)
        LongRange(a, b)
    }.sortedBy { it.first }
}
