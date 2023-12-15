package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.*
import org.elwaxoro.advent.Dir.*

/**
 * Parabolic Reflector Dish
 */
class Dec14 : PuzzleDayTester(14, 2023) {

    override fun part1(): Any = loader().let { rocks ->
        val settledRound = rocks.tilt(N)
        settledRound.sumOf { it.y + 1 }
    } == 109654

    override fun part2(): Any = loader().let { rocks ->
        val target = 1000000000
        val states = mutableMapOf<String, Int>()
        var i = 1
        var settledRound = rocks.first
        var foundSkip = false
        while (i <= target) {
            settledRound = listOf(N, W, S, E).fold(settledRound) { acc, dir ->
                (acc to rocks.second).tilt(dir)
            }
            val key = settledRound.plus(rocks.second).printify()
            if (states.containsKey(key) && !foundSkip) {
                // holy crap! found a loop! lets jump ahead as far as possible
                val loopStart = states[key]!!
                val loopSize = i - loopStart
                // don't skip ahead so far that you go right off the end of the target
                val skip: Int = ((target - loopStart) / loopSize) - 1
                i += (skip * loopSize)
                foundSkip = true
            } else {
                states[key] = i
            }
            i++
        }
        settledRound.sumOf { it.y + 1 }
    } == 94876

    private fun Pair<Set<Coord>, Set<Coord>>.tilt(dir: Dir): Set<Coord> {
        val bounds = first.plus(second).bounds()
        val occupied = second.toMutableSet()
        val settledRound = mutableSetOf<Coord>()

        val sortedRocks = when (dir) {
            N -> first.sortedByDescending { it.y }
            S -> first.sortedBy { it.y }
            E -> first.sortedByDescending { it.x }
            W -> first.sortedBy { it.x }
        }

        sortedRocks.map { rock ->
            var rolyPoly = rock
            var isStuck = false
            while (!isStuck) {
                val rolledTo = rolyPoly.move(dir)
                if (bounds.contains(rolledTo) && !occupied.contains(rolledTo)) {
                    // round rock didn't roll off the map or run into any flat rocks
                    rolyPoly = rolledTo
                } else {
                    isStuck = true
                }
            }
            settledRound.add(rolyPoly)
            occupied.add(rolyPoly)
        }
        return settledRound
    }

    private fun loader() = load().reversed().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            Coord(x, y, c).takeUnless { it.d == '.' }
        }
        // use the character value to partition the data, but after strip it back out so its just x,y coords so its easy set comparisons
    }.partition { it.d == 'O' }.let { it.first.map { it.copyD() }.toSet() to it.second.map { it.copyD() }.toSet() }
}
