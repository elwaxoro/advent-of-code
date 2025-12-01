package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 15: Timing is Everything
 * heck yea first try! part 2 thought for sure the search space would be intractable for brute force, but it ran just perfectly!
 */
class Dec15 : PuzzleDayTester(15, 2016) {

    override fun part1(): Any = loader().findFirstTime() == 203660

    override fun part2(): Any = loader().plus(Disc(7, 11, 0)).findFirstTime() == 2408135

    private fun List<Disc>.findFirstTime() =
        generateSequence(0) { it + 1 }.first { time ->
            all { disc ->
                (disc.order + time + disc.start) % disc.positions == 0
            }
        }

    private val regex = """Disc #(\d+) has (\d+) positions; at time=0, it is at position (\d+).""".toRegex()

    private fun loader() = load().map { line ->
        val (order, positions, start) = regex.find(line)!!.destructured
        Disc(order.toInt(), positions.toInt(), start.toInt())
    }

    private data class Disc(
        val order: Int,
        val positions: Int,
        val start: Int,
    )
}
