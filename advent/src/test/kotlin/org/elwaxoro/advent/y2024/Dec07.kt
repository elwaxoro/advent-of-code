package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 7: Bridge Repair
 */
class Dec07: PuzzleDayTester(7, 2024) {

    override fun part1(): Any = loader().sumOf { (target, working) -> target.takeIf { solver(target, 0, working) > 0 } ?: 0 } == 5702958180383

    private fun solver(target: Long, total: Long, working: List<Long>): Long =
        if (working.isEmpty()) {
            if (total == target) {
                total
            } else {
                0
            }
        } else if (total > target) {
            0
        } else {
            solver(target, total + working.first(), working.drop(1)) +
                    solver(target, total * working.first(), working.drop(1))
        }

    override fun part2(): Any = loader().sumOf { (target, working) ->
        target.takeIf { solver3(target, 0, working, listOf(plus, mult, concat)) > 0 } ?: 0
    } == 92612386119138

    private fun solver2(target: Long, total: Long, working: List<Long>): Long =
        if (working.isEmpty()) {
            if (total == target) {
                total
            } else {
                0
            }
        } else if (total > target) {
            0
        } else {
            solver2(target, total + working.first(), working.drop(1)) +
                    solver2(target, total * working.first(), working.drop(1)) +
                        solver2(target, "$total${working[0]}".toLong(), working.drop(1))
        }

    private fun solver3(target: Long, total: Long, working: List<Long>, ops: List<Op>): Long =
        if (working.isEmpty()) {
            if (total == target) {
                total
            } else {
                0
            }
        } else if (total > target) {
            0
        } else if (ops.any {
                solver3(target, it.apply(total, working.first()), working.drop(1), ops) > 0
            }) {
            target
        } else {
            0
        }

    private val plus: Op = object : Op {
        override fun apply(total: Long, next: Long): Long = total + next
    }

    private val mult: Op = object : Op {
        override fun apply(total: Long, next: Long): Long = total * next
    }

    private val concat: Op = object : Op {
        override fun apply(total: Long, next: Long): Long = "$total$next".toLong()
    }

    private fun loader() = load().map {
        val (total, values) = it.split(":")
        total.trim().toLong() to values.trim().split(" ").map { it.toLong() }
    }
}

private interface Op {
    fun apply(total: Long, next: Long): Long
}
