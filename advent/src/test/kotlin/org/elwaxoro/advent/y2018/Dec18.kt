package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

/**
 * Day 18: Settlers of The North Pole
 */
class Dec18 : PuzzleDayTester(18, 2018) {

    /**
     * Game of life, but with CHOPPING DOWN TREES!
     */
    override fun part1(): Any = loader().let { input ->
        val (_, trees, yards) = (1..10).fold(input) { acc, _ -> acc.cycle() }
        trees.size * yards.size
    } == 483840

    /**
     * Similar to other AoC challenges:
     * brute force will take forever, so find a shortcut
     * cycle the system until you find a loop, use the loop to skip close to the end, then cycle the remainder to get the final state
     */
    override fun part2(): Any = loader().let { input ->
        val seen = mutableMapOf(input.printify() to 0)
        var loop = ""
        var testing = input
        var i = 0
        while (loop == "") {
            i++
            testing = testing.cycle()
            val str = testing.printify()
            if (seen.contains(str)) {
                loop = str
            } else {
                seen[str] = i
            }
        }
        val target = 1000000000
        val loopStart = seen[loop]!!
        val loopSize = i - loopStart
        val loopCount = (target - loopStart) / loopSize
        val remainder = target - loopStart - (loopCount * loopSize)
        val (_, trees, yards) = (1..remainder).fold(testing) { acc, _ -> acc.cycle() }
        trees.size * yards.size
    } == 219919

    private fun List<Set<Coord>>.printify(): String {
        val (_, trees, yards) = this
        return trees.map { it.copyD('|') }.plus(yards.map { it.copyD('#') }).printify()
    }

    private fun List<Set<Coord>>.cycle(): List<Set<Coord>> {
        val (open, trees, yards) = this
        val newOpen = mutableSetOf<Coord>()
        val newTrees = mutableSetOf<Coord>()
        val newYards = mutableSetOf<Coord>()

        // Open (.) becomes trees (|) if 3+ adjacent are trees (|)
        open.forEach { c ->
            if (c.neighbors9(includeSelf = false).flatten().count { it in trees } >= 3) {
                newTrees.add(c)
            } else {
                newOpen.add(c)
            }
        }

        // Trees (|) becomes lumberyard (#) if 3+ adjacent are lumberyards (#)
        trees.forEach { c ->
            if (c.neighbors9(includeSelf = false).flatten().count { it in yards } >= 3) {
                newYards.add(c)
            } else {
                newTrees.add(c)
            }
        }

        // Lumberyard (#) stays if adjacent to 1+ lumberyard (#) AND 1+ trees (|), else it becomes open (.)
        yards.forEach { c ->
            val n = c.neighbors9(includeSelf = false).flatten()
            if (n.count { it in trees } > 0 && n.count { it in yards } > 0) {
                newYards.add(c)
            } else {
                newOpen.add(c)
            }
        }

        return listOf(newOpen, newTrees, newYards)
    }

    private fun loader(): List<Set<Coord>> = load().let { lines ->
        val open = mutableSetOf<Coord>()
        val trees = mutableSetOf<Coord>()
        val yards = mutableSetOf<Coord>()

        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    '.' -> open.add(Coord(x, y))
                    '|' -> trees.add(Coord(x, y))
                    '#' -> yards.add(Coord(x, y))
                }
            }
        }

        listOf(open, trees, yards)
    }
}
