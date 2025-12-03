package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.splitToInt

/**
 * Day 3: Lobby
 */
class Dec03 : PuzzleDayTester(3, 2025) {

    /**
     * Idea: loop each bank once, pick primary and secondary maximums as you go
     * Don't reset primary if almost to end of list
     */
    override fun part1(): Any = loader().sumOf { bank ->
        var a = 0
        var b = 0
        bank.forEachIndexed { idx, battery ->
            if (battery > a && idx < bank.size - 1) {
                a = battery
                b = 0
            } else if (battery > b) {
                b = battery
            }
        }
        if (a == 0 || b == 0) {
            throw IllegalStateException("failed to find a good voltage for $bank")
        }
        a * 10 + b
    } == 17092

    /**
     * Turn on 12 batteries instead of 2
     * same approach but with a list?
     * battery > n && idx < bank.size - 12 + n?
     * ie: don't select a new max if there's not enough room left in the bank for the remaining digits
     * 170108965159310 is too low
     */
    override fun part2(): Any = loader().sumOf { bank ->
        val joltage = MutableList(12) { 0 }

        bank.forEachIndexed { idx, battery ->
            var found = false
            var foundIdx = -1
            joltage.forEachIndexed { jidx, max ->
                if (battery > max && idx < bank.size - 11 + jidx && !found) {
                    found = true
                    foundIdx = jidx
                }
            }
            if (found) {
                joltage[foundIdx] = battery
                (foundIdx + 1 ..< joltage.size).forEach { clearIdx ->
                    joltage[clearIdx] = 0
                }
            }
        }

        if (joltage.any { it == 0 }) {
            throw IllegalStateException("failed to find a good voltage for $bank")
        }
        joltage.joinToString("").toLong()
    } == 170147128753455

    private fun loader() = load().map { it.splitToInt() }
}
