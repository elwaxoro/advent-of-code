package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.remove

/**
 * Day 23: Opening the Turing Lock
 */
class Dec23 : PuzzleDayTester(23, 2015) {

    override fun part1(): Any = runComputer(mutableMapOf("a" to 0L, "b" to 0L))
    override fun part2(): Any = runComputer(mutableMapOf("a" to 1L, "b" to 0L))

    private fun runComputer(r: MutableMap<String, Long>): Long = load().let { ops ->
        var i = 0
        while (i < ops.size) {
            val op = ops[i].remove(",").split(" ")
            when (op[0]) {
                "hlf" -> r[op[1]] = r.getValue(op[1]) / 2L
                "tpl" -> r[op[1]] = r.getValue(op[1]) * 3L
                "inc" -> r[op[1]] = r.getValue(op[1]) + 1L
                "jmp" -> i = (i + op[1].toInt() - 1)
                "jie" -> i = (i + op[2].toInt() - 1).takeIf { r.getValue(op[1]) % 2L == 0L } ?: i
                "jio" -> i = (i + op[2].toInt() - 1).takeIf { r.getValue(op[1]) == 1L } ?: i
            }
            i++
        }
        r.getValue("b")
    }
}