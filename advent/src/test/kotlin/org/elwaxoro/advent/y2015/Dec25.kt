package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 25: Let It Snow
 */
class Dec25 : PuzzleDayTester(25, 2015) {

    override fun part1(): Any {
        val row = 2981 // puzzle input target row
        val col = 3075 // puzzle input target col
        var code = 20151125 // starting code
        var x = 1
        var y = 1
        var height = 1

        // keep going until you hit the target row + col
        while (x != col || y != row) {
            if (y == 1) {
                // top of triangle, move to the new bottom back at column 1
                height++
                y = height
                x = 1
            } else {
                // move up and to the right
                y -= 1
                x += 1
            }
            code = (code * 252533) % 33554393
        }
        return code
    }

    override fun part2(): Any = "Merry Christmas!"
}