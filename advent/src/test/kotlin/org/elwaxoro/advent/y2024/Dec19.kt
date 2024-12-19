package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 19: Linen Layout
 * HA! the memoize code here is a literal copy/paste from Dec 12 2023, the hot springs puzzle! solid throwback
 */
class Dec19: PuzzleDayTester(19, 2024) {

    override fun part1(): Any = loader().let { (towels, designs) -> designs.count { arrangeTowels(it, towels) > 0 } }
    override fun part2(): Any= loader().let { (towels, designs) -> designs.sumOf { arrangeTowels(it, towels) } }

    private fun arrangeTowels(design: String, towels: List<String>): Long = computeIfAbsent(design) {
        (1L).takeIf { design.isEmpty() } ?: towels.filter { design.startsWith(it) }.sumOf { towel -> arrangeTowels(design.drop(towel.length), towels) }
    }

    private fun loader() = load(delimiter = "\n\n").let { (towels, designs) -> towels.split(", ") to designs.split("\n") }

    private val memo = mutableMapOf<String, Long>()

    private fun computeIfAbsent(key: String, compute: () -> Long): Long =
        if (memo.containsKey(key)) {
            memo.getValue(key)
        } else {
            val result = compute.invoke()
            memo[key] = result
            result
        }
}
