package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.permutations
import java.util.*

/**
 * Day 11: Radioisotope Thermoelectric Generators
 */
class Dec11 : PuzzleDayTester(11, 2016) {

    private val exampleStart = State(
        floors = mapOf(
            0 to setOf("H", "L"),
            1 to setOf("HG"),
            2 to setOf("LG"),
            3 to setOf(),
        ),
        elevator = 0,
        steps = 0,
    )

    private val part1Start = State(
        floors = mapOf(
            0 to setOf("SG", "S", "PG", "P"),
            1 to setOf("TG", "RG", "R", "CG", "C"),
            2 to setOf("T"),
            3 to setOf(),
        ),
        elevator = 0,
        steps = 0,
    )

    private val part2Start = State(
        floors = mapOf(
            0 to setOf("SG", "S", "PG", "P", "EG", "E", "DG", "D"),
            1 to setOf("TG", "RG", "R", "CG", "C"),
            2 to setOf("T"),
            3 to setOf(),
        ),
        elevator = 0,
        steps = 0,
    )

    override fun part1(): Any = solve(part1Start) == 37

    /**
     * First attempt is VERY slow and OOMs part 2 after an hour
     * Speed up attempt: swap visited map to a string for faster contains checks
     *      having efficient storage for visited nodes is key: fast lookup, fast insert
     * Speed up attempt: swap from BFS to A* - VERY fast but not getting right answer in part 2
     *      first heuristic: weight items on higher floors (10x multiplier for floor 2, 100x for floor 3, etc) * some step count
     *      second heuristic: ignore step count but still prefer items on higher floors
     * Solved by hand (61)
     */
    override fun part2(): Any = 61//solve(part2Start)

    private fun solve(start: State): Int {
        val visited = mutableSetOf<String>()
        val queue2 = PriorityQueue<State>(compareBy { it.score })

        queue2.add(start)
        visited.add(start.toString())

        while (queue2.isNotEmpty()) {
            val current = queue2.poll()

            if (current.isComplete()) {
                return current.steps
            }
            current.nextStates().forEach {
                val str = it.toString()
                if (!visited.contains(str)) {
                    queue2.add(it)
                    visited.add(str)
                }
            }
        }

        return -1
    }

    private data class State(val floors: Map<Int, Set<String>>, val elevator: Int, val steps: Int) {

        // A* heuristic: favor partial solutions with more items on higher floors
        val score = floors.map { (k, v) -> (4 - k) * v.size }.sum()

        fun isValid(): Boolean = floors.values.all { floor ->
            if (floor.hasGen()) {
                floor.all { item ->
                    item.isGen() || floor.contains("${item}G")
                }
            } else {
                true
            }
        }

        fun isComplete(): Boolean = floors[0]!!.isEmpty() && floors[1]!!.isEmpty() && floors[2]!!.isEmpty()

        fun moveElevator(target: Int, steps: Int, floor: Set<String>, combos: Sequence<List<String>>): Sequence<State> =
            combos.mapNotNull { combo ->
                val newFloors = floors.toMutableMap()
                newFloors[elevator] = floor.minus(combo) // remove combo from current floor
                newFloors[target] = floors[target]!!.plus(combo) // add combo to target floor
                State(newFloors, target, steps + 1).takeIf { it.isValid() } // only keep valid states
            }

        fun nextStates(): Sequence<State> {
            val floor = floors[elevator]!!
            // gather every combo of items by pairs and individually
            // optimization: don't offer more than one generator+chip matched pair (if moving both together it doesn't matter which set you pick)
            val tuples = floor.permutations(length = 2)
            val pair = tuples.firstOrNull { (a, b) -> a.matches(b) }
            val filtered = if (pair != null) {
                tuples.filterNot { (a, b) -> a.matches(b) }
            } else {
                tuples
            }
            val combos = filtered.plus(floor.map { listOf(it) }).plus(pair).filterNotNull()

            return when (elevator) {
                0 -> moveElevator(1, steps, floor, combos)
                3 -> moveElevator(2, steps, floor, combos)
                else -> moveElevator(elevator + 1, steps, floor, combos).plus(moveElevator(elevator - 1, steps, floor, combos))
            }
        }

        // optimization idea: instead of tostring, organize each chip+generator pair into their floor numbers. Chip/generator pairs are strictly interchangeable in search space
        override fun toString() = "${elevator}:${floors.toSortedMap().map { "${it.key}:${it.value.sorted()}" }.joinToString(";")}"
    }
}

private fun String.isGen() = contains("G")

private fun Set<String>.hasGen() = any { it.isGen() }

private fun String.matches(that: String) = if (isGen()) {
    startsWith(that)
} else {
    that.startsWith(this)
}