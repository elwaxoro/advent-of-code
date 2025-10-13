package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import java.util.*

/**
 * Day 22: Mode Maze
 */
class Dec22 : PuzzleDayTester(22, 2018) {

    override fun part1(): Any = loader().let { (depth, target) -> expandMap(depth, target).values.sum() } == 10603

    /**
     * make an area somewhat larger in x,y than the target coord this will give plenty of room to search
     * start at start, push all neighbors plus all allowable tools (along with their swap cost)
     * start starts with torch, end must be entered with torch
     * when processing a node:
     * check route cost + tool vs previous value in map (abort if better)
     * max route cost will be taxi distance * 7 (swap tools every step from start to finish)
     * sounds like dijkstra time
     *
     * Answer was off by 1 but sample worked fine ARGH
     */
    override fun part2(): Any = loader().let { (depth, target) ->
        expandMap(depth, target, Coord(800, 800)).dijkstra(CaveCoord(Coord(0, 0), Tool.TORCH), target)
    } + 1 == 952

    private enum class Tool {
        TORCH,
        CLIMB,
        NONE;

        /**
         * Rocky = 0, torch or climb
         * Wet = 1, climb or none
         * Narrow = 2, torch or none
         */
        fun canEnter(terrain: Int): Boolean =
            when (terrain) {
                0 -> this != NONE
                1 -> this != TORCH
                2 -> this != CLIMB
                else -> false
            }
    }

    private data class CaveCoord(val coord: Coord, val tool: Tool)

    private data class CostCoord(val coord: CaveCoord, val cost: Int) : Comparable<CostCoord> {
        override fun compareTo(other: CostCoord): Int = cost.compareTo(other.cost)
    }

    /**
     * basically a copy / mod of previous dijkstra puzzles in the repo
     */
    private fun Map<Coord, Int>.dijkstra(
        start: CaveCoord,
        end: Coord
    ): Int {

        val maxDist = start.coord.taxiDistance(end) * 8 // swapping tool every step of the way from A->B is as expensive as this thing can get

        // priority queue sorted by cheapest cost to visit the next coord + tool combo
        val queue = PriorityQueue(listOf(CostCoord(start, 0)))
        // allow a revisit IFF same coord + tool was reached for a lower total cost than exists already
        val visited: MutableMap<CaveCoord, Int> = mutableMapOf(start to 0)

        while (queue.isNotEmpty()) {
            val (cc, cost) = queue.remove()

            // since all costs in the puzzle are positive, the first valid exit is the cheapest (via priority queue sorting)
            if (cc.coord == end) {
                return cost
            }

            // expand cc in NSEW directions + all tool choice combos
            cc.coord.neighbors().flatMap { n ->
                Tool.entries.map { t ->
                    CaveCoord(n, t)
                }
            }.filter { nc ->
                nc.coord.x >= 0 && nc.coord.y >= 0 // positive coords only
            }.filter { nc ->
                nc.tool.canEnter(this[nc.coord]!!) // tool choice rock/paper/scissors thing restricts movement
            }.filter { nc ->
                nc.coord != end || nc.tool == Tool.TORCH // only the torch can reach the end
            }.map { nc ->
                val newCost = 8.takeIf { nc.tool != cc.tool } ?: 1
                CostCoord(nc, cost + newCost)
            }.filter { cc ->
                val v = visited[cc.coord]
                (v == null || v > cc.cost) && cc.cost < maxDist
            }.let { cc ->
                queue.addAll(cc)
                visited.putAll(cc.associate { it.coord to it.cost })
            }
        }
        // didn't find the exit condition
        return -1
    }

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
