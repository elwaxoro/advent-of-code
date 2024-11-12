package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 11: Chronal Charge
 */
class Dec11 : PuzzleDayTester(11, 2018) {

    override fun part1(): Any {
        val gridSerialNumber = 5177
        val c = buildGrid(gridSerialNumber)

        return (0..297).flatMap { y ->
            (0..297).map { x ->
                "${x + 1},${y + 1}" to calcGrid(x, y, 3, c)
            }
        }.maxBy { it.second }
    }

    override fun part2(): Any {
        val gridSerialNumber = 5177
        val c = buildGrid(gridSerialNumber)
        // this takes a few minutes, swap with (8..8) to run the full calc. my solution found with grid size 8
        // return (1..300).map { size ->
        return (8..8).map { size ->
            (0..(300 - size)).flatMap { y ->
                (0..(300 - size)).map { x ->
                    "${x + 1},${y + 1},$size" to calcGrid(x, y, size, c)
                }
            }.maxBy { it.second }
        }.maxBy { it.second }
    }

    private fun buildGrid(gridSerialNumber: Int): List<List<Int>> =
        (1..300).mapIndexed { y, _ ->
            (1..300).mapIndexed { x, _ ->
                val rackId = x + 11L
                var powerLevel = rackId * (y + 1L)
                powerLevel += gridSerialNumber
                powerLevel *= rackId
                val str = "000$powerLevel"
                val hundreds = str[str.length - 3].digitToInt()
                hundreds - 5
            }
        }

    private fun calcGrid(x: Int, y: Int, size: Int, c: List<List<Int>>): Int =
        (0..<size).sumOf { vy ->
            (0..<size).sumOf { vx ->
                c[y + vy][x + vx]
            }
        }
}
