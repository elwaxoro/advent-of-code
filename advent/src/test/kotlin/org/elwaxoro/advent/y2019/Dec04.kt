package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.splitToInt

/**
 * Day 4: Secure Container
 * Brute forcing this, runs in less than 100ms so I guess its fine
 */
class Dec04 : PuzzleDayTester(4, 2019, 2814, 1991) {

    override fun part1(): Any = (109165..576723).map { it.toString().splitToInt() }.filter { it.isAscending() }.count { it.hasRepeats() }
    override fun part2(): Any = (109165..576723).map { it.toString().splitToInt() }.filter { it.isAscending() }.count { it.hasRepeats(2) }

    private fun List<Int>.isAscending(): Boolean = zipWithNext { a, b -> a <= b }.all { it }

    /**
     * Dumb. Wanted a variable sized sliding window but couldn't think of a kotlin-y way to get that
     */
    private fun List<Int>.hasRepeats(maxRepeat: Int = Int.MAX_VALUE): Boolean {
        var idx = 0
        while (idx < size - 1) {
            var count = 1
            // grow the count to the size of every matched sequential number
            while (idx + count < size && this[idx] == this[idx + count]) {
                count++
            }
            // Anything over 1 is fine for part 1, anything exactly 2 is required for part 2
            if (count in 2..maxRepeat) {
                return true
            }
            idx += count
        }
        return false
    }
}
