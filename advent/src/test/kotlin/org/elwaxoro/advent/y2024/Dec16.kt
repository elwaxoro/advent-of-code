package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.*

/**
 * Day 16: Reindeer Maze
 * forward costs 1, turn costs 1000
 */
class Dec16 : PuzzleDayTester(16, 2024) {

    override fun part1(): Any = loader().let { grid ->
        val walls = grid.filter { it.d == '#' }.copyD().toSet()
        val start = grid.single { it.d == 'S' }.copyD('E') // tricky tricky, S/E on board != moving in those directions
        val end = grid.single { it.d == 'E' }.copyD()
        val explore = mutableListOf(start)
        val cost = mutableMapOf<Coord, Int>()
        while (explore.isNotEmpty()) {
            val e = explore.removeFirst()
            val c = cost[e] ?: 0
            if (e.copyD() != end) {
                listOf(
                    e.move(e.toDir()) to c + 1, // explore forward 1
                    e.turnMove(Turn.R) to c + 1000 + 1, // turn right forward 1
                    e.turnMove(Turn.L) to c + 1000 + 1, // turn left forward 1
                ).filterNot { walls.contains(it.first.copyD()) }.map { (newEx, newCost) ->
                    if (!cost.contains(newEx) || cost[newEx]!! >= newCost) {
                        cost[newEx] = newCost
                        explore.add(newEx)
                    }
                }
            }
        }
        val ends = cost.keys.filter { it.copyD() == end }
        ends.minOfOrNull {
            cost[it]!!
        } ?: -1
    } == 107468

    private fun Coord.toDir() = Dir.valueOf("$d")
    private fun Coord.turnMove(turn: Turn) = toDir().turn(turn).let { dir -> move(dir, md = dir.toChar()) }

    /**
     * 1220 too high
     * 1200 too high
     */
    override fun part2(): Any = loader().let { grid ->
        val walls = grid.filter { it.d == '#' }.copyD().toSet()
        val start = grid.single { it.d == 'S' }.copyD('E') // tricky tricky, S/E on board != moving in those directions
        val end = grid.single { it.d == 'E' }.copyD()
        val explore = mutableListOf(start)
        val cost = mutableMapOf<Coord, Long>()
        val visited = mutableMapOf<Coord, MutableSet<Coord>>()
        cost[start] = 0
        visited[start] = mutableSetOf(start.copyD())
        while (explore.isNotEmpty()) {
            val e = explore.removeFirst()
            val d = e.copyD()
            val dir = Dir.valueOf("${e.d}")
            val c = cost[e] ?: 0
            if (d == end || walls.contains(d)) {
                // welp this is useless, throw it away
            } else {
                // try to explore straight, turn right, turn left
                // only explore if cost is missing or greater than this
                listOf(
                    e.move(dir) to c + 1,
                    e.move(dir.turn(Turn.R)).copyD(dir.turn(Turn.R).toString()[0]) to c + 1000 + 1,
                    e.move(dir.turn(Turn.L)).copyD(dir.turn(Turn.L).toString()[0]) to c + 1000 + 1,
                ).filterNot { walls.contains(it.first.copyD()) }.map { (newEx, newCost) ->
                    if (!cost.contains(newEx) || cost[newEx]!! > newCost) {
                        // replace
                        cost[newEx] = newCost
                        explore.add(newEx)
                        val set = mutableSetOf(newEx.copyD())
                        set.addAll(visited[e]!!)
                        visited[newEx] = set
                    } else if (cost[newEx]!! == newCost) {
                        cost[newEx] = newCost
                        explore.add(newEx)
                        val set = visited.getOrDefault(newEx, mutableSetOf())
                        set.add(newEx.copyD())
                        set.addAll(visited[e]!!)
                        visited[newEx] = set
                    }
                }
            }
        }
        val ends = cost.keys.filter { it.copyD() == end }
        val best = ends.minBy { cost[it]!! }
        visited[best]!!.copyD().toSet().size
    } == 533

    private fun loader() = load().let { lines ->
        lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                Coord(x, y, c).takeIf { c in listOf('#', 'S', 'E') }
            }
        }
    }
}
