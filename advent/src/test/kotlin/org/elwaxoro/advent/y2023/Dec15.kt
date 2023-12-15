package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Lens Library
 */
class Dec15: PuzzleDayTester(15, 2023) {

    /**
     * 506437
     */
    override fun part1(): Any = loader().map { it.reindeerHASH() }.sum() == 506437

    /**
     * 288521
     */
    override fun part2(): Any = loader().let { inputs ->
        val reindeerHASHMAP = (0..<256).map { mutableListOf<Pair<String, Int>>() }
        inputs.forEach { input ->
            val label = input.replace("[\\-=0-9]*".toRegex(), "")
            val labelHASH = label.reindeerHASH()
            if (input.contains('-')) {
                reindeerHASHMAP[labelHASH].removeAll { it.first == label }
            } else {
                val focalLength = input.takeLast(1).toInt()
                if (reindeerHASHMAP[labelHASH].any { it.first == label }) {
                    reindeerHASHMAP[labelHASH].replaceAll {
                        if (it.first == label) {
                            it.first to focalLength
                        } else {
                            it
                        }
                    }
                } else {
                    reindeerHASHMAP[labelHASH].add(label to focalLength)
                }
            }
        }
        reindeerHASHMAP.flatMapIndexed { key, value ->
            value.mapIndexed { index, pair ->
                (key + 1) * (index + 1) * pair.second
            }
        }.sum()
    }

    private fun String.reindeerHASH(): Int = fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }

    private fun loader() = load().single().split(",")
}
