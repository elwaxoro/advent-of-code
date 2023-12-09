package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

class Dec09: PuzzleDayTester(9, 2023) {

    /**
     * 1868368343
     */
    override fun part1(): Any = loader().map { line ->
        generateHistory(line).last()
    }.sum() == 1868368343L

    private fun generateHistory(line: List<Long>, lastNums: List<Long> = listOf()): List<Long> =
        if (line.all { it == 0L }) {
            // base case! everything has been reduced to zero, time to build it back up
            val diffList = mutableListOf(0L)
            lastNums.reversed().fold(0L) { acc, l ->
                (l + acc).also { diffList.add(it) }
            }
            diffList
        } else {
            val diffs = line.mapIndexedNotNull { idx, l ->
                if (idx == line.size - 1) {
                    null
                } else {
                    line[idx+1]-l
                }
            }
            generateHistory(diffs, lastNums.plus(line.last()))
        }

    override fun part2(): Any = loader().map { line ->
        generateHistory2(line).last()
    }.sum()

    private fun generateHistory2(line: List<Long>, firstNums: List<Long> = listOf()): List<Long> =
        if (line.all { it == 0L }) {
            // base case! everything has been reduced to zero, time to build it back up
            val diffList = mutableListOf(0L)
            firstNums.reversed().fold(0L) { acc, l ->
                (l - acc).also { diffList.add(it) }
            }
            diffList
        } else {
            val diffs = line.mapIndexedNotNull { idx, l ->
                if (idx == line.size - 1) {
                    null
                } else {
                    line[idx+1]-l
                }
            }
            generateHistory2(diffs, firstNums.plus(line.first()))
        }

    private fun loader() = load().map { it.split(" ").map { it.toLong() } }
}
