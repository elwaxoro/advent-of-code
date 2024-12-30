package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 24: It Hangs in the Balance
 */
class Dec24 : PuzzleDayTester(24, 2015) {

    /*
     * 1st compartment has to have the fewest packages
     * 1st compartment ties decided by product of packages
     * all 3 compartments must be equal
     */
    override fun part1(): Any = loadToInt().reversed().let { packages ->
        sleighLoader(packages, packages.sum()/3) ?: -1
    }

    private var best = Long.MAX_VALUE

    private fun sleighLoader(packages: List<Int>, equalLoad: Int, a: List<Int> = listOf(), b: List<Int> = listOf(), c: List<Int> = listOf()): Long? {
        val qe = a.fold(1L) { x, y -> x * y }
        if (qe >= best || a.sum() > equalLoad || b.sum() > equalLoad || c.sum() > equalLoad) {
            return null
        } else if (packages.isEmpty()) {
            val aa = a.sum()
            if (aa == b.sum() && aa == c.sum() && a.size < b.size && a.size < c.size) {
                best = qe
                println("found a good one! $qe: $a $b $c")
                return qe
            } else {
//                println("failed $a $b $c")
                return null
            }
        } else {
            return packages.flatMap { p ->
                listOfNotNull(
                    sleighLoader(packages - p, equalLoad, a + p, b, c),
                    sleighLoader(packages - p, equalLoad, a, b + p, c),
                    sleighLoader(packages - p, equalLoad, a, b, c + p)
                )
            }.minOrNull()
        }
    }
}