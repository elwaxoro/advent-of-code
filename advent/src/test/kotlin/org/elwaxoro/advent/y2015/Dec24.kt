package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.combinations

/**
 * Day 24: It Hangs in the Balance
 */
class Dec24 : PuzzleDayTester(24, 2015) {

    /*
     * 1st compartment has to have the fewest packages
     * 1st compartment ties decided by product of packages
     * all 3 compartments must be equal
     * first attempt was brute force but completely the wrong approach, got the right answer for part 1 but part 2 was running forever
     * simplification: using every combination of groups of packages, starting from a single package and working up:
     * find the lowest group that gets ANY combo to the right weight (compartments / X) and just return the min QE of that group size
     * getting lucky here that the answer works without checking that the remaining packages can all be split by equal weight
     */
    override fun part1(): Any = sleighLoader(loadToInt(), loadToInt().sum() / 3)
    override fun part2(): Any = sleighLoader(loadToInt(), loadToInt().sum() / 4)

    private fun sleighLoader(p: List<Int>, t: Int): Long = p.indices.firstNotNullOf { i -> p.combinations(i).filter { it.sum() == t }.minOfOrNull { it.fold(1L) { acc, j -> acc * j } } }
}
