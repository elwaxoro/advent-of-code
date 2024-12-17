package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.pow
import org.elwaxoro.advent.remove
import org.elwaxoro.advent.xor

/**
 * Day 17: Chronospatial Computer
 */
class Dec17 : PuzzleDayTester(17, 2024) {

    /**
     * suspiciously easy...
     */
    override fun part1(): Any = Debugger.init(load(delimiter = "\n\n")).run()

    /**
     * AHHHHHH INFINITE RUNTIIIIIME
     *
     * input A vs output follows the form:
     * A 1 = 7
     * A 8 = 7,7
     * A 64 = 6,7,7
     * A 512 = 6,6,7,7
     * A 4096 = 6,6,6,7,7
     * A 32768 = 6,6,6,6,7,7
     * A 262144 = 6,6,6,6,6,7,7
     * A 2097152 = 6,6,6,6,6,6,7,7
     * A 16777216 = 6,6,6,6,6,6,6,7,7
     * A 134217728 = 6,6,6,6,6,6,6,6,7,7
     * A 1073741824 = 6,6,6,6,6,6,6,6,6,7,7
     * increase by powers of 8 to add length to output
     *
     * increasing A sequentially seems to impact early outputs before later ones:
     * A 32768 = 6,6,6,6,7,7
     * A 32769 = 7,6,6,6,7,7
     * A 32770 = 5,6,6,6,7,7
     * A 32771 = 6,6,6,6,7,7
     * A 32772 = 2,6,6,6,7,7
     * A 32773 = 3,6,6,6,7,7
     * A 32774 = 0,6,6,6,7,7
     * A 32775 = 1,6,6,6,7,7
     * A 32776 = 7,7,6,6,7,7
     * A 32777 = 5,7,6,6,7,7
     * A 32778 = 1,7,6,6,7,7
     *
     * idea: solve this backwards
     * start at A=0, add 1 until the end of program is matched by another number
     * each match: increase by multiple of 8 to get more output length
     *
     * GOAL 2,4,1,3,7,5,4,7,0,3,1,5,5,5,3,0
     * PARTIAL! A 6 = 0
     * PARTIAL! A 49 = 3,0
     * PARTIAL! A 393 = 5,3,0
     * PARTIAL! A 3145 = 5,5,3,0
     * PARTIAL! A 25161 = 5,5,5,3,0
     * PARTIAL! A 201290 = 1,5,5,5,3,0
     * PARTIAL! A 1610321 = 3,1,5,5,5,3,0
     * PARTIAL! A 14099780 = 0,3,1,5,5,5,3,0
     * PARTIAL! A 112798241 = 7,0,3,1,5,5,5,3,0
     * PARTIAL! A 902389516 = 4,7,0,3,1,5,5,5,3,0
     * PARTIAL! A 7219116130 = 5,4,7,0,3,1,5,5,5,3,0
     * PARTIAL! A 57752929045 = 7,5,4,7,0,3,1,5,5,5,3,0
     * PARTIAL! A 462023432360 = 3,7,5,4,7,0,3,1,5,5,5,3,0
     * PARTIAL! A 3696187458936 = 1,3,7,5,4,7,0,3,1,5,5,5,3,0
     * PARTIAL! A 29569499671493 = 4,1,3,7,5,4,7,0,3,1,5,5,5,3,0
     * SOLVED!! A 236555997372013 = 2,4,1,3,7,5,4,7,0,3,1,5,5,5,3,0
     */
    override fun part2(): Any = Debugger.init(load(delimiter = "\n\n")).let { debugger ->
        val goal = debugger.prog.joinToString(",")
        println("GOAL $goal")
        var a = 0L
        while (true) {
            debugger.reset(a)
            val attempt = debugger.run()
            if (goal == attempt) {
                println("SOLVED!! A $a = $attempt")
                return a
            } else if (attempt == goal.takeLast(attempt.length)) {
                println("PARTIAL! A $a = $attempt")
                a *= 8
            } else {
                a += 1
            }
        }
    }

    private data class Debugger(
        var a: Long = 0,
        var b: Long = 0,
        var c: Long = 0,
        var i: Int = 0,
        val prog: List<Int>
    ) {

        companion object {
            fun init(input: List<String>): Debugger {
                val (a, b, c) = input[0].split("\n").map { it.remove("Register A: ", "Register B: ", "Register C: ").toLong() }
                val prog = input[1].remove("Program: ").split(",").map { it.toInt() }
                return Debugger(a, b, c, 0, prog)
            }
        }

        fun run(): String {
            val out = mutableListOf<Long>()
            while (i < prog.size) {
                val (opcode, operand) = listOf(prog[i], prog[i + 1])
                when (opcode) {
                    0 -> a = (a / 2.pow(combo(operand)))
                    1 -> b = b xor operand
                    2 -> b = combo(operand) % 8
                    3 -> if (a != 0L) i = operand - 2 // -2 for +=2 at bottom
                    4 -> b = b xor c
                    5 -> out.add(combo(operand) % 8)
                    6 -> b = (a / 2.pow(combo(operand)))
                    7 -> c = (a / 2.pow(combo(operand)))
                }
                i += 2
            }
            return out.joinToString(",")
        }

        fun combo(operand: Int): Long =
            when (operand) {
                4 -> a
                5 -> b
                6 -> c
                else -> operand.toLong() // supposedly 7+ is illegal but they never happen
            }

        fun reset(newA: Long) {
            a = newA
            b = 0
            c = 0
            i = 0
        }
    }
}
