package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

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
     * Returns list of bad pages in the update
     * If list is empty, update is good!
     */
    private fun findBad(rules: List<List<String>>, update: List<String>): List<String> = mutableListOf<String>().let { seen ->
        update.mapNotNull { page ->
            page.takeUnless { rules.none { it[1] == page } || rules.filter { it[1] == page }.all { seen.contains(it[0]) } }.also { seen.add(page) }
        }
    }

    /**
     * Try to fix a broken update by moving one bad page at a time down the update until it's in an acceptable spot
     * Repeat until everything is placed correctly
     */
    private fun sortUpdate(rules: List<List<String>>, update: List<String>, workingPage: String? = null): List<String> {
        val badPages = findBad(rules, update)
        if (badPages.isEmpty()) {
            return update
        } else {
            val nextPage = workingPage?.takeIf { badPages.contains(it) } ?: badPages[0]
            val nextIdx = update.indexOf(nextPage)
            val nextUpdate = update.toMutableList()
            nextUpdate[nextIdx + 1] = update[nextIdx]
            nextUpdate[nextIdx] = update[nextIdx + 1]
            return sortUpdate(rules, nextUpdate, nextPage)
        }
    }

    private fun loader() = load(delimiter = "\n\n").let { (rules, updates) ->
        rules.split("\n").map { rule -> rule.split("|").let { listOf(it[0], it[1]) } } to updates.split("\n").map { it.split(",") }
    }
}
