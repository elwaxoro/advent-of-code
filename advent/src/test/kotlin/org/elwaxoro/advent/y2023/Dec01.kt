package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 1: Trebuchet?!
 */
class Dec01 : PuzzleDayTester(1, 2023) {

    /**
     * 54953
     */
    override fun part1(): Any = load().map { it.replace("[a-zA-Z]*".toRegex(), "") }.sumOf { "${it.first()}${it.last()}".toInt() }

    /**
     * Ew, David!
     * 53868
     */
    override fun part2(): Any = load().map {
        it.replace("one", "one1one")
            .replace("two", "two2two")
            .replace("three", "three3three")
            .replace("four", "four4four")
            .replace("five", "five5five")
            .replace("six", "six6six")
            .replace("seven", "seven7seven")
            .replace("eight", "eight8eight")
            .replace("nine", "nine9nine")
            .replace("[a-zA-Z]*".toRegex(), "")
    }.sumOf { "${it.first()}${it.last()}".toInt() }
}
