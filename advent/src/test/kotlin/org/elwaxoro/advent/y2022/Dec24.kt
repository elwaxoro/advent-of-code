package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import java.lang.IllegalStateException

/**
 * Day 24: Blizzard Basin
 */
class Dec24: PuzzleDayTester(24, 2022) {

    override fun part1(): Any = loader().let { coords ->
        val startBlizzard = coords.filter { it.d in listOf('>', 'v', '^', '<') }
        val max = Coord(coords.maxOf { it.x } - 1, coords.maxOf { it.y } - 1)
        val start = Coord(1, max.y + 1, 'S')
        val end = Coord(max.x, 0, 'E')
        seek(start, end, startBlizzard, max).minutes
    } == 257

    override fun part2(): Any = loader().let { coords ->
        val startBlizzard = coords.filter { it.d in listOf('>', 'v', '^', '<') }
        val max = Coord(coords.maxOf { it.x } - 1, coords.maxOf { it.y } - 1)
        val start = Coord(1, max.y + 1, 'S')
        val end = Coord(max.x, 0, 'E')
        val firstTry = seek(start, end, startBlizzard, max)
        val secondTry = seek(end, start, firstTry.blizzard.last(), max)
        val thirdTry = seek(start, end, secondTry.blizzard.last(), max)
        firstTry.minutes + secondTry.minutes + thirdTry.minutes
    } == 828

    private fun seek(start: Coord, end: Coord, startBlizzard: List<Coord>, max: Coord): State {
        var blizzard = startBlizzard
        val states = mutableMapOf<Int, State>()
        states[blizzard.plus(start).hashCode()] = State(start, listOf(blizzard), 0, listOf(start))
        blizzard = blizzard.moveBlizzard(max)
        var minutes = 1
        var solutions = listOf<State>()
        while(solutions.isEmpty()) {
            states.values.filter { it.minutes == minutes-1 }.toList().forEach { state ->

                val options = state.dude.neighbors().plus(state.dude).filterNot { blizzard.containsCoord(it) }.filter {
                    it.inBounds(max) || it.equalsCoord(start) || it.equalsCoord(end)
                }

                options.forEach { option ->
                    val hash = blizzard.plus(option).hashCode()
                    val prev = states[hash]
                    if (prev == null || prev.minutes > minutes) {
                        states[hash] = State(option, state.blizzard.plusElement(blizzard), minutes, state.path.plus(option))
                    }
                }
            }
            solutions = states.values.filter { it.dude.equalsCoord(end) }
            minutes++
            blizzard = blizzard.moveBlizzard(max)
        }
        return solutions.single()
    }

    private data class State(val dude: Coord, val blizzard: List<List<Coord>>, val minutes: Int, val path: List<Coord>)

    private fun Coord.equalsCoord(coord: Coord) = x == coord.x && y == coord.y

    private fun Iterable<Coord>.containsCoord(coord: Coord) = any { it.x == coord.x && it.y == coord.y }

    private fun Coord.inBounds(max: Coord): Boolean = (x > 0 && x <= max.x && y > 0 && y <= max.y)

    private fun List<Coord>.moveBlizzard(max: Coord) = map {
        val n = when(it.d) { // proposed new coord
            '>' -> it.move(Dir.E)
            '<' -> it.move(Dir.W)
            'v' -> it.move(Dir.S)
            '^' -> it.move(Dir.N)
            else -> throw IllegalStateException("WHAT ARE YOU DOING STEP-BLIZZARD? ${it.d}")
        }
        if(n.x < 1) {
            Coord(max.x, n.y, n.d)
        } else if(n.x > max.x) {
            Coord(1, n.y, n.d)
        } else if(n.y < 1) {
            Coord(n.x, max.y, n.d)
        } else if(n.y > max.y) {
            Coord(n.x, 1, n.d)
        } else {
            n
        }
    }

    private fun loader() = load().reversed().mapIndexed { y, row ->
        row.mapIndexed { x, c -> Coord(x,y,c) }
    }.flatten().toSet()
}