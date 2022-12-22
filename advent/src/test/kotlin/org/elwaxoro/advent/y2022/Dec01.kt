package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 1: Calorie Counting
 */
class Dec01: PuzzleDayTester(1, 2022) {

    override fun part1(): Any = loader().last()// == 70374

    override fun part2(): Any = loader().takeLast(3).sum()// == 204610

    private fun loader(): List<Int> = load(delimiter = "\n\n").map { it.split("\n").sumOf(String::toInt) }.sorted()
}
