package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 3: Rucksack Reorganization
 */
class Dec03 : PuzzleDayTester(3, 2022) {

    private val priority = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    /**
     * split each line in half, find the shared unique item, score it
     */
    override fun part1(): Any = load().sumOf { it.toList().windowed(it.length / 2, it.length / 2).let { priority.indexOf(it[0].intersect(it[1]).single()) + 1 } }// == 7990

    /**
     * every 3 lines, find the unique shared item, score it
     */
    override fun part2(): Any = load().map { it.toSet() }.windowed(3, 3).sumOf { priority.indexOf(it[0].intersect(it[1]).intersect(it[2]).single()) + 1 }// == 2602
}
