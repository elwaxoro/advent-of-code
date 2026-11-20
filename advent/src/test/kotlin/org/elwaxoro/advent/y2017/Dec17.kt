package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 17: Spinlock
 */
class Dec17 : PuzzleDayTester(17, 2017) {

    val stepCount = 376

    /**
     * straightforward list inserts
     * ctr is always the size of the list, since it starts with 0 already inserted with size 1
     */
    override fun part1(): Any {
        val buffer = mutableListOf(0)
        var idx = 0
        (1..2017).forEach { ctr ->
            idx = ((idx + stepCount) % ctr) + 1
            buffer.add(idx, ctr)
        }
        return buffer[buffer.indexOf(2017) + 1]
    }

    /**
     * list inserts will never finish, doubly so when its doing mid-list inserts
     * observation from part 1: 0 is always at idx 0. Nothing ever hits it. part 2 answer will be at idx 1
     * don't keep a list at all, just calculate the insert idx and track the ones that would be at idx 1
     * refactored to use a fold instead of a forEach
     */
    override fun part2(): Any = (1..50_000_000).fold(0 to 0) { (idx, answer), ctr ->
        (((idx + stepCount) % ctr) + 1).let { next -> 
            next to (ctr.takeIf { next == 1 } ?: answer)
        }
    }.second
}
