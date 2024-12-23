package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 22: Monkey Market
 */
class Dec22 : PuzzleDayTester(22, 2024) {

    /**
     * generate all the secrets, get the last one for each seed
     */
    override fun part1(): Any = load().sumOf { it.generateSecrets().last() }

    /**
     * Super lazy idea.
     * For each seed:
     * 1. generate all 2000 secrets and trim to last digit
     * 2. generate all trim diffs and zip with trims (first diff is just first trim)
     * 3. use a windowed view to make key / value pairs of 4 diffs to the last value
     * 4. only keep the first time a 4 diff view happens for each seed
     * 5. group everything by 4-diff key, merge the trim values into one number
     * 6. answer is the largest value
     *
     * ... its very fast, trust me :|
     * ok 2 seconds
     */
    override fun part2(): Any = load().map { it.generateSecrets().map { it % 10 } }.flatMap { trimmed ->
        val diff = listOf(trimmed.first()) + trimmed.zipWithNext { a, b -> b - a }
        trimmed.zip(diff).windowed(4, 1).map { chunk ->
            chunk.map { it.second } to chunk.last().first
        }.distinctBy { it.first }
    }.groupBy { it.first }.map { it.value.sumOf { it.second } }.max()

    private fun String.generateSecrets(count: Int = 2000): List<Long> = secretGenerator().take(count + 1).toList()

    private fun String.secretGenerator() = generateSequence(this.toLong()) { seed ->
        listOf<(input: Long) -> Long>({ it * 64 }, { it / 32 }, { it * 2048 }).fold(seed) { secret, f -> secret.mix(f.invoke(secret)).prune() }
    }

    private fun Long.mix(that: Long): Long = this xor that
    private fun Long.prune(): Long = this % 16777216
}
