package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 7: Bridge Repair
 */
class Dec07 : PuzzleDayTester(7, 2024) {

    override fun part1(): Any = loader().filter { (t, w) -> solve(t, w, listOf(plus, mult)) }.sumOf { it.first }

    override fun part2(): Any = loader().filter { (t, w) -> solve(t, w, listOf(plus, mult, concat)) }.sumOf { it.first }

    private fun solve(t: Long, w: List<Long>, ops: List<Op<Long>>, total: Long = 0): Boolean =
        w.takeIf { it.isNotEmpty() && t > total }?.let {
            ops.any { solve(t, w.drop(1), ops, it.invoke(total, w[0])) }
        } ?: (t == total)

    private fun loader() = load().map {
        val (total, values) = it.split(":")
        total.trim().toLong() to values.trim().split(" ").map { it.toLong() }
    }

    private val concat: Op<Long> = { a, b -> "$a$b".toLong() }
    private val plus: Op<Long> = { a, b -> a + b }
    private val mult: Op<Long> = { a, b -> a * b }
}

private typealias Op<Long> = (Long, Long) -> Long
