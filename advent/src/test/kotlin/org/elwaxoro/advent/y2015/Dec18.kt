package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

/**
 * Day 18: Like a GIF For Your Yard
 */
class Dec18: PuzzleDayTester(18, 2015) {

    override fun part1(): Any = loader().let { start ->
        (1..100).fold(start) { grid, step ->
            grid.map { (coord, state) ->
                val on = coord.neighbors9(includeSelf = false).flatten().count { grid[it] == '#' }
                coord to if (state == '#') {
                    if (on == 2 || on == 3) {
                        '#'
                    } else {
                        '.'
                    }
                } else if (on == 3) {
                    '#'
                } else {
                    '.'
                }
            }.toMap()//.also { println(it.filter { it.value == '#'}.keys.printify()) }
        }.count { it.value == '#' }
    }

    private val max = 99
    private val corners = setOf(Coord(0, 0), Coord(max, 0), Coord(0, max), Coord(max, max))

    override fun part2(): Any = loader().let { start ->
        val startstart = start.toMutableMap()
        corners.forEach { startstart[it] = '#' }
        (1..100).fold(startstart.toMap()) { grid, step ->
            grid.map { (coord, state) ->
                if (corners.contains(coord)) {
                    coord to '#'
                } else {
                    val on = coord.neighbors9(includeSelf = false).flatten().count { grid[it] == '#' }
                    coord to if (state == '#') {
                        if (on == 2 || on == 3) {
                            '#'
                        } else {
                            '.'
                        }
                    } else if (on == 3) {
                        '#'
                    } else {
                        '.'
                    }
                }
            }.toMap()//.also { println(it.filter { it.value == '#'}.keys.printify()) }
        }.count { it.value == '#' }
    }

    private val rules = listOf<(s: Char, n: Int) -> Boolean>(
        { s, n -> s == '.' && n == 3 },
        { s, n -> s == '#' && n in (2..3) },
    )

    private val p2Rule: (c: Coord, s: Char, n: Int) -> Boolean = { c, _, _ -> corners.contains(c) }

//    listOf<(input: Long) -> Long>
    private fun loader() = load().flatMapIndexed { y, s -> s.mapIndexed { x, c -> Coord(x, y) to c } }.toMap()
}
