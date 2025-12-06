package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rowColSwap

/**
 * Day 6: Trash Compactor
 */
class Dec06 : PuzzleDayTester(6, 2025) {

    override fun part1(): Any = loader().sumOf { problem ->
        if (problem.last().contains("*")) {
            problem.dropLast(1).fold(1L) { acc, str -> acc * str.trim().toLong() }
        } else {
            problem.dropLast(1).fold(0L) { acc, str -> acc + str.trim().toLong() }
        }
    } == 5361735137219

    override fun part2(): Any = loader().sumOf { problem ->
        val nums = problem.dropLast(1).map(String::toList).rowColSwap().map { it.joinToString("").trim().toLong() }
        if (problem.last().contains("*")) {
            nums.fold(1L) { acc, n -> acc * n }
        } else {
            nums.sum()
        }
    } == 11744693538946
    
    private fun List<String>.extractNumbers(rotate: Boolean): List<Long> =
        if (rotate) {
            dropLast(1).map(String::toList).rowColSwap().map { it.joinToString("").trim().toLong() }
        } else {
            dropLast(1).map { it.trim().toLong() }    
        }

    /**
     * loader idea: read from all rows simultaneously at the same index
     * if an entire column is blank, that's the break for the next problem
     */
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
