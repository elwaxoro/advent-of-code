package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.max

/**
 * Day 5: Alchemical Reduction
 */
class Dec05 : PuzzleDayTester(5, 2018) {

    override fun part1(): Any = reduce(load().single()).length

    override fun part2(): Any = load().single().let { input ->
        ('a'..'z').minOf {
            reduce(input.replace("$it", "", true)).length
        }
    }

    /**
     * find the first letter matching its neighbor, but opposite case - remove both and back index up by 1
     * repeat until no more matches found
     */
    private tailrec fun reduce(input: String, idx: Int = 0): String =
        if (idx == input.length - 1) {
            input
        } else {
            val a = input[idx]
            val b = input[idx + 1]
            if (a != b && a.equals(b, true)) {
                reduce(input.substringBefore("$a$b") + input.substringAfter("$a$b"), max(idx - 1, 0))
            } else {
                reduce(input, idx + 1)
            }
        }
}
