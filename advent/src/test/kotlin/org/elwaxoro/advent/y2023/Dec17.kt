package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.*
import java.util.*

/**
 * Clumsy Crucible
 */
class Dec17 : PuzzleDayTester(17, 2023) {

    /**
     * 1065
     * Basically Dijkstra, except instead of a Coord, each node is a Coord + heading + direction
     */
    override fun part1(): Any = loader().let { map ->
        val end = Coord(map[0].lastIndex, map.lastIndex)
        dijkstra(
            { cc -> cc.coord == end },
            { (coord, heading, distance) ->
                listOfNotNull(
                    CrucibleCoord(coord.move(heading), heading, distance + 1).takeIf { distance < 3 },
                    CrucibleCoord(coord.move(heading.turn(Turn.L)), heading.turn(Turn.L), 1),
                    CrucibleCoord(coord.move(heading.turn(Turn.R)), heading.turn(Turn.R), 1)
                )
            },
            map
        )
    }

    /**
     * 1249
     * Part 2 is basically the same as part 1, with slightly different rules for a valid exit and when the crucible can turn
     */
    override fun part2(): Any = loader().let { map ->
        val end = Coord(map[0].lastIndex, map.lastIndex)
        dijkstra(
            { cc -> cc.coord == end && cc.distance >= 4 },
            { (coord, heading, distance) ->
                listOfNotNull(
                    CrucibleCoord(coord.move(heading), heading, distance + 1).takeIf { distance < 10 },
                    CrucibleCoord(coord.move(heading.turn(Turn.L)), heading.turn(Turn.L), 1).takeIf { distance >= 4 || distance == 0 },
                    CrucibleCoord(coord.move(heading.turn(Turn.R)), heading.turn(Turn.R), 1).takeIf { distance >= 4 || distance == 0 }
                )
            },
            map
        )
    }

    private fun loader() = load().map { it.splitToInt() }

    private fun dijkstra(
        end: (CrucibleCoord) -> Boolean,
        choices: (CrucibleCoord) -> List<CrucibleCoord>,
        map: List<List<Int>>
    ): Int {
        val queue = PriorityQueue(listOf(CostCoord(CrucibleCoord(Coord(0, 0), Dir.E, 0), 0)))
        // don't need to worry about re-visiting the same x,y position, CrucibleCoord contains the combo of coord + heading + distance which can't be revisited for a cheaper cost as all costs in the puzzle are positive
        val visited: MutableMap<CrucibleCoord, Int> = mutableMapOf(CrucibleCoord(Coord(0, 0), Dir.E, 0) to 0)

        while (queue.isNotEmpty()) {
            val (coord, cost) = queue.remove()
            // since all costs in the puzzle are positive, the first valid exit is the cheapest (via priority queue sorting)
            if (end(coord)) {
                return cost
            }

            choices(coord)
                .filter { it.coord in map } // choice function is lazy, is the output still in play?
                .filterNot { it in visited } // don't repeat yourself
                .map { next -> CostCoord(next, cost + map[coord.coord]) }.let {
                    // everyone else goes into the queue and visited maps
                    queue.addAll(it)
                    visited.putAll(it.associate { it.coord to it.cost })
                }
        }
        // didn't find the exit condition
        return -1
    }

    /**
     * Idea: instead of just tracking cost of visiting a node in the graph,
     * need to also track heading and how many steps of that heading have been used.
     * From there, create neighbors based on the rules
     */
    data class CrucibleCoord(val coord: Coord, val heading: Dir, val distance: Int)

    data class CostCoord(val coord: CrucibleCoord, val cost: Int) : Comparable<CostCoord> {
        override fun compareTo(other: CostCoord): Int = cost.compareTo(other.cost)
    }
}
