package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify
import kotlin.math.abs

/**
 * Cosmic Expansion
 */
class Dec11: PuzzleDayTester(11, 2023) {

    override fun part1(): Any = loader().let { grid ->
        val yExpand = grid.flatMap { row ->
            if (row.none { it == '#' }) {
                listOf(row, row)
            } else {
                listOf(row)
            }
        }
        val fullExpand = yExpand.map { row ->
            row.flatMapIndexed { idx: Int, c: Char ->
                if (grid.all { it[idx] == '.' }) {
                    listOf(c, c)
                } else {
                    listOf(c)
                }

            }
        }
        val galaxies = fullExpand.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c == '#') {
                    Coord(x, y)
                } else {
                    null
                }
            }
        }
        galaxies.mapIndexed { idx, coord ->
            galaxies.drop(idx + 1).sumOf { coord.taxiDistance(it) }
        }.sum()
    }

    override fun part2(): Any = loader().let { grid ->
        val yExpansions = grid.mapIndexedNotNull { index, chars ->
            if (chars.none{ it == '#'}) {
                index
            } else {
                null
            }
        }
        val xExpansions = grid[0].mapIndexedNotNull { index, c ->
            if (grid.none { it[index] == '#' }) {
                index
            } else {
                null
            }
        }
        val galaxies = grid.flatMapIndexed { y, row ->
            val yCount = yExpansions.count { it < y }
            val yOffset = yCount * 1000000L - yCount
            row.mapIndexedNotNull { x, c ->
                val xCount = xExpansions.count { it < x }
                val xOffset = xCount * 1000000L - xCount
                if (c == '#') {
                    LCoord(x + xOffset, y + yOffset)
                } else {
                    null
                }
            }
        }
        galaxies.mapIndexed { idx, coord ->
            galaxies.drop(idx + 1).sumOf { coord.taxiDistance(it) }
        }.sum()
    }

    private fun loader(): List<List<Char>> = load().map { s ->
        s.toList()
    }

    private data class LCoord(
        val x: Long,
        val y: Long
    ) {
        fun taxiDistance(to: LCoord): Long = abs(to.x - x) + abs(to.y - y)
    }

    private fun Collection<LCoord>.printify(full: Char = '#', empty: Char = '.', invert: Boolean = false): String {
        val xs = map { it.x }.sorted()
        val ys = map { it.y }.sorted()
        val xtranslate = 0 - xs.first()
        val ytranslate = 0 - ys.first()

        return "[${xs.first()},${ys.first()}] to [${xs.last()},${ys.last()}]\n" +
                (0..(ys.last() - ys.first())).map {
                    MutableList((xs.last() - xs.first() + 1).toInt()) { empty }
                }.also { screen ->
                    forEach { coord ->
                        screen[(coord.y + ytranslate).toInt()][(coord.x + xtranslate).toInt()] = full
                    }
                }.let {
                    if (invert) {
                        it.reversed()
                    } else {
                        it
                    }
                }.joinToString("\n") { it.joinToString("") }
    }
}
