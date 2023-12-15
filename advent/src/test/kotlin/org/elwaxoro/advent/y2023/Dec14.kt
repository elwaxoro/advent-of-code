package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.*

/**
 * Parabolic Reflector Dish
 */
class Dec14: PuzzleDayTester(14, 2023) {

    override fun part1(): Any = loader().let { (round, flat) ->
//        println(round.map { it.copyD('O') }.plus(flat).printify(invert = true))
        val bounds = round.plus(flat).bounds()
        val occupied = flat.toMutableSet()
        val settledRound = mutableSetOf<Coord>()
        val sortedRocks = round.sortedByDescending { it.y }
        sortedRocks.map { rock ->
            var rolyPoly = rock
            var isStuck = false
            while (!isStuck) {
                val rolledTo = rolyPoly.move(Dir.N)
                if (bounds.contains(rolledTo) && !occupied.contains(rolledTo)) {
                    // round rock didn't roll off the map or run into any flat rocks
                    rolyPoly = rolledTo
                } else {
                    isStuck = true
                }
            }
//            println("Rolled $rock to $rolyPoly")
            settledRound.add(rolyPoly.copyD('O'))
            occupied.add(rolyPoly)
        }
        settledRound.plus(flat).printify(invert = true)
        settledRound.sumOf { it.y + 1 }
    } == 109654

    private fun Pair<Set<Coord>, Set<Coord>>.tilt(dir: Dir): Pair<Set<Coord>, Set<Coord>> {
        val bounds = first.plus(second).bounds()
        val occupied = second.toMutableSet()
        val settledRound = mutableSetOf<Coord>()
        val sortedRocks = first.sortedByDescending { it.y }
        sortedRocks.map { rock ->
            var rolyPoly = rock
            var isStuck = false
            while (!isStuck) {
                val rolledTo = rolyPoly.move(Dir.N)
                if (bounds.contains(rolledTo) && !occupied.contains(rolledTo)) {
                    // round rock didn't roll off the map or run into any flat rocks
                    rolyPoly = rolledTo
                } else {
                    isStuck = true
                }
            }
            settledRound.add(rolyPoly.copyD('O'))
            occupied.add(rolyPoly)
        }
        return settledRound to second
    }

    override fun part2(): Any = ""

    private fun loader() = load().reversed().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            Coord(x, y, c).takeUnless { it.d == '.' }
        }
        // use the character value to partition the data, but after strip it back out so its just x,y coords so its easy set comparisons
    }.partition { it.d == 'O' }.let { it.first.map { it.copyD() }.toSet() to it.second.map { it.copyD() }.toSet() }
}
