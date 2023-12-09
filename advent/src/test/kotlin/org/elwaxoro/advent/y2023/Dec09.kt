package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

class Dec09: PuzzleDayTester(9, 2023) {

    /**
     * 1868368343
     */
    override fun part1(): Any = loader().sumOf { line ->
        predictor9000(line, listOf(), { it.last() }, { acc, l -> l + acc })
    }

    /**
     * 1022
     */
    override fun part2(): Any = loader().sumOf { line ->
        predictor9000(line, listOf(), { it.first() }, { acc, l -> l - acc })
    }

    /**
     * Basic idea: recursively reduce the line by subtracting each item from the next in the list until everything is zeros (line)
     * At each recursion: also keep track of either the last (part 1) or first (part 2) item in the input list (tracker)
     * At base case: combine tracker back up to the top using the combiner function
     */
    private fun predictor9000(line: List<Long>, tracker: List<Long>, selector: (line: List<Long>) -> Long, combiner: (acc: Long, l: Long) -> Long): Long =
        if (line.all { it == 0L }) {
            tracker.reversed().fold(0L) { acc, l ->
                combiner(acc, l)
            }
        } else {
            val diffs = line.mapIndexedNotNull { idx, l ->
                if (idx == line.size - 1) {
                    null
                } else {
                    line[idx+1]-l
                }
            }
            predictor9000(diffs, tracker.plus(selector(line)), selector, combiner)
        }

    private fun loader() = load().map { it.split(" ").map { it.toLong() } }
}
