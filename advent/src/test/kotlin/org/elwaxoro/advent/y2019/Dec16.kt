package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.splitToInt

/**
 * Day 16: Flawed Frequency Transmission
 */
class Dec16 : PuzzleDayTester(16, 2019) {

    private val pattern = listOf(0, 1, 0, -1)

    /**
     * Looks like a lot of sequence generation,
     * prob some folds with offsets and other nonsense
     */
    override fun part1(): Any = (1..100).fold(loader()) { acc, _ -> acc.phase() }.joinToString("").take(8) == "30369587"

    /**
     * IDK I got some help here wtf is even happening
     */
    override fun part2(): Any = loader().let { input ->
        val offset = input.take(7).joinToString("").toInt()
        val stretched = (offset until 10_000 * input.size).map { input[it % input.size] }.toIntArray()
        repeat(100) {
            stretched.indices.reversed().fold(0) { c, i ->
                (stretched[i] + c).last().also { stretched[i] = it }
            }
        }
        stretched.take(8).joinToString("")
    }

    private fun List<Int>.phase(): List<Int> =
        (1..size).map { i ->
            val c = pattern.pattern(i, size)
            mapIndexed { idx, ie -> (ie * c[idx]) }.sum().last()
        }

    private fun List<Int>.pattern(patternSize: Int, phaseSize: Int): List<Int> = (0..phaseSize / patternSize).flatMap { i -> List(patternSize) { this[i % size] } }.drop(1)
    private fun Int.last() = "$this".last().digitToInt()
    private fun loader() = load().single().splitToInt()
}