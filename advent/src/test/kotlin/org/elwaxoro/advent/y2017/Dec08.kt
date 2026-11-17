package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 8: I Heard You Like Registers
 */
class Dec08 : PuzzleDayTester(8, 2017) {

    override fun part1(): Any = solver().first

    override fun part2(): Any = solver().second

    private fun solver(): Pair<Int, Int> {
        val register = mutableMapOf<String, Int>()
        var maxEver = -1

        load().forEach { line ->
            val tokens = line.split(" ")
            val reg1 = tokens[0]
            val val1 = register.getOrDefault(reg1, 0)
            val cmd1 = tokens[1]
            val amt1 = tokens[2].toInt()
            val ignore = tokens[3]
            val reg2 = tokens[4]
            val val2 = register.getOrDefault(reg2, 0)
            val cmd2 = tokens[5]
            val amt2 = tokens[6].toInt()

            val passes = when (cmd2) {
                "<" -> val2 < amt2
                "<=" -> val2 <= amt2
                ">" -> val2 > amt2
                ">=" -> val2 >= amt2
                "==" -> val2 == amt2
                "!=" -> val2 != amt2
                else -> false
            }

            val newVal = when (cmd1) {
                "inc" -> val1 + amt1
                "dec" -> val1 - amt1
                else -> 0
            }

            if (passes) {
                if (newVal > maxEver) {
                    maxEver = newVal
                }
                register[reg1] = newVal
            }
        }
        return register.values.max() to maxEver
    }
}
