package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.remove
import org.elwaxoro.advent.toCoord
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Day 13: Claw Contraption
 * I WAS TOLD THERE WOULD BE NO MATH
 */
class Dec13: PuzzleDayTester(13, 2024) {
    // 78101482023732
    // 72804396907562 is too low
    // 28774107866398 is too low
    val p2 = 10000000000000

    override fun part1(): Any = loader().sumOf { (a, b, p) ->
        val pax = (p.x+p2) / a.x.toDouble()
        val pay = (p.y+p2) / a.y.toDouble()
        val bax = b.x / a.x.toDouble()
        val bay = b.y / a.y.toDouble()
        val pp = pax - pay
        val bb = bax - bay
        val m = pp / bb // b button presses
        val n = pay - (m * bay) // a button presses

//        println("$a $b $p")
//        if (m.isCloseToLong() && n.isCloseToLong()) {
//            println("SOLVED!")
//            println("Press A: $n = ${n.roundToLong()}")
//            println("Press B: $m = ${m.roundToLong()}")
//        } else {
//            println("NO SOLUTION")
//            println("press A: $n")
//            println("press B: $m")
//            println("N: ${n.roundToLong()}")
//            println("M: ${m.roundToLong()}")
//        }

        if (m.isCloseToLong() && n.isCloseToLong()) {
            val tokens = n.roundToLong() * 3 + m.roundToLong()
            tokens
        } else {
            0
        }
    }

    override fun part2(): Any {
        return super.part2()
    }

    private fun List<Coord>.solve(boost: Long = 1L): Long {
        
    }

    private fun loader() = load(delimiter = "\n\n").map { game ->
        game.split("\n").map { it.remove("Button A: X+", "Button B: X+", "Prize: X=", " Y+", " Y=").toCoord() }
    }

    private fun Double.isCloseToLong(): Boolean = abs(this - this.roundToLong()) < 0.001
}
