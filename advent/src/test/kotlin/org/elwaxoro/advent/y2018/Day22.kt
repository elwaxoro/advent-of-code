package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 22: Mode Maze
 */
class Day22 : PuzzleDayTester(22, 2018) {

    override fun part1(): Any = loader().let { (depth, target) ->
        expandMap(depth, target).values.sum()
    } == 10603

    /**
     * make an area 7x larger in x,y than the target coord this will give plenty of room to search
     * start at start, push all neighbors plus all allowable tools (along with their swap cost)
     * start starts with torch, end must be entered with torch
     * when processing a node:
     * check route cost + tool vs previous value in map (abort if better)
     * max route cost will be taxi distance * 7 (swap tools every step from start to finish)
     */
    override fun part2(): Any {

        return "TODO"
    }

    /**
     * Rocky = 0
     * Wet = 1
     * Narrow = 2
     */
    private fun expandMap(depth: Int, target: Coord, stop: Coord = target): Map<Coord, Int> {
        val start = Coord(0, 0)
        val erosion = mutableMapOf<Coord, Int>()
        (start.x..stop.x).forEach { x ->
            (start.y..stop.y).forEach { y ->
                val region = Coord(x, y)
                val geologicIdx = if (region == start || region == target) {
                    0 // mouth of cave and target have a geologic index of 0
                } else if (region.y == 0) {
                    region.x * 16807 // If the region's Y coordinate is 0, the geologic index is its X coordinate times 16807
                } else if (region.x == 0) {
                    region.y * 48271 // If the region's X coordinate is 0, the geologic index is its Y coordinate times 48271
                } else {
                    // Otherwise, the region's geologic index is the result of multiplying the erosion levels of the regions at X-1,Y and X,Y-1
                    erosion[region.move(Dir.W)]!! * erosion[region.move(Dir.S)]!!
                }
                // A region's erosion level is its geologic index plus the cave system's depth, all modulo 20183
                erosion[region] = (geologicIdx + depth) % 20183
            }
        }
        return erosion.map { (c, e) ->
            c to e % 3
        }.toMap()
    }

    private fun loader() = load().map { it.split(": ")[1] }.let { (depth, target) ->
        depth.toInt() to Coord.parse(target)
    }
}
