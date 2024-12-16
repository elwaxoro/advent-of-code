package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.*

/**
 * Day 16: Reindeer Maze
 * forward costs 1, turn costs 1000
 */
class Dec16 : PuzzleDayTester(16, 2024) {

    override fun part1(): Any = loader().explore(countVisited = false)
    override fun part2(): Any = loader().explore(countVisited = true)

    private fun List<Coord>.explore(countVisited: Boolean): Int {
        val walls = filter { it.d == '#' }.copyD().toSet()
        val start = single { it.d == 'S' }.copyD('E') // tricky tricky, S/E on board != moving in those directions
        val end = single { it.d == 'E' }.copyD()
        val explore = mutableListOf(start)
        val cost = mutableMapOf<Coord, Int>()
        val visited = mutableMapOf<Coord, MutableSet<Coord>>()
        cost[start] = 0
        visited[start] = mutableSetOf(start.copyD())
        while (explore.isNotEmpty()) {
            val e = explore.removeFirst()
            val c = cost[e] ?: 0
            if (e.copyD() != end) {
                listOf(
                    e.move(e.toDir()) to c + 1, // forward 1
                    e.turnMove(Turn.R) to c + 1000 + 1, // turn right forward 1
                    e.turnMove(Turn.L) to c + 1000 + 1, // turn left forward 1
                ).filterNot { walls.contains(it.first.copyD()) }.map { (newEx, newCost) ->
                    if (!cost.contains(newEx) || cost[newEx]!! > newCost) {
                        // better cost for this coord + direction: replace
                        cost[newEx] = newCost
                        explore.add(newEx)
                        if (countVisited) {
                            val set = mutableSetOf(newEx.copyD())
                            set.addAll(visited[e]!!)
                            visited[newEx] = set
                        }
                    } else if (countVisited && cost[newEx]!! == newCost && !visited[newEx]!!.containsAll(visited[e]!!)) {
                        // equal cost for this coord + direction, but different visited coords: combine
                        cost[newEx] = newCost
                        explore.add(newEx)
                        val set = visited[newEx]!!
                        set.add(newEx.copyD())
                        set.addAll(visited[e]!!)
                        visited[newEx] = set
                    } else {
                        // worse cost, discard
                    }
                }
            }
        }
        val best = cost.keys.filter { it.copyD() == end }.minBy { cost[it]!! }
        return if (countVisited) {
            visited[best]!!.copyD().toSet().size
        } else {
            cost[best] ?: -1
        }
    }

    private fun loader() = load().let { lines ->
        lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                Coord(x, y, c).takeIf { c in listOf('#', 'S', 'E') }
            }
        }
    }

    private fun Coord.toDir() = Dir.valueOf("$d")
    private fun Coord.turnMove(turn: Turn) = toDir().turn(turn).let { dir -> move(dir, md = dir.toChar()) }
}
