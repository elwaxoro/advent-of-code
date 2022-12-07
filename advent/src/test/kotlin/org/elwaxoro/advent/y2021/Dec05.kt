package org.elwaxoro.advent.y2021

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Hydrothermal Venture
 */
class Dec05 : PuzzleDayTester(5, 2021) {

    override fun part1(testFileSuffix: Int?): Any = parse().filter { it.first().x == it.last().x || it.first().y == it.last().y }.enumerateAndCount()

    override fun part2(testFileSuffix: Int?): Any = parse().enumerateAndCount()

    private fun List<List<Coord>>.enumerateAndCount() = flatMap { it.first().enumerateLine(it.last()) }.groupingBy { it }.eachCount().filter { it.value > 1 }.size

    private fun parse(): List<List<Coord>> = load().map { it.split(" -> ").map(Coord::parse) }
}
