package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.bounds
import org.elwaxoro.advent.contains

/**
 * Day 8: Resonant Collinearity
 */
class Dec08 : PuzzleDayTester(8, 2024) {

    override fun part1(): Any = loader().makeAntinodes { a, b ->
        val dxDy = a.dxdy(b)
        listOf(
            a.extend(b, dxDy),
            b.extend(a, dxDy, -1)
        )
    }.size

    override fun part2(): Any = loader().let { input ->
        input.makeAntinodes { a, b ->
            val dxDy = a.dxdy(b)
            // board is 50x50 so just run the pattern out 50 times in both directions and throw away whatever is out of bounds
            (1..50).flatMap { mult ->
                listOf(
                    a.extend(b, dxDy, mult),
                    b.extend(a, dxDy, -1 * mult)
                )
            }
        }.plus(input.first.values.flatten()).distinct().size
    }

    private fun Pair<Map<Char, List<Coord>>, Pair<Coord, Coord>>.makeAntinodes(extendomatic: (a: Coord, b: Coord) -> List<Coord>) =
        first.values.map { v ->
            v.mapIndexed { idx, a ->
                v.drop(idx + 1).flatMap { b ->
                    extendomatic(a, b)
                }
            }.flatten()
        }.flatten().distinct().filter { second.contains(it) }

    private fun Coord.extend(that: Coord, dxdy: Pair<Int, Int>, mag: Int = 1): Coord = Coord(that.x + (dxdy.first * mag), that.y + (dxdy.second * mag))

    private fun Coord.dxdy(that: Coord): Pair<Int, Int> = that.x - x to that.y - y

    private fun loader() = load().let { lines ->
        mutableMapOf<Char, MutableList<Coord>>().also { antennas ->
            lines.mapIndexed { iy, line ->
                val y = lines.size - iy
                line.mapIndexed { x, c ->
                    antennas.getOrPut(c) { mutableListOf() }.add(Coord(x, y))
                }
            }
        }.let { antennas ->
            val bounds = antennas.values.flatten().bounds()
            antennas.remove('.')
            antennas to bounds
        }
    }
}
