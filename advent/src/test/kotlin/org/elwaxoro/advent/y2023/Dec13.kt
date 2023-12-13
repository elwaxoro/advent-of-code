package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rowColSwap

/**
 * Point of Incidence
 */
class Dec13: PuzzleDayTester(13, 2023) {

    override fun part1(): Any = loader().sumOf { pattern ->
        listOfNotNull(pattern.findHorizontalMirrorPoint()?.let { it * 100 }, pattern.rowColSwap().findHorizontalMirrorPoint()).single()
    } == 30518

    override fun part2(): Any = loader().sumOf { pattern ->
//        println("---------------------------------")
        val horizontal = pattern.findHorizontalMirrorPointList().toSet()
        val vertical = pattern.rowColSwap().findHorizontalMirrorPointList().toSet()

        val newHorizontal = doDumbShit(pattern).minus(horizontal)
        val newVertical = doDumbShit(pattern.rowColSwap()).minus(vertical)
//        println("old horizontal: $horizontal")
//        println("new horizontal: $newHorizontal")
//        println("old vertical: $vertical")
//        println("new vertical: $newVertical")

//        newHorizontal.minus(horizontal)

        if (newHorizontal.isNotEmpty()) {
            newHorizontal.single().let { (start, end) ->
                (((end + 1 - start) / 2) + start) * 100
            }
        } else {
            newVertical.single().let { (start, end) ->
                (((end + 1 - start) / 2) + start)
            }
        }
    } == 36735

    private fun doDumbShit(pattern: List<List<Char>>): Set<Pair<Int, Int>> {
//        println("starting size: ${pattern[0].size} x ${pattern.size}")
//        pattern.print()
        return pattern.indices.flatMap { rowIdx ->
            pattern[0].indices.mapNotNull { colIdx ->
                val swap = '.'.takeIf { pattern[rowIdx][colIdx] == '#' } ?: '#'
                val rowsBefore = pattern.take(rowIdx)
                val row = listOf(pattern[rowIdx].mapIndexed { index, c -> c.takeUnless { colIdx == index } ?: swap })
                val rowsAfter = pattern.takeLast(pattern.size - 1 - rowIdx)
                val newPattern = rowsBefore.plus(row).plus(rowsAfter)
//                println("new: ${newPattern[0].size} x ${newPattern.size} iteration ${(rowIdx+1)*(colIdx+1)}")
//                newPattern.print()

                newPattern.findHorizontalMirrorPointList().takeIf { it.isNotEmpty() }//?.also { println("FOUND $it at iteration ${(rowIdx+1)*(colIdx+1)}") }
            }.flatten()
        }.toSet()
//        println("All done!")
//        println(all)
//        return 0
    }

    private fun List<List<Char>>.findHorizontalMirrorPoint(): Int? =
        findHorizontalMirrorPointList().singleOrNull()?.let { (start, end) ->
            (((end+1-start)/2)+start)
        }

    private fun List<List<Char>>.findHorizontalMirrorPointList(): List<Pair<Int, Int>> =
        findMirrorPoint().filter { it.second + 1 == size || it.first == 0 }

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

    private fun loader() = load(delimiter = "\n\n").map { it.split("\n").map { it.toList() } }

    private fun List<List<Char>>.print() {
        this.forEach { println(it.joinToString("")) }
    }
}
