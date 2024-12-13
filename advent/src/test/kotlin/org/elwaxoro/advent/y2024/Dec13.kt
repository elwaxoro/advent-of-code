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
 * solve for paps
 * p.x - (bap * b.x) = pap * a.x
 * pap = (p.x - (bap * b.x)) / a.x
 * pap = (p.x / a.x) - bap * (b.x / a.x)
 * pap = (p.y / a.y) - bap * (b.y / a.y)
 *
 * extract a couple variables
 * pax = p.x / a.x
 * pay = p.y / a.y
 * bax = b.x / a.x
 * bay = b.y / a.y
 *
 * ahhh much nicer
 * pap = pax - bap * bax
 * pap = pay - bap * bay
 *
 * solve for bap
 * pax - bap * bax = pay - bap * bay
 * pax - pay = bap * bax - bap * bay
 * pax - pay = bap * (bax - bay)
 * bap = (pax - pay) / (bax - bay)
 *
 * if bap and bay are both "close enough" to whole numbers, then solve is good
 * pap * 3 + bap is minimum tokens for each game
 */
class Dec13 : PuzzleDayTester(13, 2024) {

    override fun part1(): Any = loader().sumOf { it.solve() }
    override fun part2(): Any = loader().sumOf { it.solve(10000000000000) }

    private fun List<Coord>.solve(boost: Long = 0L): Long = this.let { (a, b, p) ->
        val pax = (p.x + boost) / a.x.toDouble()
        val pay = (p.y + boost) / a.y.toDouble()
        val bax = b.x / a.x.toDouble()
        val bay = b.y / a.y.toDouble()
        val bap = (pax - pay) / (bax - bay)
        val pap = pay - bap * bay
        (pap.snap() * 3 + bap.snap()).takeIf { pap.slap() && bap.slap() } ?: 0
    }

    private fun loader() = load(delimiter = "\n\n").map { game ->
        game.split("\n").map { it.remove("Button A: X+", "Button B: X+", "Prize: X=", " Y+", " Y=").toCoord() }
    }

    /**
     * "close enough" took me like 4 tries to walk in on part 2
     * started part 1 worked with 0.000001
     */
    private fun Double.slap(): Boolean = abs(this - this.snap()) < 0.001

    // I think I'm funny ok
    private fun Double.snap(): Long = roundToLong()
}
