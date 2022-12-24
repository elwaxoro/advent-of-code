package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.*
import java.lang.Exception
import kotlin.reflect.KFunction2

class Dec23 : PuzzleDayTester(23, 2022) {

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

            proposedMoves.getOrPut(p){ mutableListOf() }.add(elf)
        }
        return proposedMoves.flatMap { (p,e) ->
            if(e.size == 1) { listOf(p) } else { e }
        }.toSet()
    }

    override fun part1(): Any = loader().let { loaded ->
//        println(loaded.printify(invert=true))
        var elves = loaded
        var hasMoved = true
        var checks: List<KFunction2<Coord, Set<Coord>, Coord?>> = listOf(::checkNorth, ::checkSouth, ::checkWest, ::checkEast)

        (0 until 10).forEach { _ ->
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

                proposedMoves.getOrPut(p){ mutableListOf() }.add(elf)
            }
            val newElves = proposedMoves.flatMap { (p,e) ->
                if(e.size == 1) { listOf(p) } else { e }
            }.toSet()
            hasMoved = elves != newElves
            elves = newElves
            val (first, rest) = checks.takeSplit(1)
            checks = rest.plus(first)
        }

        val (min, max) = elves.bounds()
        val x = max.x - min.x + 1
        val y = max.y - min.y + 1
        val size = x * y
        // 6241 is too high
        size - elves.size
    } == 3920

    override fun part2(): Any = loader().let { loaded ->
//        println(loaded.printify(invert=true))
        var elves = loaded
        var hasMoved = true
        var counter = 0
        var checks = listOf(::checkNorth, ::checkSouth, ::checkWest, ::checkEast)

        while(hasMoved) {
            counter++
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

                proposedMoves.getOrPut(p){ mutableListOf() }.add(elf)
            }
            val newElves = proposedMoves.flatMap { (p,e) ->
                if(e.size == 1) { listOf(p) } else { e }
            }.toSet()
            hasMoved = elves != newElves
            elves = newElves
            val (first, rest) = checks.takeSplit(1)
            checks = rest.plus(first)
        }

        counter
    } == 889

    private fun loader() = load().reversed().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c -> Coord(x, y).takeIf { c == '#' } }
    }.toSet()
}
