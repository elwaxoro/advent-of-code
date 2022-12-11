package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import java.math.BigInteger

/**
 * Day 11: Monkey in the Middle
 */
class Dec11 : PuzzleDayTester(11, 2022) {

    override fun part1(): Any = loader().let { barrel ->
        (1..20).forEach { round ->
            barrel.forEach { monke ->
                monke.inspect()
                monke.toss(barrel)
            }
            println("round: $round = ${barrel.map { it.display() }}")
            barrel.map { it.display() }
        }
        barrel.map { it.inspectCount }.sorted().takeLast(2).let { (a, b) ->
            a * b
        }
    }

    override fun part2(): Any = loader().let { barrel ->
        (1..10000).forEach { round ->
            barrel.forEach { monke ->
                monke.inspect(hasRelief = false)
                monke.toss(barrel)
            }
            if(round % 100 == 0) {
                println("round: $round = ${barrel.map { it.display() }}")
            }
//            barrel.map { it.display() }
        }
        barrel.map { it.inspectCount }.sorted().takeLast(2).let { (a, b) ->
            a * b
        }
    }

    private fun loader() = load(delimiter = "\n\n").map { it.split("\n") }.map { monke ->
        val opIsPlus = monke[2].contains("+")
        Monke(
            name = monke[0][7].digitToInt(),
            items = monke[1].substringAfter(':').split(",").map { BigInteger(it.trim()) }.toMutableList(),
            opIsPlus = opIsPlus,
            opAmt = monke[2].substringAfter('+'.takeIf { opIsPlus } ?: '*').trim(),
            testDivisor = BigInteger(monke[3].substringAfter('y').trim()),
            testTrue = monke[4].substringAfter('y').trim().toInt(),
            testFalse = monke[5].substringAfter('y').trim().toInt(),
        )
    }

    /**
     * 1. monke inspects items in list: worry change is defined by op
     * 2. after inspect is relief: worry change / 3 round down (int)
     * 3. monke runs test and throws to another monke
     */
    data class Monke(
        val name: Int, // Monkey 0:
        var items: MutableList<BigInteger>, // Starting items: 79, 98 (worry level for each item)
        val opIsPlus: Boolean, // Operation: new = old */+ 19 (monke inspect. worry changes)
        val opAmt: String, // Operation: new = old */+ 19
        val testDivisor: BigInteger, // Test: divisible by 23 (monke use new worry, throw item to new monke)
        val testTrue: Int, // If true: throw to monkey 2
        val testFalse: Int, // If false: throw to monkey 3
        var inspectCount: BigInteger = BigInteger.ZERO,
    ) {
        private val three = BigInteger("3")

        fun inspect(hasRelief: Boolean = true) {
            items = items.map { item ->
                val change = if (opAmt == "old") {
                    item
                } else {
                    BigInteger(opAmt)
                }
                val test = if (opIsPlus) {
                    item.plus(change)
                } else {
                    item * change
                }
                if(hasRelief) {
                    test.divide(three)
                } else {
                    test.mod((9699690).toBigInteger())
                }
            }.toMutableList()
            inspectCount = inspectCount.plus(items.size.toBigInteger())
        }

        fun toss(barrel: List<Monke>) {
            items.forEach { item ->
                if(item.mod(testDivisor) == BigInteger.ZERO) {
                    barrel[testTrue].items.add(item)
                } else {
                    barrel[testFalse].items.add(item)
                }
            }
            items = mutableListOf()
        }

        fun display(): String = "Monkey $name: $items"
    }
}