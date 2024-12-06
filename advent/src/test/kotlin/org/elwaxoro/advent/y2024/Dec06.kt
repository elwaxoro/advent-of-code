package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.*

/**
 * Day 6: Guard Gallivant
 */
class Dec06 : PuzzleDayTester(6, 2024) {

    override fun part1(): Any = loader().let { (guard, obstacles) ->
        guard.moveTillExitOrLoop(obstacles)
        guard.path.map { it.first }.toSet().size - 1
    }

    override fun part2(): Any = loader().let { (guard, obstacles) ->
        guard.moveTillExitOrLoop(obstacles)
        guard.path.map { it.first }.drop(1).toSet().filter { testObstacle ->
            // takes about a minute to run without preload, 15 seconds with. This still isn't super efficient but meh
            Guard.preload(guard.path.take(guard.path.indexOfFirst { it.first == testObstacle })).moveTillExitOrLoop(obstacles + testObstacle)
        }.size - 1
    }

    private data class Guard(
        var c: Coord,
        var d: Dir = Dir.N,
        val path: MutableList<Pair<Coord, Dir>> = mutableListOf(c to d)
    ) {
        companion object {
            fun preload(p: List<Pair<Coord, Dir>>): Guard = Guard(p.last().first, p.last().second, p.toMutableList())
        }

        fun move(obstacles: Set<Coord>): Boolean {
            val nc = c.move(d)
            return if (obstacles.contains(nc)) {
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

        fun moveTillExitOrLoop(obstacles: Set<Coord>): Boolean {
            val bounds = obstacles.bounds()
            while (bounds.contains(c)) {
                val loop = move(obstacles)
                if (loop) {
                    return true
                }
            }
            return false
        }
    }

    private fun loader() = load().let { lines ->
        var guard = Coord(0, 0, '?')
        val obstacles = lines.mapIndexed { yee, line ->
            val y = lines.size - yee
            line.mapIndexedNotNull { x, c ->
                when (c) {
                    '#' -> Coord(x, y)
                    '^' -> null.also { guard = Coord(x, y) }
                    else -> null
                }
            }
        }.flatten()
        Guard(guard) to obstacles.toSet()
    }
}