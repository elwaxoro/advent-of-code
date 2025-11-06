package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.Turn

/**
 * Day 1: No Time for a Taxicab
 */
class Dec01 : PuzzleDayTester(1, 2016) {

    override fun part1(): Any = parse().fold(Position()) { acc, move ->
        move.apply(acc)
    }.coord.taxiDistance(Coord())

    /**
     * This got messy
     * Didn't realize it was ANY coordinate not just the ending coordinates
     * 153
     */
    override fun part2(): Any {
        val visited = mutableSetOf(Coord())
        var position = Position()
        return parse().flatMap { move ->
            move.applyList(position).let { newPositions ->
                position = newPositions.last()
                newPositions.map { it.coord }
            }
        }.dropWhile {
            // hashset returns false if the item is already in the set
            visited.add(it)
        }.first().taxiDistance(Coord())
    }

    private fun parse(testNum: Int? = null) = load(testNum = testNum, delimiter = ", ").map {
        Move(
            Turn.valueOf(it[0].toString()),
            it.substring(1).toInt()
        )
    }

    data class Position(val coord: Coord = Coord(), val facing: Dir = Dir.N)

    data class Move(val turn: Turn, val distance: Int) {

        fun apply(position: Position): Position = position.facing.turn(turn).let { newDir ->
            Position(
                position.coord.move(newDir, distance),
                newDir
            )
        }

        /**
         * Go one coord at a time, return a list of all positions visited
         * Last position in list == output of other apply function
         */
        fun applyList(position: Position): List<Position> = position.facing.turn(turn).let { newDir ->
            (1..distance).fold(mutableListOf()) { acc, _ ->
                acc.also {
                    acc.add(
                        Position(
                            (acc.lastOrNull() ?: position).coord.move(newDir, 1),
                            newDir
                        )
                    )
                }
            }
        }
    }
}