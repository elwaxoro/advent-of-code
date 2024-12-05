package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.takeSplit

/**
 * Day 5: Print Queue
 */
class Dec05 : PuzzleDayTester(5, 2024) {

    /**
     * Filter for good updates, take the middle pages and add em up
     */
    override fun part1(): Any = loader().let { (rules, updates) ->
        updates.filter { update ->
            findBad(rules.filter { update.containsAll(it) }, update).isEmpty()
        }.sumOf {
            it[it.size / 2].toInt()
        }
    }

    /**
     * Filter for bad updates, fix them, take the middle pages and add em up
     */
    override fun part2(): Any = loader().let { (rules, updates) ->
        updates.filter { update ->
            findBad(rules.filter { update.containsAll(it) }, update).isNotEmpty()
        }.map { update ->
            sortUpdate(rules.filter { update.containsAll(it) }, update)
        }.sumOf {
            it[it.size / 2].toInt()
        }
    }

    /**
     * Returns the indexes of each invalid page in the update
     * If list is empty, update is good!
     */
    private fun findBad(rules: List<List<String>>, update: List<String>): List<Int> = mutableListOf<String>().let { seen ->
        update.mapIndexedNotNull { index, page ->
            index.takeUnless { rules.none { it[1] == page } || rules.filter { it[1] == page }.all { seen.contains(it[0]) } }.also { seen.add(page) }
        }
    }

    /**
     * Try to fix a broken update by moving one bad page at a time down the update until it's in an acceptable spot
     * Repeat until everything is placed correctly
     */
    private fun sortUpdate(rules: List<List<String>>, update: List<String>, workingPage: String? = null): List<String> {
        val allBadIdx = findBad(rules, update)
        if (allBadIdx.isEmpty()) {
            return update
        } else {
            val badPages = allBadIdx.map { update[it] }
            val nextIdx =
                if (badPages.contains(workingPage)) {
                    update.indexOf(workingPage)
                } else {
                    allBadIdx[0]
                }
            val (a, b) = update.takeSplit(nextIdx + 1)
            val nextPage = a.takeLast(1).single()
            val nextUpdate = a.dropLast(1) + b.take(1) + nextPage + b.drop(1)
            return sortUpdate(rules, nextUpdate, nextPage)
        }
    }

    private fun loader() = load(delimiter = "\n\n").let { (rules, updates) ->
        rules.split("\n").map { rule -> rule.split("|").let { listOf(it[0], it[1]) } } to updates.split("\n").map { it.split(",") }
    }
}
