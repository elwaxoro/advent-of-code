package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 11: Plutonian Pebbles
 * each tick:
 * 0 becomes 1
 * even becomes 2 (left half of digits go left, right half of digits go right 1000 = 10 and 0)
 * else stone becomes stone * 2024
 * preserve order
 */
class Dec11: PuzzleDayTester(11, 2024) {

    override fun part1(): Any = load().single().let { input ->
        println(input)
        var current = input
        (1..25).forEach {
            current = current.blink()
        }
        current.split(" ").size
    }

    private fun String.blink(): String = split(" ").joinToString(" ") {
        val i = it.toLong()
        if (i == 0L) {
            "1"
        } else if (it.length % 2 == 0) {
            "${it.take(it.length / 2).toLong()} ${it.takeLast(it.length / 2).toLong()}"
        } else {
            "${i * 2024}"
        }
    }

    /**
     * Observation: the stones never interact and we don't care about order
     */
    override fun part2(): Any = load().single().let { input ->
        var stones: Map<String, Long> = input.split(" ").groupBy { it }.map { it.key to it.value.size.toLong() }.toMap()
//        (1..75).fold()
        (1..75).forEach { _ ->
            val newStones: MutableMap<String, Long> = mutableMapOf()
            stones.map { (stone, count) ->
                if (stone == "0") {
                    newStones["1"] = newStones.getOrDefault("1", 0) + count
                } else if (stone.length % 2 == 0) {
                    val a = "${stone.take(stone.length / 2).toLong()}"
                    val b = "${stone.takeLast(stone.length / 2).toLong()}"
                    newStones[a] = newStones.getOrDefault(a, 0) + count
                    newStones[b] = newStones.getOrDefault(b, 0) + count
                } else {
                    val a = "${stone.toLong() * 2024}"
                    newStones[a] = newStones.getOrDefault(a, 0) + count
                }
            }
            stones = newStones
        }
        stones.values.sum()
    }
}
