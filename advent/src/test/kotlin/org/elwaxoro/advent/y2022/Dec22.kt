package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.*
import kotlin.math.max

/**
 * Day 22: Monkey Map
 */
class Dec22 : PuzzleDayTester(22, 2022) {

    private val empty = '_'

    override fun part1(): Any = loader().let { scenario ->

        val map = scenario.map
        val mapR = map.rowColSwap()

        val path = mutableListOf(scenario.start)
        var dir = Dir.N
        var pos = scenario.start
//        println(scenario.map.flatten().plus(path.map { it.copyD('x') }).plus(scenario.start.copyD('S')).printify(empty = ' ', invert = true))
        scenario.moves.forEach { m ->
            dir = dir.turn(m.turn)
            (0 until m.dist).forEach { _ ->
                var newPos = pos.move(dir)
                var lookup = map[newPos.y][newPos.x]
                newPos = if (lookup.d == empty) {
                    when (dir) {
                        Dir.N -> mapR[newPos.x].wrapToStart()
                        Dir.S -> mapR[newPos.x].wrapToEnd()
                        Dir.E -> map[newPos.y].wrapToStart()
                        Dir.W -> map[newPos.y].wrapToEnd()
                    }
                } else {
                    newPos
                }
                lookup = map[newPos.y][newPos.x]
                if (lookup.d == '.') {
                    pos = newPos
                    path.add(pos)
                }
            }
        }
        println("Ending position: $pos facing $dir row is ${map.size-pos.y-1}")

        val row = map.size - pos.y - 1
        val col = pos.x
        val face = when (dir) {
            Dir.N -> 3
            Dir.S -> 1
            Dir.E -> 0
            Dir.W -> 2
        }

        //println(scenario.map.flatten().plus(scenario.start.copyD('S')).plus(path.map { it.copyD('x') }).plus(pos.copyD('Z')).plus(Coord(0, 0, '0')).printify(empty = ' ', invert = true))
        // 77046 is too low
        (row * 1000) + (4 * col) + face

    }

    private fun List<Coord>.wrapToStart(): Coord = first { it.d != empty }
    private fun List<Coord>.wrapToEnd(): Coord = last { it.d != empty }

    override fun part2(): Any = ""

    private fun loader() = load(delimiter = "\n\n").let { (map, moves) ->
        val splitMap = map.split("\n")
        val maxX = splitMap.maxOf { it.length }
        val maxY = splitMap.size
        val maxLength = max(maxX, maxY)
        val bottomRow: List<Coord> = (0..maxLength + 1).map { Coord(it, 0, empty) }
        val topRows: List<List<Coord>> = (maxY + 1 until maxLength + maxLength - maxY + 2).map { y -> (0..maxLength + 1).map { x -> Coord(x, y, empty) } }
        val coords = splitMap.reversed().mapIndexed { y, row ->
            listOf(Coord(0, y + 1, empty)).plus(
                row.padEnd(maxLength, ' ').mapIndexed { x, c ->
                    if (c == '.' || c == '#') {
                        Coord(x + 1, y + 1, c)
                    } else {
                        Coord(x + 1, y + 1, empty)
                    }
                }).plus(Coord(maxLength + 1, y + 1, empty))
        }
        val start = coords.last().first { it.d == '.' }

        var turn: Turn = Turn.R
        var dist = ""
        val parsedMoves = moves.mapNotNull { c ->
            if (c.isDigit()) {
                dist += c
                null
            } else {
                Move(turn, dist.toInt()).also {
                    dist = ""
                    turn = Turn.L.takeIf { c == 'L' } ?: Turn.R
                }
            }
        }.plus(Move(turn, dist.toInt()))
        val finalCoords = mutableListOf<List<Coord>>()
        finalCoords.add(bottomRow)
        finalCoords.addAll(coords)
        finalCoords.addAll(topRows)
        Scenario(finalCoords, start, parsedMoves)
    }

    data class Scenario(val map: List<List<Coord>>, val start: Coord, val moves: List<Move>)
    data class Move(val turn: Turn, val dist: Int)
}
