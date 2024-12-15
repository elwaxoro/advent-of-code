package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.*

/**
 * Day 15: Warehouse Woes
 */
class Dec15 : PuzzleDayTester(15, 2024) {

    override fun part1(): Any = loader().let { w ->
//        println(w.walls.copyD('#').plus(w.boxes.copyD('O')).plus(w.robot.copyD('R')).printify(invert = true))
        w.moveAll()
        println(w.walls.copyD('#').plus(w.boxes.copyD('O')).plus(w.robot.copyD('R')).printify(invert = true))
        val (min, max) = w.walls.bounds()
        w.boxes.sumOf {
            it.x + (100 * (max.y - it.y))
        }
    }

    override fun part2(): Any {
        return super.part2()
    }

    private fun loader() = load(delimiter = "\n\n").let { (grid, moves) ->
        var robot = Coord(0, 0)
        val walls = mutableListOf<Coord>()
        val boxes = mutableListOf<Coord>()
        val gsplit = grid.split("\n")
        gsplit.mapIndexed { yidx, line ->
            val y = gsplit.size - yidx - 1
            line.mapIndexed { x, c ->
                when (c) {
                    '#' -> walls.add(Coord(x, y))
                    'O' -> boxes.add(Coord(x, y))
                    '@' -> robot = Coord(x, y)
                    else -> {}
                }
            }
        }
        val dirs = moves.split("\n").flatMap { line -> line.map { Dir.fromCarets(it) } }
        Warehouse(robot, walls.toSet(), boxes.toMutableSet(), dirs)
    }

    private data class Warehouse(
        var robot: Coord,
        val walls: Set<Coord>,
        val boxes: MutableSet<Coord>,
        val moves: List<Dir>,
        var moveIdx: Int = 0,
    ) {

        fun moveAll() {
            moves.forEach {
                move(it)
            }
        }

        fun move(dir: Dir) {
            var c: Coord? = robot.move(dir)
            val shift = mutableSetOf(robot)
            while (c != null) {
                if (walls.contains(c)) {
                    // bonk. nobody moves
                    shift.clear()
                    c = null
                } else if (boxes.contains(c)) {
                    shift.add(c)
                    c = c.move(dir)
                } else {
                    // just hit empty space, stop
                    c = null
                }
            }
            if (shift.isNotEmpty()) {
                robot = robot.move(dir)
                boxes.removeAll(shift)
                boxes.addAll(shift.drop(1).map { it.move(dir) })
            }
        }
    }
}