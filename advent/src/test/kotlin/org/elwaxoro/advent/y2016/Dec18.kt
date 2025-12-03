package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 18: Like a Rogue
 *
 * Idea: temporarily pad the previous row with a '.' on either side, then run a windowed view to see if any chunks are in the trap list
 * Use a running fold to keep track of all generated rows (without any padding)
 *
 * Part 2 takes about 1.4s, without windowed gets it down to about 1s
 */
class Dec18 : PuzzleDayTester(18, 2016) {
    private val traps = listOf("^^.", ".^^", "^..", "..^")

    private val input = ".^^^.^.^^^^^..^^^..^..^..^^..^.^.^.^^.^^....^.^...^.^^.^^.^^..^^..^.^..^^^.^^...^...^^....^^.^^^^^^^"

    override fun part1(): Any = countTraps(40) == 1939

    override fun part2(): Any = countTraps(400000) == 19999535

    private fun countTraps(rows: Int) =
        (1..<rows).runningFold(input) { acc, _ ->
            ".$acc.".windowed(size = 3, step = 1, partialWindows = false).joinToString("") { window -> "^".takeIf { window in traps } ?: "." }
        }.sumOf { row -> row.count { it == '.' } }
}
