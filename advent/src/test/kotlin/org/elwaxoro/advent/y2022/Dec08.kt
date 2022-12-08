package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.padTo
import org.elwaxoro.advent.splitToInt

/**
 * Day 8: Treetop Tree House
 */
class Dec08: PuzzleDayTester(8, 2022) {

    /**
     * Can't see the forest for all the trees
     */
    override fun part1(): Any = load().map { it.splitToInt() }.let { trees ->
        val visible = trees.map {
            listOf(0).padTo(it.size).toMutableList()
        }

        val rowSize = trees.lastIndex
        val colSize = trees.first().lastIndex

        trees.lookAtTrees((0..rowSize), (0..colSize), visible)
        trees.lookAtTrees((rowSize downTo 0), (colSize downTo 0), visible)
        trees.lookAtTreesTheOtherWay((0..rowSize), (0..colSize), visible)
        trees.lookAtTreesTheOtherWay((rowSize downTo 0), (colSize downTo 0), visible)
        visible.sumOf { it.sum() }
    }

    /**
     * Let's find the tree with the best visibility for this here tree house
     * Aren't we supposed to be finding a starfruit tree for santa???
     */
    private fun List<List<Int>>.lookAtTrees(rows: IntProgression, cols: IntProgression, visible: List<MutableList<Int>>) {
        rows.forEach { row ->
            var max = -1
            cols.forEach { col ->
                if(this[row][col] > max) {
                    visible[row][col] = 1
                    max = this[row][col]
                }
            }
        }
    }

    private fun List<List<Int>>.lookAtTreesTheOtherWay(rows: IntProgression, cols: IntProgression, visible: List<MutableList<Int>>) {
        cols.forEach { col ->
            var max = -1
            rows.forEach { row ->
                if(this[row][col] > max) {
                    visible[row][col] = 1
                    max = this[row][col]
                }
            }
        }
    }

    override fun part2(): Any = load().map { it.splitToInt() }.let { trees ->
        val visibility = trees.map {
            listOf(0).padTo(it.size).toMutableList()
        }
        trees.forEachIndexed { rowIdx, row ->
            row.forEachIndexed { colIdx, tree ->
                val up = (rowIdx-1 downTo 0).countTrees { trees[it][colIdx] < tree }
                val down = (rowIdx+1..trees.lastIndex).countTrees { trees[it][colIdx] < tree }
                val left = (colIdx-1 downTo 0).countTrees { trees[rowIdx][it] < tree }
                val right = (colIdx+1..trees.first().lastIndex).countTrees { trees[rowIdx][it] < tree }
                visibility[rowIdx][colIdx] = right * left * down * up
            }
        }
        visibility.maxOf { it.max() }
    }

    /**
     * This is literally just takeWhile except it also takes the first item that breaks the predicate
     * ex: look for trees of height less than 5, but also take that first tree that is 5 or higher
     */
    private fun Iterable<Int>.countTrees(predicate: (Int) -> Boolean): Int {
        val list = ArrayList<Int>()
        for (item in this) {
            if (!predicate(item)) {
                list.add(item)
                break
            }
            list.add(item)
        }
        return list.size
    }
}
