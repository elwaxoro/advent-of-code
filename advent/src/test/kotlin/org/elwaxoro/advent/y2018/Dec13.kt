package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.Turn

/**
 * Day 13: Mine Cart Madness
 */
class Dec13: PuzzleDayTester(13, 2018) {

    override fun part1(): Any {
        return super.part1()
    }

    override fun part2(): Any {
        return super.part2()
    }

    private fun loader() = load().let { lines ->
        val carts = mutableListOf<Cart>()
    }

    private data class Cart(
        var c: Coord,
        var h: Dir,
        var t: Turn = Turn.L
    )
}
