package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.Coord3D
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.lcm
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.sign

/**
 * Day 12: The N-Body Problem
 */
class Dec12 : PuzzleDayTester(12, 2019) {

    /**
     * Ok pretty easy. move the moons 1000 times and do some math. ez-pz
     */
    override fun part1(): Any = (1..1000).fold(loader()) { moons, _ ->
        moons.step()
    }.sumOf { it.first.absSum() * it.second.absSum() }// == 9139

    /**
     * CRAAAAP this will never finish on its own.
     * Instead, lets split each axis and see when all the moons converge on their starting x position & x velocity (or y, or z)
     * Once the period is known for each separate axis, find the least common multiple of all 3 axis: this is the iteration count needed to return to start
     * Full disclosure: I needed some hints on this one :rip:
     */
    override fun part2(): Any = loader().let { initialMoons ->
        var seekX = -1L
        var seekY = -1L
        var seekZ = -1L
        var idx = 0L
        var moons = initialMoons
        while (seekX < 1 || seekY < 1 || seekZ < 1) {
            idx++
            moons = moons.step()
            if (seekX < 1 && initialMoons.zip(moons).all { (a, b) -> a.first.x == b.first.x && a.second.x == b.second.x }) {
                seekX = idx
            }
            if (seekY < 1 && initialMoons.zip(moons).all { (a, b) -> a.first.y == b.first.y && a.second.y == b.second.y }) {
                seekY = idx
            }
            if (seekZ < 1 && initialMoons.zip(moons).all { (a, b) -> a.first.z == b.first.z && a.second.z == b.second.z }) {
                seekZ = idx
            }
        }
        listOf(seekX, seekY, seekZ).map { BigInteger("$it") }.lcm().toLong()
    }// == 420788524631496

    private fun List<Pair<Coord3D, Coord3D>>.step(): List<Pair<Coord3D, Coord3D>> =
        mapIndexed { _, moon ->
            val newVel = this.minus(moon).fold(moon.second) { acc, (otherPos, _) ->
                val dx = (otherPos.x - moon.first.x).sign
                val dy = (otherPos.y - moon.first.y).sign
                val dz = (otherPos.z - moon.first.z).sign
                acc.add(dx, dy, dz)
            }
            val newPos = moon.first.add(newVel)
            newPos to newVel
        }

    private fun Coord3D.absSum() = abs(x) + abs(y) + abs(z)


    private fun loader() = load().map {
        // LAZY
        Coord3D.parse(it.replace("<x=", "").replace(" y=", "").replace(" z=", "").replace(">", "")) to Coord3D()
    }
}
