package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 2: Inventory Management System
 */
class Dec02 : PuzzleDayTester(2, 2018) {

    override fun part1(): Any = load().map { id -> id.groupBy { it }.values.map { it.size }.distinct() }.let { counts ->
        counts.count { it.contains(2) } * counts.count { it.contains(3) }
    }

    override fun part2(): Any = load().let { ids ->
        ids.mapIndexedNotNull { i, a ->
            ids.subList(i + 1, ids.size).mapNotNull { b ->
                keepSame(a, b).takeIf { it.length == a.length - 1 }
            }.singleOrNull()
        }
    }

    /**
     * keep matching chars at each index of string, discard the rest
     */
    private fun keepSame(a: String, b: String): String = a.zip(b).mapNotNull { pair -> pair.first.takeIf { pair.first == pair.second } }.joinToString("")
}
