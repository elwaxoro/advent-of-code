package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 6: Tuning Trouble
 */
class Dec06 : PuzzleDayTester(6, 2022) {

    /**
     * start-of-packet: first 4 unique chars in a row
     */
    override fun part1(): Any = findPacket(4)// == 1300

    /**
     * start-of-message: first 14 unique chars in a row
     */
    override fun part2(): Any = findPacket(14)// == 3986

    private fun findPacket(size: Int): Int = load().first().toList().let { input ->
        // could look forwards then add the size or backwards and add nothing
        (size..input.size).first { endIdx ->
            input.subList(endIdx - size, endIdx).distinct().size == size
        }
    }
}
