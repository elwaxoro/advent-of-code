package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 12: Subterranean Sustainability
 */
class Dec12 : PuzzleDayTester(12, 2018) {

    private val pad = ".........."

    override fun part1(): Any = loader().let { (initialState, rules) ->
        (1..20).fold(initialState) { acc, _ -> acc.growPlants(rules) }.countPlants()
    }

    /**
     * by running a few hundred of these and printing them out,
     * I noticed that the diff between scores stabilized at round 196,
     * increasing by 45 each time after that
     * keep going until previous diff == current diff,
     * then its just diff * remaining loops + existing score
     */
    override fun part2(): Any = loader().let { (initialState, rules) ->
        var state = initialState
        var previousScore = 0
        var previousDiff = Int.MAX_VALUE
        (1..200).forEach {
            state = state.growPlants(rules)
            val score = state.countPlants()
            val diff = score - previousScore
            if (diff == previousDiff) {
                return (50000000000L - it) * diff + score
            }
            previousDiff = diff
            previousScore = score
        }
        throw IllegalStateException("Failed to find repeating diff")
    }

    private fun String.growPlants(rules: List<List<String>>): String =
        mapIndexed { idx, pot ->
            if (idx > 1 && idx < length - 3) {
                rules.singleOrNull { substring(idx - 2, idx + 3) == it[0] }?.let { it[1] } ?: "."
            } else {
                pot
            }
        }.joinToString("").let {
            // noticed the pattern moves exclusively to the right, so make sure there's always room
            if (!it.endsWith(pad)) {
                it + pad
            } else {
                it
            }
        }

    /**
     * there's only a single pad on the left, padding on the right doesn't matter
     */
    private fun String.countPlants(): Int = mapIndexed { index, c -> (index - pad.length).takeIf { c == '#' } ?: 0 }.sum()

    private fun loader() = load(delimiter = "\n\n").let { (a, b) ->
        // make sure the starting state has enough padding on both sides
        val initialState = pad + a.replace("initial state: ", "") + pad
        val rules = b.split("\n").map { rule ->
            rule.split(" => ")
        }
        initialState to rules
    }
}
