package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 15: Oxygen System
 */
class Dec15 : PuzzleDayTester(15, 2019) {

    /**
     * drive a repair droid around randomly, using intcode program to control it
     * program loop:
     * 1. movement input
     * 2. status output
     * inputs: 1 (N), 2 (S), 3 (E), 4(W)
     * outputs: 0 (wall), 1 (moved), 2 (oxygen system)
     */
    override fun part1(): Any = runBlocking {
        val droid = RepairDroid(loadToLong(delimiter = ","))
        droid.explore()
        //println(droid.coords.map { it.key.copyD(it.value) }.printify(empty = ' '))
        droid.stack.size - 1
    } == 212

    /**
     * fully explore the maze, then flood fill starting from the oxygen system (BFS)
     */
    override fun part2(): Any = runBlocking {
        val droid = RepairDroid(loadToLong(delimiter = ","), exploreFully = true)
        droid.explore()
        val oxygenSystem = droid.coords.firstNotNullOf { cc -> cc.key.takeIf { cc.value == 'E' } }
        val queue = mutableListOf(oxygenSystem to 0)
        val visited = droid.coords.filter { it.value == '#' }.keys.toMutableSet()
        var maxDist = 0
        while (queue.isNotEmpty()) {
            val (c, d) = queue.removeFirst()
            if (d > maxDist) {
                maxDist = d
            }
            visited.add(c)
            c.neighbors().filter { n -> !visited.contains(n) && queue.none { it.first == n } }.map { n ->
                queue.add(n to d + 1)
            }
        }
        maxDist
    } == 358

    /**
     * droid position is last coord in stack
     * start position is 0,0
     * droid explores in a DFS fashion
     */
    private class RepairDroid(
        val code: List<Long>,
        val coords: MutableMap<Coord, Char> = mutableMapOf(Coord(0, 0) to '.'),
        val stack: MutableList<Coord> = mutableListOf(Coord(0, 0)),
        val exploreFully: Boolean = false,
    ) {

        fun Dir.toLong(): Long =
            when (this) {
                Dir.N -> 1
                Dir.S -> 2
                Dir.E -> 3
                Dir.W -> 4
            }

        fun getInput(): Long =
            if (stack.isEmpty()) {
                Long.MAX_VALUE
            } else {
                val check = stack.last()
                if (!exploreFully && coords[check] == 'E') {
                    // part 1: stop as soon as the stack reaches E for the first time
                    Long.MAX_VALUE
                } else {
                    val options = check.neighbors().filterNot { coords.containsKey(it) }
                    if (options.isEmpty()) {
                        // can't explore meaningfully in any directions, we know what everything is already, time to backtrack
                        stack.removeLast()
                        if (stack.isEmpty()) {
                            Long.MAX_VALUE
                        } else {
                            check.dirTo(stack.last()).toLong()
                        }
                    } else {
                        // pick any of the options, add it to the stack and attempt to move in that direction
                        val moveTo = options.first()
                        stack.add(moveTo)
                        check.dirTo(moveTo).toLong()
                    }
                }
            }

        fun readOutput(value: Long) {
            when (value) {
                0L -> {
                    val wall = stack.removeLast()
                    coords[wall] = '#'
                }

                1L -> {
                    val open = stack.last()
                    coords[open] = '.'
                }

                2L -> {
                    val oxygenSystem = stack.last()
                    coords[oxygenSystem] = 'E'
                }
            }
        }

        suspend fun explore() {
            ElfCode(code).runner(
                setup = ElfCode.memExpander(10000),
                input = { getInput() },
                output = { readOutput(it) }
            )
        }
    }
}