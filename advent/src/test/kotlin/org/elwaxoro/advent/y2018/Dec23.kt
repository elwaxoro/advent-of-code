package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.BronKerbosch
import org.elwaxoro.advent.Coord3D
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: Experimental Emergency Teleportation
 */
class Dec23 : PuzzleDayTester(23, 2018) {

    override fun part1(): Any = loader().let { nanobots ->
        nanobots.maxBy { it.power }.let { bot ->
            nanobots.count {
                bot.inRange(it)
            }
        }
    } == 933

    /**
     * oh god what the shit is this? max clique??
     * tree search maybe with reducing size boxes??
     * brute force size is waaaay too big
     * max search space is the bounds of the bots positions + their power in every direction
     *
     * yeaaa. I'm not doing this for a previous year. Bron-Kerbosch does the thing, thanks to the excellent blog post at todd.ginsberg.com
     */
    override fun part2(): Any = loader().let { nanobots ->
        val neighbors: Map<Nanobot, Set<Nanobot>> = nanobots.associateWith { bot -> nanobots.filterNot { it == bot }.filter { bot.withinRangeOfSharedPoint(it) }.toSet() }
        val clique: Set<Nanobot> = BronKerbosch(neighbors).largestClique()
        clique.maxOf { it.pos.manhattan(Coord3D()) - it.power }
    } == 70887840

    private fun loader() = load().map { line ->
        val (pos, r) = line.replace("pos=<", "").split(">, r=")
        Nanobot(Coord3D.parse(pos), r.toInt())
    }

    private class Nanobot(
        val pos: Coord3D,
        val power: Int,
    ) {
        fun inRange(other: Nanobot): Boolean = pos.manhattan(other.pos) <= power

        fun withinRangeOfSharedPoint(other: Nanobot): Boolean = pos.manhattan(other.pos) <= power + other.power
    }
}
