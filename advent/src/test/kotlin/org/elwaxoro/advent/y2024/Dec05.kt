package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.swapAt

/**
 * Day 5: Print Queue
 */
class Dec05 : PuzzleDayTester(5, 2024) {

    /**
     * Filter for good updates, take the middle pages and add em up
     * note: rules only apply if both sides of the rule are in the update
     */
    override fun part1(): Any = loader().let { (rules, updates) ->
        updates.filter { update ->
            rules.active(update).findBad(update).isEmpty()
        }.sumOf {
            it[it.size / 2].toInt()
        }
    }

    /**
     * Filter for bad updates, fix them, take the middle pages and add em up
     */
    override fun part2(): Any = loader().let { (rules, updates) ->
        updates.filter { update ->
            rules.active(update).findBad(update).isNotEmpty()
        }.map { update ->
            rules.active(update).fix(update)
        }.sumOf {
            it[it.size / 2].toInt()
        }
    }

    /**
     * rules are only active if both sides of the rule appear in the update
     */
    private fun List<List<String>>.active(update: List<String>) = filter { update.containsAll(it) }

    /**
     * Returns list of bad pages in the update
     * If list is empty, update is good!
     */
    private fun List<List<String>>.findBad(update: List<String>): List<String> = mutableListOf<String>().let { seen ->
        update.mapNotNull { page ->
            page.takeUnless { none { it[1] == page } || filter { it[1] == page }.all { seen.contains(it[0]) } }.also { seen.add(page) }
        }
    }

    /**
     * Try to fix a broken update by moving one bad page at a time down the update until the rules all pass
     * Repeat until everything is placed correctly
     */
    private fun List<List<String>>.fix(update: List<String>, workingPage: String? = null): List<String> {
        val badPages = findBad(update)
        if (badPages.isEmpty()) {
            return update
        } else {
            val nextPage = workingPage?.takeIf { badPages.contains(it) } ?: badPages[0]
            val nextIdx = update.indexOf(nextPage)
            return fix(update.swapAt(nextIdx, nextIdx + 1), nextPage)
        }
    }

    private fun loader() = load(delimiter = "\n\n").let { (rules, updates) ->
        rules.split("\n").map { rule -> rule.split("|").let { listOf(it[0], it[1]) } } to updates.split("\n").map { it.split(",") }
    }
}
