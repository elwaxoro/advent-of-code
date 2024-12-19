package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 19: Linen Layout
 */
class Dec19: PuzzleDayTester(19, 2024) {

    override fun part1(): Any = loader().let { (towels, designs) ->
        designs.count {
            arrangeTowels(it, towels)
        }
    }

    fun arrangeTowels(design: String, towels: Set<String>): Boolean = computeIfAbsent(design) {
        if (design.isEmpty()) {
            true
        } else {
            towels.filter { design.startsWith(it) }.any {
                arrangeTowels(design.drop(it.length), towels)
            }
        }
    }

    override fun part2(): Any {
        return super.part2()
    }

    private fun loader() = load(delimiter = "\n\n").let { (a, b) -> a.split(", ").toSet() to b.split("\n") }

    private val memo = mutableMapOf<String, Boolean>()

    private fun computeIfAbsent(key: String, compute: () -> Boolean): Boolean =
        if (memo.containsKey(key)) {
            memo.getValue(key)
        } else {
            val result = compute.invoke()
            memo[key] = result
            result
        }
}
