package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 18: Like a GIF For Your Yard
 */
class Dec18: PuzzleDayTester(18, 2015) {

    override fun part1(): Any = loader().let { start ->
        (1..100).fold(start.toMap()) { grid, _ -> grid.twinkle(rules) }.count { it.value == '#' }
    }

    override fun part2(): Any = loader(lightCorners = true).let { start ->
        (1..100).fold(start.toMap()) { grid, _ -> grid.twinkle(rules + p2Rule) }.count { it.value == '#' }
    }

    private fun Map<Coord, Char>.twinkle(rules: List<(c: Coord, s: Char, n: Int) -> Boolean>): Map<Coord, Char> = map { (coord, state) ->
        val on = coord.neighbors9(includeSelf = false).flatten().count { this[it] == '#' }
        coord to ('#'.takeIf { rules.any { it.invoke(coord, state, on) } } ?: '.')
    }.toMap()

    private val rules = listOf<(c: Coord, s: Char, n: Int) -> Boolean>(
        { _, s, n -> s == '.' && n == 3 },
        { _, s, n -> s == '#' && n in (2..3) },
    )

    private val p2Rule: (c: Coord, s: Char, n: Int) -> Boolean = { c, _, _ -> corners.contains(c) }

    private val max = 99
    private val corners = setOf(Coord(0, 0), Coord(max, 0), Coord(0, max), Coord(max, max))

    private fun loader(lightCorners: Boolean = false) = load().flatMapIndexed { y, s -> s.mapIndexed { x, c ->
        val coord = Coord(x, y)
        coord to (c.takeUnless { lightCorners && corners.contains(coord) } ?: '#')
    } }.toMap()
}
