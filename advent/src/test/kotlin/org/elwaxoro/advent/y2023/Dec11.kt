package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs

/**
 * Cosmic Expansion
 * in which I totally rewrote part 1 after reading part 2
 */
class Dec11 : PuzzleDayTester(11, 2023) {

    /**
     * 9627977L
     */
    override fun part1(): Any = loader().expandTheGalaxies(2L).countTheDistances()

    /**
     * 644248339497L
     */
    override fun part2(): Any = loader().expandTheGalaxies(1000000L).countTheDistances()

    /**
     * Offset each galaxy coordinate by the number of x and y expansions in play, multiplied by the expansion size
     */
    private fun List<List<Char>>.expandTheGalaxies(multiplier: Long): List<LCoord> {
        val yExpansions = mapIndexedNotNull { index, chars -> index.takeIf { chars.none { it == '#' } } }
        val xExpansions = this[0].mapIndexedNotNull { index, _ -> index.takeIf { none { it[index] == '#' } } }
        return flatMapIndexed { y, row ->
            val yCount = yExpansions.count { it < y }
            row.mapIndexedNotNull { x, c ->
                val xCount = xExpansions.count { it < x }
                LCoord(x + xCount * multiplier - xCount, y + yCount * multiplier - yCount).takeIf { c == '#' }
            }
        }
    }

    private fun List<LCoord>.countTheDistances(): Long = mapIndexed { idx, coord -> drop(idx + 1).sumOf { coord.taxiDistance(it) } }.sum()

    private fun loader(): List<List<Char>> = load().map { it.toList() }

    /**
     * Need a Coord backed by Long instead of Int
     */
    private data class LCoord(
        val x: Long,
        val y: Long
    ) {
        fun taxiDistance(to: LCoord): Long = abs(to.x - x) + abs(to.y - y)
    }
}
