package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rowColSwap

/**
 * Day 3: Squares With Three Sides
 */
class Dec03 : PuzzleDayTester(3, 2016) {

    private val match = Regex("\\W*(\\d+)\\W+(\\d+)\\W+(\\d+)\\W*")

    /**
     * each row = triangle
     */
    override fun part1(): Any = loader().counter()

    /**
     * groups of 3 columns = triangle
     * chunk into groups of 3 rows, swap rows and columns, each row is a triangle again
     */
    override fun part2(): Any = loader().chunked(3).flatMap { it.rowColSwap() }.counter()

    private fun List<List<Int>>.counter() = map { it.sorted() }.filter { it[0] + it[1] > it[2] }.size

    private fun loader() = load().map { match.find(it)!!.destructured.toList().map(String::toInt) }
}
