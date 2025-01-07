package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.bounds
import org.elwaxoro.advent.contains
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class Dec20 : PuzzleDayTester(20, 2019) {

    override fun part1(): Any = loader().explore() == 590

    override fun part2(): Any = loader().explore(maxDepth = 25) == 7180

    /**
     * Most annoying part of this puzzle was the loader!
     * NOTE: some portals have an entrance / exit like BC / BC but others might be like FX / XF
     * THESE ARE THE SAME PORTAL PAIRS
     */
    private fun loader() = load().let { lines ->
        val portals = mutableMapOf<Coord, String>()
        val walls = mutableSetOf<Coord>()
        val coords = lines.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                Coord(x, y) to c
            }
        }.toMap()
        var start = Coord(0, 0)
        var end = Coord(0, 0)
        coords.entries.forEach { (c, d) ->
            if (d in listOf(' ', '#')) {
                walls.add(c)
            } else if (d in 'A'..'Z') {
                walls.add(c)
                listOf(
                    listOf(c, c.add(0, -1), c.add(0, -2)), // up
                    listOf(c, c.add(0, 1), c.add(0, 2)), // down
                    listOf(c, c.add(-1, 0), c.add(-2, 0)), // left
                    listOf(c, c.add(1, 0), c.add(2, 0)) // right
                ).map { group ->
                    group to group.map { coords.getOrDefault(it, ' ') }
                }.filter { (_, chars) ->
                    // to be valid, the first two are A-Z, the last one is .
                    // other A-Z might be part of a portal but we just one them once
                    chars[0] in 'A'..'Z' && chars[1] in 'A'..'Z' && chars[2] == '.'
                }.map { (coords, chars) ->
                    val name = "${chars[0]}${chars[1]}"
                    when (name) {
                        "AA" -> start = coords[2]
                        "ZZ" -> end = coords[2]
                        else -> portals[coords[2]] = chars.sorted().joinToString("") // portal entrance / exit will be the "." coordinate of the triple
                    }
                }
            }
        }
        Maze(start, end, portals, walls, walls.bounds())
    }

    private class Maze(
        val start: Coord,
        val end: Coord,
        val portals: Map<Coord, String>,
        val walls: Set<Coord>,
        val bounds: Pair<Coord, Coord>,
    ) {

        // just lots of chars in an array to store layer depth in the Coord's 'd' variable
        private val chars = (('0'..'9') + ('a'..'z') + ('A'..'Z')).toList()

        /**
         * A coord is at an outside portal if it's in the portal list and on the boundary of the maze
         * Note: bounds are too big by 2 in all directions, since I'm including the portal names in the grid!
         */
        fun isOutsidePortal(coord: Coord): Boolean =
            portals[coord]?.let {
                coord.x == bounds.first.x + 2 ||
                        coord.y == bounds.first.y + 2 ||
                        coord.x == bounds.second.x - 2 ||
                        coord.y == bounds.second.y - 2
            } ?: false

        fun isInsidePortal(coord: Coord): Boolean = portals[coord]?.let { !isOutsidePortal(coord) } ?: false

        /**
         * Gets the coord for the opposite side of a portal
         */
        fun getExitPortal(entry: Coord): Coord? = portals[entry]?.let { name -> portals.filterValues { it == name }.filter { it.key != entry }.keys.single() }

        /**
         * Basically just BFS for part 1 and part 2
         * Only changes from part 1 to 2 are the portal rules and visited keys are a triple (x, y, d) instead of just (x, y)
         * Max depth can go up to chars.size depending on needs, 25 was deep enough for my puzzle input
         */
        fun explore(maxDepth: Int = 0): Int {
            val queue = mutableListOf(start.copyD(chars[0]) to 0)
            val visited = mutableMapOf(start.copyD(chars[0]) to 0)

            while (queue.isNotEmpty()) {
                val (c, dist) = queue.removeFirst()
                val depth = chars.indexOf(c.d!!)
                val coord = c.copyD()

                // check if current coord is a portal, try go to in/out depending on rules
                val teleport: Coord? = getExitPortal(coord)?.let { exit ->
                    if (maxDepth == 0) {
                        exit.copyD('0') // part 1, no layers all portals are active and don't change depth
                    } else if (isOutsidePortal(coord) && depth > 0) {
                        // outside portals go a layer shallower (except layer 0 where they aren't real)
                        exit.copyD(chars[depth - 1])
                    } else if (isInsidePortal(coord) && depth < maxDepth) {
                        // inside portals go a layer deeper (except layer maxDepth where they aren't real)
                        exit.copyD(chars[depth + 1])
                    } else {
                        // this portal isn't available at this depth
                        null
                    }
                }

                val next = (coord.neighbors().map { it.copyD(chars[depth]) } + teleport).filterNotNull()
                val nextDist = dist + 1

                // check neighbors + any portal exit to see if we should explore them
                next.filter { bounds.contains(it) && !walls.contains(it.copyD()) && (visited[it] ?: Int.MAX_VALUE) >= nextDist }.map { nc ->
                    queue.add(nc to nextDist)
                    visited[nc] = nextDist
                }
            }
            return visited[end.copyD('0')]!!
        }
    }
}
