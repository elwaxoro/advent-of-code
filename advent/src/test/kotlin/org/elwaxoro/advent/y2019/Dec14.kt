package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Day 14: Space Stoichiometry
 */
class Dec14 : PuzzleDayTester(14, 2019) {

    private val stock = mutableMapOf<String, Long>()
    private val recipes = loader()
    private val trillion = 1000000000000

    override fun part1(): Any = synthesize("FUEL", 1) == 654909L

    /**
     * Simple binary search, keep trying to synthesize until we're as close as we get
     */
    override fun part2(): Any {
        var low = 0L
        var high = trillion
        while (low <= high) {
            stock.clear()
            val mid = (low + high) / 2
            val created = synthesize("FUEL", mid)
            if (created > trillion) {
                high = mid - 1
            } else if (created < trillion) {
                low = mid + 1
            } else {
                return mid == 2876992L
            }
        }
        return low - 1 == 2876992L
    }

    private fun synthesize(type: String, amount: Long): Long =
        if (type == "ORE") {
            // base case: getting ORE costs 1 per
            amount
        } else {
            val available = takeStock(type, amount)
            if (available == amount) {
                // we have everything on hand already, zero additional cost
                0
            } else {
                val need = amount - available
                val (makes, costs) = recipes.getValue(type)
                val repeat = ceil(need / makes.toDouble()).toInt()
                val created = makes * repeat
                // save the excess from creation
                addStock(type, max(0, created - need))
                // ok now actually make all the components
                costs.sumOf { (makeType, makeAmount) -> synthesize(makeType, makeAmount * repeat) }
            }
        }

    private fun addStock(type: String, count: Long) {
        stock[type] = stock.getOrDefault(type, 0) + count
    }

    private fun takeStock(type: String, count: Long): Long {
        val available = stock.getOrDefault(type, 0)
        stock[type] = max(0, available - count)
        return min(count, available)
    }

    private fun loader() = load().associate { line ->
        val (inputRaw, outputRaw) = line.split(" => ")
        val inputs = inputRaw.split(",").map { it.trim().split(" ").let { it[1] to it[0].toLong() } }
        val (outAmt, outType) = outputRaw.split(" ")
        outType to (outAmt.toLong() to inputs)
    }
}
