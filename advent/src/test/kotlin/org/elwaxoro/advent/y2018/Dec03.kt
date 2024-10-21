package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 3: No Matter How You Slice It
 */
class Dec03: PuzzleDayTester(3, 2018) {

    /**
     * Find overlaps
     * Do this with some set operations since each rectangle is a list of coords anyway
     * Any group of same coord with more than one is an overlap
     */
    override fun part1(): Any = loader().let { rectangles ->
        rectangles.values.flatten().groupBy { it }.count { it.value.size > 1 }
    }

    /**
     * Same as part 1: group coords into lists where they overlap
     * Find the ID for the single rectangle where all coords have no overlap (group size for all coords is 1)
     */
    override fun part2(): Any = loader().let { rectangles ->
        val occupied = rectangles.values.flatten().groupBy { it }
        rectangles.filter { it.value.all { occupied[it]?.size == 1} }.keys.single()
    }

    /**
     * Split lines of input into a map of ID to rectangle of coords
     */
    private fun loader(): Map<String, List<Coord>> = load().map { it.replace(" @ ", ": ").split(": ") }.associate { (id, c, s) ->
        val start = Coord.parse(c)
        val (dx, dy) = s.split("x").map { it.toInt() - 1 }
        val end = start.add(dx, dy)
        val rectangle = start.enumerateRectangle(end)
        id to rectangle
    }
}
