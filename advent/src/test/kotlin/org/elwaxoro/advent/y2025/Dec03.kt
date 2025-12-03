package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.splitToInt

/**
 * Day 3: Lobby
 *
 * Part1 idea: loop each bank once, pick primary and secondary maximums as you go, don't reset primary if almost to end of bank
 * Part2 idea: same as part 1, but with a list, don't reset max N if bankIdx >= bank.size - 11 + idxN (ie leave room for the remaining batteries that need a max)
 * After solving part2 - it solves part1 just fine too
 */
class Dec03 : PuzzleDayTester(3, 2025) {

    override fun part1(): Any = loader().sumOf { it.joltage(2) } == 17092L

    /**
     * Turn on 12 batteries instead of 2
     * same approach but with a list?
     * battery > n && idx < bank.size - 11 + n?
     * ie: don't select a new max if there's not enough room left in the bank for the remaining digits
     */
    override fun part2(): Any = loader().sumOf { it.joltage(12) } == 170147128753455

    private fun List<Int>.joltage(count: Int): Long {
        val joltage = MutableList(count) { 0 }
        forEachIndexed { idx, battery ->
            var found = false
            var foundIdx = -1
            joltage.forEachIndexed { jidx, max ->
                if (battery > max && idx < size - (count - 1) + jidx && !found) {
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
            throw IllegalStateException("failed to find a good voltage for $this")
        }
        return joltage.joinToString("").toLong()
    }

    private fun loader() = load().map { it.splitToInt() }
}
