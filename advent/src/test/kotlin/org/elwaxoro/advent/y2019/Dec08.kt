package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.padTo
import org.elwaxoro.advent.splitToInt

/**
 * Day 8: Space Image Format
 */
class Dec08 : PuzzleDayTester(8, 2019) {

    /**
     * Loader already split everything into layers (list of list)
     * so this check is dumber than it has to be, would be easier
     * to just load the layer as a single list
     */
    override fun part1(): Any = loader(25, 6).minBy { layer ->
        layer.sumOf { it.count { it == 0 } }
    }.let { layerWithAllTheZeroes ->
        val ones = layerWithAllTheZeroes.sumOf { it.count { it == 1 } }
        val twos = layerWithAllTheZeroes.sumOf { it.count { it == 2 } }
        ones * twos
    }// == 2975

    /**
     * Create a destination image, then replace transparent pixels with
     * the first valid color encountered
     */
    override fun part2(): Any = loader(25, 6).let { layers ->
        val image = (0..5).map {
            listOf(2).padTo(25).toMutableList()
        }
        layers.forEach { layer ->
            layer.forEachIndexed { row, rowData ->
                rowData.forEachIndexed { col, colData ->
                    if (image[row][col] == 2) {
                        image[row][col] = colData
                    }
                }
            }
        }
        "\n${image.printify()}"
    }

    /**
     * Try to make the final image more readable
     */
    private fun List<List<Int>>.printify(): String = joinToString("\n") {
        it.joinToString("") { pixel ->
            if (pixel == 0) {
                " "
            } else {
                "X"
            }
        }
    }

    private fun loader(w: Int, h: Int) = load().single().splitToInt().windowed(w * h, w * h).map { it.windowed(w, w) }
}
