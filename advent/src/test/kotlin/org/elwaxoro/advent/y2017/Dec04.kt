package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 4: High-Entropy Passphrases
 */
class Dec04: PuzzleDayTester(4, 2017) {

    /**
     * compare the phrase word length to a set that removes duplicates
     */
    override fun part1(): Any = load().count { line -> 
        val words = line.split(" ")
        words.size == words.toSet().size
    }

    /**
     * same as part 1, but sort each word first. anagrams exist if sorted words match
     */
    override fun part2(): Any = load().count { line ->
        val words = line.split(" ").map { it.toList().sorted().joinToString("") }
        words.size == words.toSet().size
    }
}
