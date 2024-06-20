package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.Dir.*
import org.elwaxoro.advent.LCoord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.picksTheorem
import org.elwaxoro.advent.shoelaceArea

/**
 * Lavaduct Lagoon
 */
class Dec18 : PuzzleDayTester(18, 2023) {

    /**
     * Initially did this with flood fill, which worked fine
     * 50603
     */
    override fun part1(): Any = load().map { line ->
        // just throw the last bit about color away, unused
        line.split(" ").dropLast(1).let { it.first() to it.last().toLong() }
    }.digItUp()

    /**
     * shoelace + pick's, which solves part 1 after refactor
     * 96556251590677
     */
    override fun part2(): Any = load().map {
        // throw the first stuff away, convert the "color" into a distance + command
        val cleaned = it.split(" ").last().replace("[()#]*".toRegex(), "")
        val dist = cleaned.take(5).toLong(16)
        val cmd = when (cleaned.last()) {
            '0' -> "R"
            '1' -> "D"
            '2' -> "L"
            '3' -> "U"
            else -> throw IllegalStateException("augh!")
        }
        cmd to dist
    }.digItUp()

    private fun List<Pair<String, Long>>.digItUp(): Long = fold(listOf(LCoord(0, 0))) { dug, (cmd, dist) ->
        val newHeading = when (cmd) {
            "U" -> N
            "D" -> S
            "L" -> W
            "R" -> E
            else -> throw IllegalStateException("omg")
        }
        dug.plus(dug.last().move(newHeading, dist))
    }.let { dug ->
        val area = dug.shoelaceArea() + 2 // TODO why does adding + 2 fix this? why is everything so hard
        val border = dug.zipWithNext { a, b -> a.taxiDistance(b) }.sum().toDouble()
        picksTheorem(area, border).toLong()
    }
}
