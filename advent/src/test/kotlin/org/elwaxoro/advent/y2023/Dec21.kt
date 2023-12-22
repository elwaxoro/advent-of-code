package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

class Dec21: PuzzleDayTester(21, 2023) {

    override fun part1(): Any = loader().let { grid ->
        val start = grid.single { it.d == 'S' }.copyD()
        val rocks = grid.filter { it.d == '#' }.map { it.copyD() }.toSet()
        val garden = grid.filter { it.d == '.' }.map { it.copyD() }.toSet().plus(start.copyD())
        val end = mutableSetOf<Coord>()
        var explore = setOf(start)
        var steps = 0
        while (steps < 64) {
            explore = explore.flatMap { coord ->
                coord.neighbors().filter { garden.contains(it) }//.filterNot { end.contains(it) }
            }.toSet()
            if (steps % 2 == 0) {
                end.addAll(explore)
            }
//            println("STEP ${steps+1} explore: ${explore.size}")

            steps ++
        }
        println(garden.map { it.copyD('.') }.plus(start.copyD('S')).plus(explore.map { it.copyD('O') }).plus(rocks.map { it.copyD('#') }).printify(invert = true))
        explore.size
    } == 3671

    override fun part2(): Any {
        return super.part2()
    }

    private fun loader() = load().reversed().flatMapIndexed { y, s -> s.mapIndexed { x, c -> Coord(x, y, c) } }
}
