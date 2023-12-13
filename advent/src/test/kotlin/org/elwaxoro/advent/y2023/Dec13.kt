package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rowColSwap

/**
 * Point of Incidence
 */
class Dec13 : PuzzleDayTester(13, 2023) {

    /**
     * 30518
     *
     * This got a LOT easier when you realize there's only ONE solution for every input
     * The reflection must touch 3/4 sides to be valid
     * There is either one horizontal or one vertical reflection
     * Don't even need to bother with filtering down to just one, there will only BE just one result for each pattern
     */
    override fun part1(): Any = loader().sumOf { pattern ->
        pattern.findMirrorPoint().sumOf { it.midpoint() * 100 } +
                pattern.rowColSwap().findMirrorPoint().sumOf { it.midpoint() }
    }

    /**
     * 36735
     *
     * Same as part 1, except there can be more than one result
     * Vertical could have the original result, horizontal could have the new one ...
     * Use set functions to strip out duplicates and the original solution
     * There will still be only ONE valid solution at the very end
     * Instead of doing something smart, lets just gonna brute force the entire search space and collapse it to a set at the end
     */
    override fun part2(): Any = loader().sumOf { pattern ->
        // could be smarter here and decide if the original was H or V but meh just grab it all, these two sets will have exactly one entry between them
        val originalHorizontal = pattern.findMirrorPoint().toSet()
        val originalVertical = pattern.rowColSwap().findMirrorPoint().toSet()
        doDumbShit(pattern).minus(originalHorizontal).sumOf { it.midpoint() * 100 } +
                doDumbShit(pattern.rowColSwap()).minus(originalVertical).sumOf { it.midpoint() }
    }

    private fun Pair<Int, Int>.midpoint() = (((second + 1 - first) / 2) + first)

    /**
     * Started out not realizing that a valid mirror point has to touch the sides of the pattern to be valid
     * So yea... this computes ALL the reflection points even ones in the middle ><
     * Filter at the end to make it actually do what the puzzle asked for
     */
    private fun List<List<Char>>.findMirrorPoint(): List<Pair<Int, Int>> = indices.mapNotNull { start ->
        val startCheck = drop(start)
        startCheck.indices.mapNotNull { end ->
            val check = startCheck.dropLast(end)
            if (check.size % 2 == 0) {
                val pass = (0..<(check.size / 2)).all {
                    check[it] == check[check.size - it - 1]
                }
                if (pass) {
                    start to (this.size - end - 1)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }.flatten().filter { it.second + 1 == size || it.first == 0 }

    /**
     * BRUTUS! FORCE THE ANSWER OUT OF HIM!
     */
    private fun doDumbShit(pattern: List<List<Char>>): Set<Pair<Int, Int>> =
        pattern.indices.flatMap { rowIdx ->
            pattern[0].indices.mapNotNull { colIdx ->
                val swap = '.'.takeIf { pattern[rowIdx][colIdx] == '#' } ?: '#'
                val rowsBefore = pattern.take(rowIdx)
                val row = listOf(pattern[rowIdx].mapIndexed { index, c -> c.takeUnless { colIdx == index } ?: swap })
                val rowsAfter = pattern.takeLast(pattern.size - 1 - rowIdx)
                val newPattern = rowsBefore.plus(row).plus(rowsAfter)
                newPattern.findMirrorPoint().takeIf { it.isNotEmpty() }
            }.flatten()
        }.toSet()

    private fun loader() = load(delimiter = "\n\n").map { it.split("\n").map { it.toList() } }
}
