package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.*

/**
 * A Long Walk
 */
class Dec23: PuzzleDayTester(23, 2023) {

    /**
     * 2114
     * BFS kinda thing. Does ok, not great
     */
    override fun part1(): Any = loader().let { maze ->
        val sorted = maze.flatten().groupBy { it.d }.map { group -> group.key to group.value.map { it.copyD() } }.toMap()
        val start = sorted['.']!!.maxBy { it.y }
        val end = sorted['.']!!.minBy { it.y }
        val bounds = maze.flatten().bounds()
        var paths = listOf(listOf(start))
        val goodPaths = mutableListOf<List<Coord>>()
        while (paths.isNotEmpty()) {
            paths = paths.flatMap { path ->
                if (path.last() == end) {
                    goodPaths.add(path)
                    listOf()
                } else {
                    val choices = path.last().neighbors().filterNot { it in path }.filter { bounds.contains(it) }.map { maze[it.y][it.x] }.filterNot { it.d == '#' }
                    if (choices.isEmpty()) {
                        listOf()
                    } else {
                        // check remaining choices, add a new path for each
                        choices.mapNotNull { choice ->
                            val heading = path.last().edge(choice)
                            when(choice.d) {
                                '.' -> path.plus(choice.copyD())
                                // ^><v == check if it can be entered, if so move to it AND the next place in that direction (do a validity check here, if it collides with trees or the current path, add this path to the dead paths)
                                in listOf('^', 'v', '>', '<') -> {
                                    if (Dir.fromCarets(choice.d!!) == heading) {
                                        path.plus(choice.copyD()).plus(choice.move(heading).copyD()).takeIf { it.last() !in sorted['#']!! && it.last() !in path && bounds.contains(it.last()) }
                                    } else {
                                        // can't go this direction, it's too steep
                                        null
                                    }
                                }
                                else -> throw IllegalStateException("omg fell over at $choice")
                            }
                        }
                    }
                }
            }
        }
        goodPaths.maxBy { it.size }.size - 1
    }

    private var maxPath = 0

    /**
     * 6322
     * DFS kinda thing. Does ok, not great
     */
    override fun part2(): Any = loader().let { maze ->
        val sorted = maze.flatten().groupBy { it.d }.map { group -> group.key to group.value.map { it.copyD() } }.toMap()
        val start = sorted['.']!!.maxBy { it.y }
        val end = sorted['.']!!.minBy { it.y }
        // commented out for github runs
//        dfs(start, end, 0, mutableSetOf(), maze)
        "skipped!"
    }

    private fun dfs(current: Coord, end: Coord, distance: Int, visited: MutableSet<Coord>, maze: List<List<Coord>>): Int =
        if (current.x < 0 || current.x >= maze.first().size || current.y < 0 || current.y >= maze.size || maze[current.y][current.x].d == '#' || visited.contains(current)) {
            0
        } else if (current == end) {
            // if hack is removed, un-comment this to get a result without having to wait the full time
            if (distance > maxPath) {
                println("found $distance")
                maxPath = distance
            }
            distance
        } else {
            // hack to shorten runtime on github, remove this to actually calculate
//            if (maxPath == 6322) {
//                maxPath
//            } else {
                // tried building sets during the search, but it's too much mem usage and blows up (like BFS did)
                visited.add(current)
                val max = current.neighbors().maxOf { neighbour ->
                    dfs(neighbour, end, distance + 1, visited, maze)
                }
                visited.remove(current)
                max
//            }
        }

    private fun loader() = load().reversed().mapIndexed { y, line ->
        line.mapIndexed { x, d -> Coord(x, y, d) }
    }
}
