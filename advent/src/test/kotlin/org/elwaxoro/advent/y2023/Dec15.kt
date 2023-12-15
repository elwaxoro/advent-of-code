package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Lens Library
 */
class Dec15 : PuzzleDayTester(15, 2023) {

    /**
     * 506437
     */
    override fun part1(): Any = loader().sumOf { it.HASH() } == 506437

    /**
     * 288521
     */
    override fun part2(): Any =
        (0..<256).map { mutableListOf<Pair<String, Int>>() }.also { HASHMAP ->
            loader().forEach { input ->
                val label = input.replace("[\\-=0-9]*".toRegex(), "")
                val bucket = HASHMAP[label.HASH()]
                if (input.contains('-')) {
                    bucket.removeAll { it.first == label }
                } else {
                    val upsert = label to input.takeLast(1).toInt()
                    if (bucket.any { it.first == label }) {
                        bucket.replaceAll {
                            if (it.first == label) {
                                upsert
                            } else {
                                it
                            }
                        }
                    } else {
                        bucket.add(upsert)
                    }
                }
            }
        }.flatMapIndexed { key, value ->
            value.mapIndexed { index, pair ->
                (key + 1) * (index + 1) * pair.second
            }
        }.sum() == 288521

    private fun String.HASH(): Int = fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }

    private fun loader() = load().single().split(",")
}
