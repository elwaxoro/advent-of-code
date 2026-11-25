package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.flipHorizontal
import org.elwaxoro.advent.rotate90

/**
 * Day 21: Fractal Art
 */
class Dec21 : PuzzleDayTester(21, 2017) {

    val start = listOf(
        ".#.",
        "..#",
        "###"
    )

    /**
     * oh boy its game of life time
     * there's a crap ton of rules, but also the inputs have to be rotated and flipped and stuff to see if they match
     * idea: pre-compute all the possible matching orientations for each rule, then
     */
    override fun part1(): Any = gameOfLife(5)

    override fun part2(): Any = gameOfLife(18)

    private fun gameOfLife(steps: Int): Int = loader().let { rules ->
        var grid = start
        repeat(steps) {
            grid = step(grid, rules)
        }
        grid.sumOf { row -> row.count { it == '#' } }
    }

    private fun step(grid: List<String>, rules: Map<String, List<String>>): List<String> {
        val blockSize = 2.takeIf { grid.size % 2 == 0 } ?: 3
        val numBlocks = grid.size / blockSize

        return (0 until numBlocks).flatMap { blockRow ->
            val enhancedRowBlocks = (0 until numBlocks).map { blockCol ->
                val key = (0 until blockSize).joinToString("/") { r ->
                    grid[blockRow * blockSize + r].substring(
                        blockCol * blockSize,
                        (blockCol + 1) * blockSize
                    )
                }
                rules.getValue(key)
            }
            enhancedRowBlocks.first().indices.map { r ->
                enhancedRowBlocks.joinToString("") { it[r] }
            }
        }
    }

    /**
     * Load the rules, and pre-compute all possible orientations of input
     */
    private fun loader() = load().let { lines ->
        buildMap {
            for (line in lines) {
                val (input, output) = line.split(" => ").map { it.split("/") }
                input.allOrientations().forEach { orientation ->
                    put(orientation, output)
                }
            }
        }
    }

    private fun List<String>.allOrientations(): Set<String> {
        // all 4 rotations
        val rotations = generateSequence(this) { it.rotate90() }.take(4).toList()
        // each rotation, flipped
        val flips = rotations.map { it.flipHorizontal() }
        // convert them all back to the flat puzzle format
        return (rotations + flips).map { it.joinToString("/") }.toSet()
    }
}
