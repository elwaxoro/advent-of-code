package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 20: A Regular Map
 */
class Dec20 : PuzzleDayTester(20, 2018) {

    override fun part1(): Any = loadToString().explore().values.max() == 3512

    override fun part2(): Any = loadToString().explore().values.filter { it >= 1000 }.size == 8660

    private fun String.explore(): Map<Coord, Int> = mutableMapOf<Coord, Int>().also { distMap ->
        var pos = Coord(0, 0)
        val stack = mutableListOf<Coord>()

        forEach { c ->
            when (c) {
                '(' -> stack.add(pos)
                ')' -> pos = stack.removeLast()
                '|' -> pos = stack.last()
                in "NSEW" -> {
                    val nextPos = pos.move(Dir.fromChar(c))
                    val nextDist = distMap.getOrDefault(pos, 0) + 1
                    if (nextDist < (distMap[nextPos] ?: Int.MAX_VALUE)) {
                        distMap[nextPos] = nextDist
                    }
                    pos = nextPos
                }

                else -> {} // ignore chars like ^$
            }
        }
    }
}
