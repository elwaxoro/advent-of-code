package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Never Tell Me The Odds
 *
 * Yea... I can't claim original code on this at all, got lots of help from the subreddit
 */
class Dec24 : PuzzleDayTester(24, 2023) {

    /**
     * 14672
     */
    override fun part1(): Any = loader().let { storm ->
        storm.mapIndexed { index, hail ->
            storm.drop(index).filter { otherHail ->
                hail.intersectsXY(otherHail)
            }
        }.flatten().size
    }

    override fun part2(): Any = "uhhhh I might give up"

    /**
     * Copy of Coord3D here to store everything as double instead of int
     */
    private data class Coord3D(val x: Double, val y: Double, val z: Double) {
        companion object {
            fun parse(string: String): Coord3D = string.split(",").map { it.trim() }.let { (a, b, c) ->
                Coord3D(a.toDouble(), b.toDouble(), c.toDouble())
            }
        }
    }

    /**
     * Wrapper for 2 Coord3D instead of just pair
     */
    private data class Hail(val p: Coord3D, val v: Coord3D) {

        val targetRange = 200000000000000.0..400000000000000.0

        private fun slope() = v.y / v.x

        fun intersectsXY(that: Hail): Boolean =
            if (slope() == that.slope()) {
                false
            } else {
                val cx = ((that.slope() * that.p.x) - (slope() * p.x) + p.y - that.p.y) / (that.slope() - slope())
                val cy = (slope() * (cx - p.x)) + p.y

                cx in targetRange && cy in targetRange && this.isValid(cx, cy) && that.isValid(cx, cy)
            }

        fun isValid(cx: Double, cy: Double) = !((v.x < 0 && p.x < cx) || (v.x > 0 && p.x > cx) || (v.y < 0 && p.y < cy) || (v.y > 0 && p.y > cy))
    }

    private fun loader() = load().map { line ->
        val (loc, vel) = line.split(" @ ").map(Coord3D::parse)
        Hail(loc, vel)
    }
}
