package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 11: Monkey in the Middle
 *
 * 1. monke inspects items in list: worry change is defined by op
 * 2. after inspect is relief or part 2 reduction: relief is / 3, part 2 action is % by a common multiple of the testDivisors to keep everything from blowing up
 * 3. monke runs test and throws to another monke
 * 4. repeat
 * 5. count up inspections and multiply the top 2 inspectors (monkey business)
 */
class Dec11 : PuzzleDayTester(11, 2022) {

    override fun part1(): Any = loader().shake(20) { it / 3 }// == 120756L

    override fun part2(): Any = loader().let { barrel ->
        val commonDenominator = barrel.fold(1L) { acc, monke ->
            acc * monke.testDivisor
        }
        barrel.shake(10000) {
            it % commonDenominator
        }
    }// == 39109444654L

    private fun List<Monke>.shake(reps: Int, reducer: (test: Long) -> Long): Long {
        (1..reps).forEach { _ ->
            forEach { monke ->
                monke.inspect(reducer)
                monke.toss(this)
            }
        }
        return map { it.inspectCount }.sorted().takeLast(2).let { (a, b) ->
            a * b
        }
    }

    data class Monke(
        val name: Int,
        var items: MutableList<Long>,
        val opIsPlus: Boolean,
        val opAmt: String,
        val testDivisor: Long,
        val testTrue: Int,
        val testFalse: Int,
        var inspectCount: Long = 0L,
    ) {
        fun inspect(reducer: (test: Long) -> Long) {
            items = items.map { item ->
                val change = if (opAmt == "old") {
                    item
                } else {
                    opAmt.toLong()
                }
                val test = if (opIsPlus) {
                    item + change
                } else {
                    item * change
                }
                reducer(test)
            }.toMutableList()
            inspectCount += items.size
        }

        fun toss(barrel: List<Monke>) {
            items.forEach { item ->
                if (item % testDivisor == 0L) {
                    barrel[testTrue].items.add(item)
                } else {
                    barrel[testFalse].items.add(item)
                }
            }
            items = mutableListOf()
        }
    }

    private fun loader() = load(delimiter = "\n\n").map { it.split("\n") }.map { monke ->
        val opIsPlus = monke[2].contains("+")
        Monke(
            name = monke[0][7].digitToInt(),
            items = monke[1].substringAfter(':').split(",").map { it.trim().toLong() }.toMutableList(),
            opIsPlus = opIsPlus,
            opAmt = monke[2].substringAfter('+'.takeIf { opIsPlus } ?: '*').trim(),
            testDivisor = monke[3].substringAfter('y').trim().toLong(),
            testTrue = monke[4].substringAfter('y').trim().toInt(),
            testFalse = monke[5].substringAfter('y').trim().toInt(),
        )
    }
}