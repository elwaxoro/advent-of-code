package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.*

/**
 * Day 15: Warehouse Woes
 */
class Dec15 : PuzzleDayTester(15, 2024) {

    override fun part1(): Any = loader(expand = false).runTheSimulation()

    override fun part2() = loader(expand = true).runTheSimulation()

    private data class Moveable(
        val id: String,
        var coords: Set<Coord>
    ) {
        fun move(dir: Dir) {
            coords = coords.map { it.move(dir) }.toSet()
        }
    }

    private data class Warehouse(
        val walls: Set<Coord>,
        val moveables: MutableMap<String, Moveable>,
        val directions: List<Dir>
    ) {

        fun runTheSimulation(): Int {
            directions.forEach { dir ->
                val moved = move(dir, setOfNotNull(moveables["ROBOT"]))
                if (moved.isNotEmpty()) {
                    moved.forEach { m ->
                        moveables[m.id]?.move(dir)
                    }
                }
            }

            val maxY = walls.maxY()
            return moveables.minus("ROBOT").values.sumOf { m ->
                m.coords.minX() + (100 * (maxY - m.coords.first().y))
            }
        }

        fun move(dir: Dir, ms: Set<Moveable>): Set<Moveable> =
            ms.flatMap { it.coords }.map { it.move(dir) }.let { check ->
                if (check.any { walls.contains(it) }) {
                    setOf()
                } else {
                    val newMs = ms.plus(moveables.values.filter { b -> check.any { b.coords.contains(it) } })
                    if (newMs.size > ms.size) {
                        move(dir, newMs)
                    } else {
                        ms
                    }
                }
            }
    }

    private fun loader(expand: Boolean) = load(delimiter = "\n\n").let { (grid, moves) ->
        val walls = mutableListOf<Coord>()
        val movables = mutableMapOf<String, Moveable>()
        val gsplit = grid.split("\n")
        gsplit.mapIndexed { yidx, line ->
            val y = gsplit.size - yidx - 1
            line.mapIndexed { xidx, c ->
                val x = (xidx * 2).takeIf { expand } ?: xidx
                val coords = setOf(Coord(x, y), Coord(x + 1, y)).takeIf { expand } ?: setOf(Coord(x, y))
                when (c) {
                    '#' -> walls.addAll(coords)
                    'O' -> movables["Box$x,$y"] = Moveable("Box$x,$y", coords)
                    '@' -> movables["ROBOT"] = Moveable("ROBOT", setOf(Coord(x, y)))
                    else -> {}
                }
            }
        }
        val directions = moves.split("\n").flatMap { line -> line.map { Dir.fromCarets(it) } }
        Warehouse(walls.toSet(), movables, directions)
    }
}