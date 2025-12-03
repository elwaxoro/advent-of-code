package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.md5

/**
 * Day 17: Two Steps Forward
 */
class Dec17 : PuzzleDayTester(17, 2016) {

    private val directions = "UDLR"
    private val unlocked = "bcdef"

    override fun part1(): Any = findPath("gdjjyniy", shortest = true) == "DUDDRLRRRD"
    override fun part2(): Any = findPath("gdjjyniy", shortest = false).length == 578

    private fun findPath(seed: String, shortest: Boolean): String {
        val start = Coord(0, 0)
        val target = Coord(3, 3)
        val grid = start.enumerateRectangle(target)
        val queue = ArrayDeque(listOf(State(start)))
        val visited = mutableSetOf<String>()
        var longestPath = ""

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current.pos == target) {
                if (shortest) {
                    return current.path
                } else if (current.path.length > longestPath.length) {
                    longestPath = current.path
                }
            } else {
                val hash = (seed + current.path).md5()
                val options2 = directions.map { d ->
                    State(current.pos.move(d), current.path + d)
                }.filterIndexed { idx, s ->
                    hash[idx] in unlocked
                            && grid.contains(s.pos)
                            && !visited.contains(s.toString())
                }
                queue.addAll(options2)
                visited.addAll(options2.map { it.toString() })
            }
        }
        return longestPath
    }

    private data class State(val pos: Coord, val path: String = "") {
        override fun toString(): String = pos.toString() + path
    }

    private fun Coord.move(dir: Char): Coord =
        when (dir) {
            'U' -> Coord(x, y - 1)
            'D' -> Coord(x, y + 1)
            'L' -> Coord(x - 1, y)
            'R' -> Coord(x + 1, y)
            else -> throw IllegalStateException("NO :(")
        }
}
