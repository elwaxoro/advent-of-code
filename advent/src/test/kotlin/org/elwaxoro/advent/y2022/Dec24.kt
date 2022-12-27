package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.containsCoord
import java.lang.IllegalStateException

/**
 * Day 24: Blizzard Basin
 */
class Dec24 : PuzzleDayTester(24, 2022) {

    override fun part1(): Any = loader().let { s ->
        seek(s.start, s.end, s.blizzard, s.max).minutes
    }// == 257

    override fun part2(): Any = loader().let { s ->
        val firstTry = seek(s.start, s.end, s.blizzard, s.max)
        val secondTry = seek(s.end, s.start, firstTry.blizzard, s.max)
        val thirdTry = seek(s.start, s.end, secondTry.blizzard, s.max)
        firstTry.minutes + secondTry.minutes + thirdTry.minutes
    }// == 828

    private fun seek(start: Coord, end: Coord, startBlizzard: List<Coord>, max: Coord): State {
        val states = mutableMapOf(startBlizzard.plus(start).hashCode() to State(start, startBlizzard, 0, listOf(start)))
        var minutes = 0
        var blizzard = startBlizzard
        while (true) {
            minutes++
            blizzard = blizzard.moveBlizzard(max)
            // only look at states that changed in the previous minute
            states.values.filter { it.minutes == minutes - 1 }.toList().forEach { state ->
                state.pos.neighbors().plus(state.pos) // all possible moves, including staying put
                    .filterNot { blizzard.containsCoord(it) } // remove blizzard collisions
                    .filter { it.inBounds(max) || it.equalsCoord(start) || it.equalsCoord(end) } // remove any out of bounds
                    .forEach { pos ->
                        if (pos.equalsCoord(end)) {
                            // yay we did it! bail now
                            return State(pos, blizzard, minutes, state.path.plus(pos))
                        } else {
                            // hash the blizzard + position, nothing else matters
                            // (already been here earlier with same blizzard setup, so current path is inferior to a previous state)
                            states.getOrPut(blizzard.plus(pos).hashCode()) {
                                State(pos, blizzard, minutes, state.path.plus(pos))
                            }
                        }
                    }
            }
        }
    }

    private data class State(val pos: Coord, val blizzard: List<Coord>, val minutes: Int, val path: List<Coord>)

    private fun Coord.inBounds(max: Coord): Boolean = (x > 0 && x <= max.x && y > 0 && y <= max.y)

    private fun List<Coord>.moveBlizzard(max: Coord) = map {
        val n = when (it.d) {
            '>' -> it.move(Dir.E)
            '<' -> it.move(Dir.W)
            'v' -> it.move(Dir.S)
            '^' -> it.move(Dir.N)
            else -> throw IllegalStateException("WHAT ARE YOU DOING STEP-BLIZZARD? ${it.d}")
        }
        if (n.x < 1) {
            Coord(max.x, n.y, n.d)
        } else if (n.x > max.x) {
            Coord(1, n.y, n.d)
        } else if (n.y < 1) {
            Coord(n.x, max.y, n.d)
        } else if (n.y > max.y) {
            Coord(n.x, 1, n.d)
        } else {
            n
        }
    }

    data class Scenario(val coords: Set<Coord>, val blizzard: List<Coord>, val start: Coord, val end: Coord, val max: Coord)

    private fun loader() = load().reversed().mapIndexed { y, row ->
        row.mapIndexed { x, c -> Coord(x, y, c) }
    }.flatten().toSet().let { coords ->
        val startBlizzard = coords.filter { it.d in listOf('>', 'v', '^', '<') }
        val max = Coord(coords.maxOf { it.x } - 1, coords.maxOf { it.y } - 1)
        val start = Coord(1, max.y + 1, 'S')
        val end = Coord(max.x, 0, 'E')
        Scenario(coords, startBlizzard, start, end, max)
    }
}