package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.*
import org.elwaxoro.advent.Dir.*

/**
 * The Floor Will Be Lava
 */
class Dec16 : PuzzleDayTester(16, 2023) {

    /**
     * 7884
     */
    override fun part1(): Any = loader().let { contraption ->
        val bounds = contraption.flatten().bounds()
        contraption.lightUpTheContraption(Beam(Coord(-1, bounds.second.y, '@'), E))
    }

    /**
     * 8185
     */
    override fun part2(): Any = loader().let { contraption ->
        val yBeams = contraption.indices.flatMap { y -> listOf(Beam(Coord(0 - 1, y), E), Beam(Coord(contraption[0].size, y), W)) }
        val xBeams = contraption[0].indices.flatMap { x -> listOf(Beam(Coord(x, 0 - 1), N), Beam(Coord(x, contraption.size), S)) }
        yBeams.plus(xBeams).maxOf { contraption.lightUpTheContraption(it) }
    }

    private fun List<List<Coord>>.lightUpTheContraption(startingBeam: Beam): Int {
        val bounds = flatten().bounds()
        var beams = listOf(startingBeam)
        val energized = mutableMapOf<Coord, MutableList<Dir>>()
        while (beams.isNotEmpty()) {
            beams = beams.flatMap { beam ->
                val coord = beam.coord.move(beam.heading)
                if (bounds.contains(coord)) {
                    val tile = this[coord.y][coord.x]
                    val state = energized[coord]
                    if (state != null && state.contains(beam.heading)) {
                        // this tile has already been energized by a beam of light with the same heading, throw the beam away
                        listOf()
                    } else {
                        // this tile has not been energized from this direction yet
                        val update = energized.getOrPut(coord) { mutableListOf() }
                        update.add(beam.heading)
                        when (tile.d) {
                            '.' -> listOf(Beam(coord, beam.heading)) // no change, keep going
                            '|' -> when (beam.heading) {
                                N, S -> listOf(Beam(coord, beam.heading)) // no change, keep going
                                E, W -> listOf(Beam(coord, N), Beam(coord, S)) // SPLIT!
                            }

                            '\\' -> when (beam.heading) { // TURN!
                                N -> listOf(Beam(coord, W))
                                S -> listOf(Beam(coord, E))
                                E -> listOf(Beam(coord, S))
                                W -> listOf(Beam(coord, N))
                            }

                            '-' -> when (beam.heading) {
                                N, S -> listOf(Beam(coord, E), Beam(coord, W)) // SPLIT!
                                E, W -> listOf(Beam(coord, beam.heading)) // no change, keep going
                            }

                            '/' -> when (beam.heading) { // TURN!
                                N -> listOf(Beam(coord, E))
                                S -> listOf(Beam(coord, W))
                                E -> listOf(Beam(coord, N))
                                W -> listOf(Beam(coord, S))
                            }

                            else -> throw IllegalStateException("what the hekcy, becky?")
                        }
                    }
                } else {
                    listOf() // beam flew off the contraption
                }
            }
        }
        return energized.size
    }

    private fun loader() = load().reversed().mapIndexed { y, s -> s.mapIndexed { x, c -> Coord(x, y, c) } }

    private data class Beam(
        val coord: Coord,
        val heading: Dir
    )
}