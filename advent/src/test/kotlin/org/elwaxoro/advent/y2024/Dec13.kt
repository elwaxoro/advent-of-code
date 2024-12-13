package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.remove
import org.elwaxoro.advent.toCoord
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Day 13: Claw Contraption
 *
 * I WAS TOLD THERE WOULD BE NO MATH
 *
 * prize at p.x, p.y
 * pap = presses of A button moving a.x, a.y each time
 * bap = presses of B button moving b.x, b.y each time
 * p.x = (pap * a.x) + (bap * b.x)
 * p.y = (pap * a.y) + (bap * b.y)
 *
 * solve for pap
 * p.x - (bap * b.x) = pap * a.x
 * (p.x - (bap * b.x) / a.x = pap
 * (p.x / a.x) - bap * (b.x / a.x) = pap
 * (p.y / a.y) - bap * (b.y / a.y) = pap
 *
 * extract a couple variables
 * pax = p.x / a.x
 * pay = p.y / a.y
 * bax = b.x / a.x
 * bay = b.y / a.y
 *
 * ahhh much nicer
 * pax - bap * bax = pap
 * pay - bap * bay = pap
 *
 * solve for bap
 * pax - bap * bax = pay - bap * bay
 * pax - pay = bap * bax - bap * bay
 * pax - pay = bap * (bax - bay)
 * (pax - pay) / (bax - bay) = bap
 *
 * if bap and bay are "close enough" to whole numbers, the solve is good
 */
class Dec13 : PuzzleDayTester(13, 2024) {

    override fun part1(): Any = loader().sumOf { it.solve() }
    override fun part2(): Any = loader().sumOf { it.solve(10000000000000) }

    private fun List<Coord>.solve(boost: Long = 0L): Long {
        val (a, b, p) = this
        val pax = (p.x + boost) / a.x.toDouble()
        val pay = (p.y + boost) / a.y.toDouble()
        val bax = b.x / a.x.toDouble()
        val bay = b.y / a.y.toDouble()
        val paxy = pax - pay
        val baxy = bax - bay
        val bap = paxy / baxy
        val pap = pay - (bap * bay)
        return if (bap.isCloseToLong() && pap.isCloseToLong()) {
            val tokens = pap.roundToLong() * 3 + bap.roundToLong()
            tokens
        } else {
            0
        }
    }

    private fun loader() = load(delimiter = "\n\n").map { game ->
        game.split("\n").map { it.remove("Button A: X+", "Button B: X+", "Prize: X=", " Y+", " Y=").toCoord() }
    }

    private fun Double.isCloseToLong(): Boolean = abs(this - this.roundToLong()) < 0.001
}
