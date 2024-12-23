package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 22: Monkey Market
 */
class Dec22: PuzzleDayTester(22, 2024) {

    /**
     * generate all the secrets, get the last one for each seed
     */
    override fun part1(): Any = load().sumOf { it.generateSecrets().last() }

    override fun part2(): Any = load().flatMap { seed ->
        val trimmed = seed.generateSecrets().map { it % 10 }
        val diffs = listOf(trimmed.first()) + trimmed.zipWithNext { a, b -> b-a }
        trimmed.zip(diffs).windowed(4, 1).map { chunk ->
            chunk.joinToString(",") { "${it.second}" } to chunk.last().first
        }.distinctBy { it.first }
    }.groupBy { it.first }.map { it.value.sumOf { it.second } }.max()

    private fun String.generateSecrets(): List<Long> = (1..2000).fold(mutableListOf(this.toLong())) { acc, _ -> acc.also { it.add(it.last().nextSecret()) }}

    private fun Long.nextSecret(): Long {
        var nextSecret = this.mix(this * 64).prune()
        nextSecret = nextSecret.mix(nextSecret / 32).prune()
        nextSecret = nextSecret.mix(nextSecret * 2048).prune()
        return nextSecret
    }

    private fun Long.mix(that: Long): Long = this xor that
    private fun Long.prune(): Long = this % 16777216
}
