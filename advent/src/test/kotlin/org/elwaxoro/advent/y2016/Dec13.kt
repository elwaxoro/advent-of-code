package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 13: A Maze of Twisty Little Cubicles
 */
class Dec13 : PuzzleDayTester(13, 2016) {

    // input: designer's favorite number
    private val fav = 1352
    // pre-generate an arbitrary sized maze. 50x50 worked fine
    private val maze = buildMaze()

    override fun part1(): Any = explore(Coord(31, 39)) == 90

    override fun part2(): Any = explore(maxDist = 50) == 135

    private fun explore(end: Coord? = null, maxDist: Int = Int.MAX_VALUE): Int {
        val start = Coord(1, 1)
        val visited = mutableSetOf(start)
        val queue = ArrayDeque<Pair<Coord, Int>>()
        queue.add(start to 0)
        while (queue.isNotEmpty()) {
            val (c, l) = queue.removeFirst()
            if (c == end) {
                return l
            }
            c.neighbors().filter { n -> l < maxDist && n.x >= 0 && n.y >= 0 && !maze.contains(n) && !visited.contains(n) }.forEach {
                visited.add(it)
                queue.add(it to l + 1)
            }
        }
        return visited.size
    }

    private fun buildMaze(): Set<Coord> = (0..50).flatMap { y ->
        (0..50).mapNotNull { x ->
            Coord(x, y).takeIf { !it.isOpen(fav) }
        }
    }.toSet()

    private fun Coord.isOpen(fav: Int) = ((x * x) + (3 * x) + (2 * x * y) + y + (y * y) + fav).toString(2).count { it == '1' } % 2 == 0
}
