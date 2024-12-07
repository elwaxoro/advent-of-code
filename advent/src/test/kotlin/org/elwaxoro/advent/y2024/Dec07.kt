package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 7: Bridge Repair
 */
class Dec07 : PuzzleDayTester(7, 2024) {

    override fun part1(): Any = loader().mapNotNull { (t, w) -> t.takeIf { solve(t, 0, w, listOf(plus, mult)) } }.sum()

    override fun part2(): Any = loader().mapNotNull { (t, w) -> t.takeIf { solve(t, 0, w, listOf(plus, mult, concat)) } }.sum()

    private fun solve(target: Long, total: Long, remaining: List<Long>, ops: List<Op<Long>>): Boolean =
        remaining.takeIf { it.isNotEmpty() && target > total }?.let {
            ops.any { solve(target, it.invoke(total, remaining.first()), remaining.drop(1), ops) }
        } ?: (target == total)

    private fun loader() = load().map {
        val (total, values) = it.split(":")
        total.trim().toLong() to values.trim().split(" ").map { it.toLong() }
    }

    private val concat: Op<Long> = { a, b -> "$a$b".toLong() }
    private val plus: Op<Long> = { a, b -> a + b }
    private val mult: Op<Long> = { a, b -> a * b }
}

private typealias Op<Long> = (Long, Long) -> Long
