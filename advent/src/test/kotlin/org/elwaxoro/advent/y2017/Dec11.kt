package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs

class Dec11: PuzzleDayTester(11, 2017) {

    override fun part1(): Any = loader().let { moves -> 
        val start = Hex(0, 0, 0)
        val end = moves.fold(start) { acc, dir -> 
            acc.move(dir)
        }
        start.distance(end)
    }

    override fun part2(): Any = loader().let { moves ->
        val start = Hex(0, 0, 0)
        var maxDist = 0
        val end = moves.fold(start) { acc, dir ->
            acc.move(dir).also { 
                val dist = start.distance(it)
                if (dist > maxDist) {
                    maxDist = dist
                }
            }
        }
        maxDist
    }

    private fun loader() = loadToString().split(",").map { HexDir.valueOf(it.uppercase()) }
    
    private enum class HexDir { N, S, NE, NW, SE, SW }

    /**
     * Hex coordinates based on https://www.redblobgames.com/grids/hexagons/
     * Uses cube coordinates in the "flat top" orientation
     */
    private data class Hex(val q: Int, val r: Int, val s: Int) {

        fun move(dir: HexDir): Hex =
            when (dir) {
                HexDir.N -> Hex(q, r - 1, s + 1)
                HexDir.S -> Hex(q, r + 1, s - 1)
                HexDir.NE -> Hex(q + 1, r - 1, s)
                HexDir.NW -> Hex(q - 1, r, s + 1)
                HexDir.SE -> Hex(q + 1, r, s - 1)
                HexDir.SW -> Hex(q - 1, r + 1, s)
            }

        fun neighbors(): List<Hex> = HexDir.entries.map { move(it) }
        
        fun distance(that: Hex): Int = (abs(q - that.q) + abs(r - that.r) + abs(s - that.s)) / 2
    }
}
