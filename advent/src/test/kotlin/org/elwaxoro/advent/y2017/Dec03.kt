package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs

/**
 * Day 3: Spiral Memory
 *
 * 17  16  15  14  13
 * 18   5   4   3  12
 * 19   6   1   2  11
 * 20   7   8   9  10
 * 21  22  23  24 ... >
 *
 * sq 0: 1 (1x1) 1 to 1
 * sq 1: 8*1 = 8 (3x3) 2 to 9
 * sq 2: 8*2 = 16 (5x5) 10 to 25
 * sq 3: 8*3 = 24 (7x7) 26 to 49
 * sq 4: 8*4 = 32 (9x9) 50 to 81
 */
class Dec03: PuzzleDayTester(3, 2017)  {

    /**
     * 438
     * prob a better way to do this...
     * size of each shell increases by 8
     * size of each side increases by 2
     * starting idx is previous shell ending idx + 1
     * ending idx is the square of the side size
     * first: find the correct shell
     * second: find the closest midpoint of any side
     * third: manhattan distance is just "get to midpoint" then "go to center" (shell count)
     */
    override fun part1(): Any = "265149".toInt().let { pos ->
        var shell = 0
        var shellSide = 1
        var shellMin = 1
        var shellMax = 1
        while(shellMax < pos) {
            shell += 1
            shellSide += 2
            shellMin = shellMax + 1
            shellMax = shellSide * shellSide
        }
        val mid = shellSide / 2
        val mid1 = shellMin + mid - 1
        val mid2 = mid1 + shellSide - 1
        val mid3 = mid2 + shellSide - 1
        val mid4 = mid3 + shellSide - 1
        val minDist = listOf(mid1, mid2, mid3, mid4).map { abs(pos - it) }.minOf { it }
        shell + minDist
    }

    /**
     * welp. being tricky in part 1 doesn't help me here.
     * thinking we need a full generator here
     */
    override fun part2(): Any = "Someday!"
}
