package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.contains
import kotlin.Int.Companion.MAX_VALUE
import kotlin.collections.set

/**
 * Day 18: RAM Run
 * Input observations: every byte is in-bounds of 0,0 thru 70,70 and there are no duplicates
 */
class Dec18 : PuzzleDayTester(18, 2024) {

    // bounds contains the size of the grid AND represents start/end points
    private val bounds = Pair(Coord(0, 0), Coord(70, 70))

    override fun part1(): Any = explore(loader().take(1024))

    /**
     * First try: brute force from size 1025 up till first failure (25s runtime)
     * Refactor: binary search (70 ms runtime)
     */
    override fun part2(): Any = loader().let { it[binaryExplore(it)!!] }

    /**
     * Basically a copy of Dec16 with extras torn out
     */
    private fun explore(bytes: List<Coord>): Int {
        val explore = mutableListOf(bounds.first)
        val cost = mutableMapOf(bounds.first to 0)
        while (explore.isNotEmpty()) {
            val ex = explore.removeFirst()
            if (ex != bounds.second) {
                val c = (cost[ex] ?: 0) + 1
                ex.neighbors().filter { it !in bytes && bounds.contains(it) && (cost[it] ?: MAX_VALUE) > c }.map {
                    explore.add(it)
                    cost[it] = c
                }
            }
        }
        return cost[bounds.second] ?: MAX_VALUE
    }

    private fun binaryExplore(bytes: List<Coord>, low: Int = 0, high: Int = bytes.size): Int? =
        if (low == high) {
            low.takeIf { explore(bytes.take(low)) != MAX_VALUE }
        } else {
            val mid = low + (high - low) / 2
            val score = explore(bytes.take(mid))
            if (score != MAX_VALUE) {
                binaryExplore(bytes, mid + 1, high) ?: mid
            } else {
                binaryExplore(bytes, low, mid - 1)
            }
        }

    private fun loader() = load().map { Coord.parse(it) }
}
