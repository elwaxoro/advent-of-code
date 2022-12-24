package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.*
import java.lang.Exception
import kotlin.reflect.KFunction2

/**
 * Day 23: Unstable Diffusion
 */
class Dec23 : PuzzleDayTester(23, 2022) {

    /**
     * Loop through the elf search pattern 10 times, count the open spaces
     * ~100ms
     */
    override fun part1(): Any = loader().let { loaded ->
        var elves = loaded
        var checks = listOf(::checkNorth, ::checkSouth, ::checkWest, ::checkEast)

        (0 until 10).forEach { _ ->
            elves = loopOnce(elves, checks)
            checks = checks.rotate()
        }

        elves.countSpace()
    }// == 3920

    /**
     * Loop through the elf search pattern until it stabilises
     * ~2500ms
     */
    override fun part2(): Any = loader().let { loaded ->
        var elves = loaded
        var hasMoved = true
        var counter = 0
        var checks = listOf(::checkNorth, ::checkSouth, ::checkWest, ::checkEast)

        while (hasMoved) {
            counter++
            val newElves = loopOnce(elves, checks)
            hasMoved = elves != newElves
            elves = newElves
            checks = checks.rotate()
        }

        counter
    }// == 889

    private fun checkNorth(elf: Coord, neighbors: Set<Coord>): Coord? = elf.move(Dir.N).takeIf { neighbors.none { it.y > elf.y } }
    private fun checkSouth(elf: Coord, neighbors: Set<Coord>): Coord? = elf.move(Dir.S).takeIf { neighbors.none { it.y < elf.y } }
    private fun checkWest(elf: Coord, neighbors: Set<Coord>): Coord? = elf.move(Dir.W).takeIf { neighbors.none { it.x < elf.x } }
    private fun checkEast(elf: Coord, neighbors: Set<Coord>): Coord? = elf.move(Dir.E).takeIf { neighbors.none { it.x > elf.x } }

    private fun loopOnce(elves: Set<Coord>, checks: List<KFunction2<Coord, Set<Coord>, Coord?>>): Set<Coord> {
        val proposedMoves: MutableMap<Coord, MutableList<Coord>> = mutableMapOf()
        elves.map { elf ->
            val grid: Set<Coord> = elf.neighbors9().flatten().minus(elf).toSet()
            val neighbors: Set<Coord> = grid.intersect(elves)
            val p = if (neighbors.isEmpty()) {
                elf
            } else {
                try {
                    checks.firstNotNullOf {
                        it.invoke(elf, neighbors)
                    }
                } catch (ex: Exception) {
                    elf
                }
            }

            proposedMoves.getOrPut(p) { mutableListOf() }.add(elf)
        }
        return proposedMoves.flatMap { (p, e) ->
            if (e.size == 1) {
                listOf(p)
            } else {
                e
            }
        }.toSet()
    }

    private fun List<KFunction2<Coord, Set<Coord>, Coord?>>.rotate() = takeSplit(1).let { (first, rest) -> rest.plus(first) }

    private fun Set<Coord>.countSpace() = bounds().let { (min, max) ->
        ((max.x - min.x + 1) * (max.y - min.y + 1)) - size
    }

    /**
     * Reverse the input to make visualizing the coordinates easier
     * Ignore empty space completely
     */
    private fun loader() = load().reversed().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c -> Coord(x, y).takeIf { c == '#' } }
    }.toSet()
}
