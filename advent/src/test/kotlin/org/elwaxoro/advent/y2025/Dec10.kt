package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.toBinaryInt

/**
 * Day 10: Factory
 * Part1 is my own work
 * Part2 was Z3 adapted from https://github.com/Jadarma/advent-of-code-kotlin-solutions/blob/92e1c6945b689ab88f271ab235447e626d791f0d/solutions/aockt/y2025/Y2025D10.kt
 */
class Dec10 : PuzzleDayTester(10, 2025) {

    override fun part1(): Any = loader().sumOf(Machine::solveButtons) == 461

    override fun part2() = "see linked Z3 code for solution"

    private val regex = """\[(.+)] (.+) \{(.+)}""".toRegex()

    private fun loader() = load().map { line ->
        val (l, b, j) = regex.find(line)!!.destructured
        val lights = l.map { c -> 0.takeIf { c == '.' } ?: 1 }
        val buttons = b.split(" ").map {
            it.replace("(", "").replace(")", "").split(",").map { it.toInt() }.let { digits ->
                val spots = MutableList(lights.size) { 0 }
                digits.forEach { d -> spots[d] = 1 }
                spots.toBinaryInt()
            }
        }
        val buttons2 = b.split(" ").map {
            it.replace("(", "").replace(")", "").split(",").map { it.toInt() }.toSet()
        }
        val joltage = j.split(",").map { it.toInt() }
        Machine(lights.toBinaryInt(), buttons, buttons2, joltage)
    }

    private data class Machine(
        val lights: Int,
        val buttons: List<Int>,
        val buttons2: List<Set<Int>>, // used for part 2
        val joltage: List<Int>
    ) {

        fun solveButtons(): Int {
            val visited = mutableSetOf<Int>()
            val queue = ArrayDeque<Pair<Int, Int>>()
            visited.add(0)
            queue.add(0 to 0)
            while (queue.isNotEmpty()) {
                val (currentLights, presses) = queue.removeFirst()
                if (currentLights == this.lights) {
                    return presses
                }
                buttons.map { currentLights xor it }.filterNot { it in visited }.forEach { b ->
                    queue.add(b to presses + 1)
                    visited.add(b)
                }
            }
            return -1
        }
    }
}
