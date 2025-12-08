package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 7: Laboratories
 */
class Dec07 : PuzzleDayTester(7, 2025) {

    override fun part1(): Any = loader().tachyonEmitter().first == 1687

    override fun part2(): Any = loader().tachyonEmitter().second.values.sum() == 390684413472684L

    private fun Pair<Coord, Set<Coord>>.tachyonEmitter(): Pair<Int, Map<Coord, Long>> = let { (start, splitters) ->
        val maxY = splitters.maxOf { it.y } + 1
        var beams: Map<Coord, Long> = mapOf(start to 1L)
        var splitCount = 0
        (0..maxY).forEach { _ ->
            val nState = mutableMapOf<Coord, Long>()
            beams.map { (beam, count) ->
                val next = beam.move(Dir.N)
                if (next in splitters) {
                    splitCount++
                    nState[next.move(Dir.E)] = nState.getOrPut(next.move(Dir.E)) { 0L } + count
                    nState[next.move(Dir.W)] = nState.getOrPut(next.move(Dir.W)) { 0L } + count
                } else {
                    nState[next] = nState.getOrPut(next) { 0L } + count
                }
            }
            beams = nState
        }
        beams.values.sum()
        return splitCount to beams
    }

    private fun loader() = load().let { lines ->
        var start = Coord()
        val splitters = lines.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c == 'S') {
                    start = Coord(x, y)
                    null
                } else if (c == '^') {
                    Coord(x, y)
                } else {
                    null
                }
            }
        }
        start to splitters.toSet()
    }
}