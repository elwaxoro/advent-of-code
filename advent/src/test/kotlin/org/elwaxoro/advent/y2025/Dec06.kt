package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rowColSwap

/**
 * Day 6: Trash Compactor
 */
class Dec06 : PuzzleDayTester(6, 2025) {

    override fun part1(): Any = loader().sumOf { problem ->
        problem.extractNumbers(false).solve(problem.last())
    } == 5361735137219

    override fun part2(): Any = loader().sumOf { problem ->
        problem.extractNumbers(true).solve(problem.last())
    } == 11744693538946

    private fun List<Long>.solve(opr: String): Long =
        if (opr.contains("*")) {
            fold(1L) { acc, n -> acc * n }
        } else {
            sum()
        }

    private fun List<String>.extractNumbers(rotate: Boolean): List<Long> =
        if (rotate) {
            dropLast(1).map(String::toList).rowColSwap().map { it.joinToString("").trim().toLong() }
        } else {
            dropLast(1).map { it.trim().toLong() }    
        }

    private fun loader() = load().let { lines ->
        val rows = lines.size
        var idx = 0
        val problems: MutableList<List<String>> = mutableListOf()
        var current = MutableList(rows) { "" }
        while (idx < lines.first().length) {
            lines.forEachIndexed { rowIdx, line ->
                current[rowIdx] += line[idx]
            }
            if (current.all { it.last() == ' ' }) {
                problems.add(current.map { it.dropLast(1) })
                current = MutableList(rows) { "" }
            }
            idx++
        }
        problems.add(current.map { it })
        problems
    }
}
