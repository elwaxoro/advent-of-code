package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.takeSplit

/**
 * Day 11: Plutonian Pebbles
 */
class Dec11 : PuzzleDayTester(11, 2024) {

    /**
     * Initially solved with an expanding string to store stones in the correct order
     * Part 2 revealed none of that was needed so refactored to just use part 2 solution with part 1
     */
    override fun part1(): Any = loader().blink(25) == 199753L

    /**
     * Despite the instructions stating "order is preserved",
     * the puzzle does not care about order and the stones never interact
     */
    override fun part2(): Any = loader().blink(75) == 239413123020116L

    private fun Map<String, Long>.blink(count: Int): Long = (1..count).fold(this) { stones, _ ->
        mutableMapOf<String, Long>().also { newStones ->
            stones.map { (stone, count) ->
                (if (stone == "0") {
                    listOf("1")
                } else if (stone.length % 2 == 0) {
                    stone.takeSplit(stone.length / 2).map { "${it.toLong()}" }
                } else {
                    listOf("${stone.toLong() * 2024}")
                }).forEach { newStones[it] = newStones.getOrDefault(it, 0) + count }
            }
        }
    }.values.sum()

    private fun loader() = load().single().split(" ").groupBy { it }.map { it.key to it.value.size.toLong() }.toMap()
}
