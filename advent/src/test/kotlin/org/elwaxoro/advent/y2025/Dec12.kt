package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.multiply

/**
 * Day 12: Christmas Tree Farm
 */
class Dec12: PuzzleDayTester(12, 2025) {

    /**
     * ARRRG I have no idea how to start doing this
     * idea: filter out the obvious possible/impossible combos
     * definite yes: area of tree >= grid area of all gifts. do nothing fancy just slap em in there ignoring gaps
     * definite no: area of tree < filled grid area of all gifts. even with perfect orientation, the gifts can't fit
     * maybe: tetris time. maybe a heuristic for combos of gifts idx that fit well to start? inputs are WAY too big for perfect packing
     */
    override fun part1(): Any = loader().filter { (area, gifts) -> 
        // definite yes test first
        area >= gifts.sumOf { gift -> 9 * gift }
    }.size == 451

    override fun part2(): Any = "Merry Christmas!"

    /**
     * added an extra newline to separate gifts from trees to make parsing easier
     */
    private fun loader() = load(delimiter = "\n\n\n").let { (gifts, trees) ->
        // each gift is a 3x3 with various filled/open spaces inside
        gifts.split("\n\n").map { lines ->
            // TODO
        }

        // each tree is area and the required gifts to go inside
        // 45x43: 21 39 38 39 38 35
        trees.split("\n").map { line ->
            val (area, targets) = line.split(": ")
            area.split("x").map { it.toInt() }.multiply() to targets.split(" ").map { it.toInt() }
        }
    }
}