package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.toBinaryLong

/**
 * Day 24: Crossed Wires
 * with some help from the subreddit on part 2!
 */
class Dec24 : PuzzleDayTester(24, 2024) {

    override fun part1(): Any = loader().let { (wires, gates) ->
        while (wires.any { it.key.startsWith("z") && it.value == null }) pulse(wires, gates)
        wires.filter { it.key.startsWith("z") }.toSortedMap().values.mapNotNull { it }.reversed().toBinaryLong()
    }

    /**
     * activate all gates with sufficient inputs and no output yet
     */
    private fun pulse(wires: MutableMap<String, Int?>, gates: List<Gate>) = gates.forEach { g -> wires[g.out] = g.run(wires[g.a], wires[g.b]) }

    /**
     * oh look its an adding machine, specifically a ripple carry adder:
     * https://en.wikipedia.org/wiki/Adder_(electronics)
     * this picture is very helpful: https://en.wikipedia.org/wiki/Adder_(electronics)#/media/File:Fulladder.gif
     * input numbers are x00-x44 and y00-y44 output number is z00-z45
     * each pair (bit) of xNN-yNN MUST eventually output to the same zNN
     * each pair (bit) has 2 outputs (except the most-significant bit): XOR into Z, AND into some unknown cNN (carry bit to next-significant bit)
     * each pair (bit) MUST connect to the previous carry bit (next layer down as a XOR and an AND) except the first pair
     * last z will be the carry bit (fed by OR)
     * every output to z must be a XOR (except z45, which is fed by OR)
     * every XOR must have at least one x,y input or a z output
     * XOR never feeds OR
     * AND always feeds OR (except the last register which feeds 1 OR and 1 XOR)
     */
    override fun part2(): Any = loader().let { (_, gates) ->
        gates.filter { g -> g.isBad(gates) }.sortedBy { it.out }.joinToString(",") { it.out }
    }

    private fun loader() = load(delimiter = "\n\n").let { (rawWires, rawGates) ->
        val wires: MutableMap<String, Int?> = rawWires.split("\n").associate {
            val (name, value) = it.split(": ")
            name to value.toInt()
        }.toMutableMap()

        val gates = rawGates.split("\n").map { Gate.fromString(it) }

        gates.forEach { op ->
            wires.putIfAbsent(op.a, null)
            wires.putIfAbsent(op.b, null)
            wires.putIfAbsent(op.out, null)
        }

        wires to gates
    }

    data class Gate(
        val a: String,
        val op: String,
        val b: String,
        val out: String,
        var outVal: Int? = null
    ) {
        companion object {
            fun fromString(str: String): Gate {
                val (function, output) = str.split(" -> ")
                val (a, op, b) = function.split(" ")
                return Gate(a, op, b, output)
            }
        }

        fun run(aVal: Int?, bVal: Int?): Int? {
            if (outVal != null) {
                return outVal
            } else if (aVal == null || bVal == null) {
                return null
            } else {
                outVal = when (op) {
                    "AND" -> aVal and bVal
                    "OR" -> aVal or bVal
                    "XOR" -> aVal xor bVal
                    else -> throw IllegalStateException("what the duck")
                }
            }
            return outVal
        }

        fun isBad(gates: List<Gate>): Boolean =
            if (out.isHigherZ() && op != "XOR") {
                // every gate to z must be XOR (except z45, which is fed by OR)
                true
            } else if (op == "XOR" && listOf(a, b, out).none { it.isXYZ() }) {
                // every XOR must have at least one x,y input or a z output
                true
            } else if (op == "AND" && listOf(a, b).none { it.isHigh() }) {
                // AND always feeds OR (except the last register which feeds 1 OR and 1 XOR)
                gates.any { gg -> gg.op != "OR" && feeds(gg) }
            } else if (op == "XOR") {
                // XOR never feeds OR
                gates.any { gg -> gg.op == "OR" && feeds(gg) }
            } else {
                // looks good to me
                false
            }

        private fun feeds(that: Gate): Boolean = out == that.a || out == that.b

        private fun String.isHigh() = this.endsWith("00")
        private fun String.isHigherZ() = startsWith('z') && this != "z45"
        private fun String.isXYZ() = listOf('x', 'y', 'z').contains(first())
    }
}
