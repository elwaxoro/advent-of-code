package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.bitFlip
import org.elwaxoro.advent.splitToInt

/**
 * Day 16: Dragon Checksum
 *
 * Started out with nicer chained calls, chunked(2) to help with checksum, etc
 * Worked great in part 1, but part 2 is way too big and hit OOM issues whenever extra lists were being kept around
 */
class Dec16 : PuzzleDayTester(16, 2016) {

    private val input = "01111010110010011".splitToInt()

    override fun part1(): Any = checksum(expand(272, input)).joinToString("") == "00100111000101111"
    override fun part2(): Any = checksum(expand(35651584, input)).joinToString("") == "11101110011100110"

    private fun expand(target: Int, fill: List<Int>): List<Int> {
        var expanded: List<Int> = fill.expand()
        while (expanded.size < target) {
            expanded = expanded.expand()
        }
        return expanded.take(target)
    }

    private fun List<Int>.expand() = this.plus(0).plus(reversed().bitFlip())

    private fun checksum(list: List<Int>): List<Int> {
        val result = mutableListOf<Int>()
        for (i in 0 until list.size step 2) {
            if (list[i] == list[i + 1]) {
                result.add(1)
            } else {
                result.add(0)
            }
        }
        return if (result.size % 2 == 0) {
            checksum(result)
        } else {
            result
        }
    }
}
