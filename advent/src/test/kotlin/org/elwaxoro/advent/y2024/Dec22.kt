package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 22: Monkey Market
 */
class Dec22: PuzzleDayTester(22, 2024) {

    private fun Long.nextSecret(): Long {
        var nextSecret = this.mix(this * 64).prune()
        nextSecret = nextSecret.mix(nextSecret / 32).prune()
        nextSecret = nextSecret.mix(nextSecret * 2048).prune()
        return nextSecret
    }

    private fun Long.mix(that: Long): Long = this xor that
    private fun Long.prune(): Long = this % 16777216

    override fun part1(): Any = load().sumOf { (1..2000).fold(it.toLong()) { acc, _ -> acc.nextSecret() } }

    /**
     * 2039 is too low
     */
    override fun part2(): Any = load().flatMap { startSecret ->
        val secrets = (1..2000).fold(mutableListOf(startSecret.toLong())) { acc, _ -> acc.also { it.add(it.last().nextSecret()) }}
        val trimmed = secrets.map { it % 10 }
        val diff = listOf(trimmed.first()) + trimmed.zipWithNext { a, b -> b-a }
        trimmed.zip(diff).windowed(4, 1).map { chunk ->
            val key = chunk.joinToString(",") { "${it.second}" }
            val value = chunk.last().first
            key to value
        }.distinctBy { it.first }
    }.groupBy { it.first }.map {
        it.value.sumOf { it.second }
    }.max()
}
