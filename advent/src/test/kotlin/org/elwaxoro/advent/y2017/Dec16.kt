package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rotate

/**
 * Day 16: Permutation Promenade
 */
class Dec16 : PuzzleDayTester(16, 2017) {

    private val startingOrder = ('a'..'p').toList()

    override fun part1(): Any = loader().dance(startingOrder).joinToString("")

    /**
     * classic "this will take forever" find a cycle or other way to shorten the process
     * in my case, found the cycle at dance 30 (returned to startingOrder)
     */
    override fun part2(): Any = loader().let { moves ->

        val seen = mutableSetOf(startingOrder.joinToString(""))
        var order = startingOrder
        var count = 0
        var foundCycle = false

        // first, find a cycle
        while (!foundCycle) {
            order = moves.dance(order)
            count++
            if (seen.contains(order.joinToString(""))) {
                foundCycle = true
            } else {
                seen.add(order.joinToString(""))
            }
        }

        // second, skip to the end and just do what's left
        val remainder = 1_000_000_000 % count
        (1..remainder).forEach { _ ->
            order = moves.dance(order)
        }
        order.joinToString("")
    }

    private fun List<String>.dance(start: List<Char>): List<Char> {
        var order = start.toMutableList()
        this.forEach { move ->
            val name = move.first()
            val target = move.drop(1)
            when (name) {
                's' -> {
                    order = order.rotate(target.toInt()).toMutableList()
                }

                'x' -> {
                    val (a, b) = target.split("/").map { it.toInt() }
                    val tmp = order[a]
                    order[a] = order[b]
                    order[b] = tmp
                }

                'p' -> {
                    val (a, b) = target.split("/")
                    val aI = order.indexOf(a[0])
                    val bI = order.indexOf(b[0])
                    val tmp = order[aI]
                    order[aI] = order[bI]
                    order[bI] = tmp
                }

                else -> throw IllegalStateException("Unknown move $move")
            }
        }
        return order
    }

    private fun loader() = loadToString().split(",")
}