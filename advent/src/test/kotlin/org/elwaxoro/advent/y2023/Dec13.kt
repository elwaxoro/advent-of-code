package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rowColSwap

/**
 * Point of Incidence
 */
class Dec13: PuzzleDayTester(13, 2023) {

    override fun part1(): Any = loader().map { pattern ->
//        println("NEW PUZZLE")
//        pattern.print()
//        println("ROTATE")
//        pattern.rowColSwap().print()
        // ok what the fuck the mirror point has to touch three of the sides. fuuuuuuuck me
        val horizontal = pattern.findMirrorPoint().filter { it.second + 1 == pattern.size || it.first == 0 }
        val vertical = pattern.rowColSwap().findMirrorPoint().filter { it.second + 1 == pattern[0].size || it.first == 0 }
//        println("Found ${horizontal.size} horizontal: $horizontal")
//        println("Found ${vertical.size} vertical: $vertical")
        println("Total found: ${horizontal.size + vertical.size}")
        val maxH = horizontal.maxByOrNull { it.second-it.first } ?: (0 to 0)
        val maxV = vertical.maxByOrNull { it.second-it.first } ?: (0 to 0)
//        println("maxH: $maxH (h size: ${pattern.size}) maxV: $maxV (v size: ${pattern[0].size})")
        val maxHMid = ((maxH.second+1-maxH.first)/2)+maxH.first
        val maxVMid = ((maxV.second+1-maxV.first)/2)+maxV.first
//        println("H mid: $maxHMid")
//        println("V mid: $maxVMid")
        if (maxHMid > maxVMid) {
            maxHMid * 100
        } else {
            maxVMid
        }
    }.sum() == 30518

    private fun List<List<Char>>.findHorizontalMirrorPoint(): Int? =
        findMirrorPoint().singleOrNull { it.second + 1 == size || it.first == 0 }?.let { (start, end) ->
            (((end+1-start)/2)+start)*100
        }

    private fun List<List<Char>>.findVerticalMirrorPoint(): Int? =
        rowColSwap().findMirrorPoint().singleOrNull { it.second + 1 == first().size || it.first == 0 }?.let { (start, end) ->
            ((end+1-start)/2)+start
        }

    private fun List<List<Char>>.findMirrorPoint(): List<Pair<Int, Int>> = indices.mapNotNull { start ->
        val startCheck = drop(start)
        startCheck.indices.mapNotNull { end ->
            val check = startCheck.dropLast(end)
            if (check.size % 2 == 0) {
                val pass = (0..<(check.size/2)).all {
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
    }.flatten()

    override fun part2(): Any = "todo"

    private fun loader() = load(delimiter = "\n\n").map { it.split("\n").map { it.toList() } }

    private fun List<List<Char>>.print() {
        this.forEach { println(it.joinToString("")) }
    }
}
