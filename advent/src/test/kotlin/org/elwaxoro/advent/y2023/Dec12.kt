package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Hot Springs
 */
class Dec12 : PuzzleDayTester(12, 2023) {

    override fun part1(): Any = loader().sumOf { (record, checksum) -> recursiveSpringalator6001(record, checksum) } == 7344L

    override fun part2() = loader().sumOf { (record, checksum) -> recursiveSpringalator6001(record.repeatSeparator(5), checksum.repeat(5)) } == 1088006519007L

    private fun String.repeatSeparator(size: Int): String = (0 until size).joinToString("") { "$this?" }.dropLast(1)

    private fun List<Int>.repeat(size: Int): List<Int> = (0 until size).flatMap { this }

    private val memo = mutableMapOf<String, Long>()

    /**
     * HashMap was throwing ConcurrentModificationException so just make my own. with blackjack! and hookers!
     */
    private fun computeIfAbsent(key: String, compute: () -> Long): Long =
        if (memo.containsKey(key)) {
            memo.getValue(key)
        } else {
            val result = compute.invoke()
            memo[key] = result
            result
        }

    private fun recursiveSpringalator6001(record: String, checksum: List<Int>): Long = computeIfAbsent(record + checksum.joinToString()) {
        when (record.firstOrNull()) {
            null -> 1L.takeIf { checksum.isEmpty() } ?: 0 // base case: out of records to check
            '.' -> recursiveSpringalator6001(record.drop(1), checksum) // recursive step: throw away the .
            '?' -> listOf('.', '#').sumOf { recursiveSpringalator6001(it + record.drop(1), checksum) } // recursive step: try # and .
            '#' -> if (checksum.isEmpty()) {
                0 // base case: invalid record: ran out of checksums but still have springs
            } else {
                val item = checksum.first()
                if (record.length < item) {
                    0 // base case: invalid record: remaining record isn't big enough for the checksum
                } else {
                    val testing = record.take(item)
                    // if the remainder starts with a '?' swap it for a '.'
                    val remainder = record.drop(item).let { it.takeUnless { it.firstOrNull() == '?' } ?: ('.' + it.drop(1)) }

                    if (testing.all { it == '#' || it == '?' } && remainder.firstOrNull () != '#') {
                        // recursive step: this all looks good so keep going
                        recursiveSpringalator6001(remainder, checksum.drop(1))
                    } else {
                        0 // base case: either the testing chunk couldn't be converted to springs or the next char after the chunk is also a spring
                    }
                }
            }
            else -> 0 // base case: idk
        }
    }

    private fun loader() = load().map { line ->
        val (record, checksum) = line.split(" ")
        record to checksum.split(",").map { it.toInt() }
    }
}
