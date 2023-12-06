package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Wait For It
 */
class Dec06 : PuzzleDayTester(6, 2023) {

    /**
     * 4811940
     */
    override fun part1(): Any = parse1().map { (raceTime, record) -> race(raceTime, record) }.reduce { acc, i -> acc * i }

    /**
     * 30077773
     */
    override fun part2(): Any = parse2().let { (raceTime, record) -> race(raceTime, record) }

    private fun race(raceTime: Long, record: Long): Long = (0L..raceTime).fold(0) { acc, buttonTime ->
        (acc + 1L).takeIf { (raceTime - buttonTime) * buttonTime > record } ?: acc
    }

    private fun parse1() = load().map {
        it.replace("[a-zA-Z:]".toRegex(), "")
            .trim()
            .split("\\W+".toRegex())
            .map { it.toLong() }
    }.let { (times, distances) ->
        times.zip(distances)
    }

    private fun parse2() = load().map {
        it.replace("[a-zA-Z:\\W]".toRegex(), "").trim().toLong()
    }
}
