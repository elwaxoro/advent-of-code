package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 2: Cube Conundrum
 */
class Dec02 : PuzzleDayTester(2, 2023) {

    private val maxCubes = mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14
    )

    /**
     * 2156
     * keep games where all colors of each round are under the cube limits
     * return the sum of remaining game numbers
     */
    override fun part1(): Any = loader().let { games ->
        // keep games where all colors of each round are under the cube limits
        games.filter { (_, rounds) ->
            rounds.all { round ->
                round.none { (color, count) ->
                    count > maxCubes[color]!!
                }
            }
        }.sumOf { it.first }
    }

    /**
     * 66909
     * for each game, find the highest cube count for each color and multiply those 3 values
     * return sum of each game
     */
    override fun part2(): Any = loader().sumOf { (_, rounds) ->
        // for each game, find the highest cube count for each color
        maxCubes.keys.map { color ->
            rounds.maxOf { it[color] ?: 0 }
        }.reduce { acc, i -> acc * i }
    }

    /**
     * List<Pair<Int, List<Map<String, Int>>>> is a totally normal and not-at-all janky thing to return
     */
    private fun loader(): List<Pair<Int, List<Map<String, Int>>>> = load().map { game ->
        val split = game.split(":", ";")
        val gameIdx = split[0].replace("Game ", "").toInt()
        val parsedRounds = split.drop(1).map { round ->
            val colors = round.trim().split(',')
            colors.associate { color ->
                color.trim().split(" ").let {
                    it[1] to it[0].toInt()
                }
            }
        }
        gameIdx to parsedRounds
    }
}
