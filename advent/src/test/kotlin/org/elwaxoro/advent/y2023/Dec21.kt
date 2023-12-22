package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

class Dec21 : PuzzleDayTester(21, 2023) {

    /**
     * 3671
     */
    override fun part1(): Any = loader().explore(64)

    /**
     * 609708004316870L
     * I'm not gonna sit here and lie to you about this: I got a hint from the subreddit
     */
    override fun part2(): Any = loader2().let { map ->
        val v1 = map.explore(65).toLong()
        val v2 = map.explore(196).toLong()
        val v3 = map.explore(327).toLong()
        val n = 202300L
        val a = (v1 - 2L * v2 + v3) / 2L
        val b = (-3L * v1 + 4L * v2 - v3) / 2L
        val c = v1
        a * n * n + b * n + c
    }

    private fun List<Coord>.explore(stepLimit: Int): Int {
        val start = single { it.d == 'S' }.copyD()
        val garden = filter { it.d == '.' }.map { it.copyD() }.toSet().plus(start.copyD())
        var explore = setOf(start)
        var steps = 0
        while (steps < stepLimit) {
            explore = explore.flatMap { coord ->
                // not super efficient, I think I could optimize based on "even" steps
                coord.neighbors().filter { garden.contains(it) }
            }.toSet()
            steps++
        }
        // println(garden.map { it.copyD('.') }.plus(start.copyD('S')).plus(explore.map { it.copyD('O') }).plus(rocks.map { it.copyD('#') }).printify(invert = true))
        return explore.size
    }

    private fun loader() = load().reversed().flatMapIndexed { y, s -> s.mapIndexed { x, c -> Coord(x, y, c) } }

    /**
     * Expand the map up to big enough for the max step count: 5x5 original map size
     */
    private fun loader2() = load(delimiter = "\n\n").single().let { chunk ->
        val filler = chunk.replace("S", ".")
        listOf(filler, filler, chunk, filler, filler).joinToString("\n")
    }.split("\n").reversed().map { line ->
        val filler = line.replace("S", ".")
        filler + filler + line + filler + filler
    }.flatMapIndexed { y, s -> s.mapIndexed { x, c -> Coord(x, y, c) } }
}
