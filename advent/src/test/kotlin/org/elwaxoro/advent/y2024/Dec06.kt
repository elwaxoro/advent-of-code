package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.*

/**
 * Day 6: Guard Gallivant
 */
class Dec06: PuzzleDayTester(6, 2024) {

    override fun part1(): Any = loader().let { (guard, objects) ->
        guard.moveTillExitOrLoop(objects)
        guard.path.map { it.first }.toSet().size - 1
    }

    override fun part2(): Any = loader().let { (guard, objects) ->
        val start = guard.c
        guard.moveTillExitOrLoop(objects)
        val testObstacles = guard.path.map { it.first }.toSet().minus(start)
        testObstacles.filter { ob ->
            val g = Guard(start)
            g.moveTillExitOrLoop(objects.plus(ob))
        }.size - 1
    }

    private data class Guard(
        var c: Coord,
        var d: Dir = Dir.N,
        val path: MutableList<Pair<Coord, Dir>> = mutableListOf(c to d)
    ) {
        fun move(objects: Set<Coord>): Boolean {
            val nc = c.move(d)
            return if (objects.contains(nc)) {
                d = d.turn(Turn.R)
                false
            } else {
                c = nc
                if (path.contains(c to d)) {
                    true
                } else {
                    path.add(c to d)
                    false
                }
            }
        }

        fun moveTillExitOrLoop(objects: Set<Coord>): Boolean {
            val bounds = objects.bounds()
            while(bounds.contains(c)) {
                val loop = move(objects)
                if (loop) {
                    return true
                }
            }
            return false
        }
    }

    private fun loader() = load().let { lines ->
        var guard = Coord(0, 0, '?')
        val coords = lines.mapIndexed { yee, line ->
            val y = lines.size - yee
            line.mapIndexedNotNull { x, c ->
                when (c) {
                    '#' -> Coord(x, y)
                    '^' -> null.also { guard = Coord(x, y) }
                    else -> null
                }
            }
        }.flatten()
        Guard(guard) to coords.toSet()
    }
}