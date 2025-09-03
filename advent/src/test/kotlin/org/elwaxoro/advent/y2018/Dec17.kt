package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.bounds

/**
 * Day 17: Reservoir Research
 */
class Dec17 : PuzzleDayTester(17, 2018) {

    override fun part1(): Any = runWater().let { (visited, filled) ->
        // NOTE: due to laziness on my part, there is some overlap between visited and filled
        // filled is accurate, visited is overstated
        visited.plus(filled).toSet().size
    } == 38409

    override fun part2(): Any = runWater().second.size == 32288

    private fun runWater() = loader().let { walls ->
        val start = Coord(500, 0)
        val bounds = walls.bounds()
        val minY = bounds.first.y
        val maxY = bounds.second.y
        val visited = mutableSetOf<Coord>() // falling water that has not settled
        val filled = mutableSetOf<Coord>() // settled water
        val falls = mutableListOf(start) // fall points to test from

        while (falls.isNotEmpty()) {
            val next = fall(falls.removeFirst(), walls, visited, filled, maxY)
            if (next != null && next !in filled) {
                falls.addAll(spread(next, walls, visited, filled, maxY))
            }
        }

        // println(visited.map { it.copyD('|') }.plus(filled.map { it.copyD('~') }).plus(walls).printify(empty = ' '))

        // ignore y < smallest y and y > greatest y in scan data
        visited.filter { it.y in minY..maxY } to filled
    }

    private fun spread(start: Coord, walls: Set<Coord>, visited: MutableSet<Coord>, filled: MutableSet<Coord>, maxY: Int): Set<Coord> {
        val (leftO, leftP) = spread(mutableListOf(start), Dir.W, walls, visited, filled, maxY)
        val (rightO, rightP) = spread(mutableListOf(start), Dir.E, walls, visited, filled, maxY)

        if (leftO == SpreadOutcome.WALL && rightO == SpreadOutcome.WALL) {
            // flood fill! both paths become fill, call spread with start = start -1 y to start the next layer up
            filled.addAll(leftP)
            filled.addAll(rightP)
            return spread(start.add(0, -1), walls, visited, filled, maxY)
        } else {
            val newFalls = mutableSetOf<Coord>()
            visited.addAll(leftP)
            visited.addAll(rightP)
            if (leftO == SpreadOutcome.FALL) {
                newFalls.add(leftP.last())
            }
            if (rightO == SpreadOutcome.FALL) {
                newFalls.add(rightP.last())
            }
            return newFalls
        }
    }

    private fun spread(path: MutableList<Coord>, dir: Dir, walls: Set<Coord>, visited: MutableSet<Coord>, filled: Set<Coord>, maxY: Int): Pair<SpreadOutcome, List<Coord>> {
        val next = path.last().move(dir)
        val belowNext = next.add(0, 1)
        if (next in walls) {
            // bonk! hit a wall
            return SpreadOutcome.WALL to path
        } else if (belowNext !in walls && belowNext !in filled) {
            // open air below! or already falling water. both are fine to fall down
            path.add(next)
            return SpreadOutcome.FALL to path
        } else if (next in filled) {
            throw IllegalStateException()
        } else {
            // keep spreading it!
            path.add(next)
            return spread(path, dir, walls, visited, filled, maxY)
        }
    }

    enum class SpreadOutcome {
        FALL,
        WALL,
        DEAD
    }

    private fun fall(start: Coord, walls: Set<Coord>, visited: MutableSet<Coord>, filled: Set<Coord>, maxY: Int): Coord? {
        val next = start.add(0, 1)
        if (next.y > maxY) {
            // base case: fell off the bottom of the map
            return null
        } else if (next in visited) {
            // base case: this has already been explored
            return null
        } else if (next in walls || next in filled) {
            // base case: hit a floor or some settled water. shouldn't be hitting settled water ever
            return start
        } else {
            // recursive case: fall more
            visited.add(next)
            return fall(next, walls, visited, filled, maxY)
        }
    }

    private fun loader() = load().flatMap { line ->
        val (a, b) = line.split(", ")
        val (s, e) = b.substring(2).split("..").map { it.toInt() }
        val j = a.substring(2).toInt()
        (s..e).map { i ->
            if (a.startsWith("x")) {
                Coord(j, i)
            } else {
                Coord(i, j)
            }
        }
    }.toSet()
}
