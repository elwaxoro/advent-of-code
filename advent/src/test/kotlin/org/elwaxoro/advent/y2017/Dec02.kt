package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 2: Corruption Checksum
 */
class Dec02: PuzzleDayTester(2, 2017)  {

    /**
     * 21845
     */
    override fun part1(): Any = loader().sumOf { it.last() - it.first() }

    /**
     * 191
     */
    override fun part2(): Any = loader().sumOf { row ->
        row.mapIndexedNotNull { indexA, a ->
            (indexA + 1 until row.size).mapNotNull { indexB ->
                (row[indexB] / a).takeIf { row[indexB] % a == 0 }
            }.singleOrNull()
        }.single()
    }

    private fun loader() = load().map {
        it.split("\\s+".toRegex()).map { it.toInt() }.sorted()
    }
}
