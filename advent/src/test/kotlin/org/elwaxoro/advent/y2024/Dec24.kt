package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.toBinaryLong

/**
 * Day 24: Crossed Wires
 */
class Dec24 : PuzzleDayTester(24, 2024) {

    override fun part1(): Any = loader().let { (wires, operations) ->
        while (wires.any { it.key.startsWith("z") && it.value == null }) {
            pulse(wires, operations)
        }
        wires.filter { it.key.startsWith("z") }.toSortedMap().values.mapNotNull { it }.reversed().toBinaryLong()
    }

    /**
     * this is a ripple carry adder. ugh.
     * https://en.wikipedia.org/wiki/Adder_(electronics)
     * on the plus side, we don't actually need to FIX anything here! just find bad gates
     * input numbers are x00-x44 and y00-y44 output number is z00-z45
     * each pair (bit) of xNN-yNN MUST eventually output to the same zNN
     * each pair (bit) has 2 outputs (except the most-significant bit): XOR into Z, AND into some unknown cNN (carry bit to next-significant bit)
     * each pair (bit) MUST be fed by the previous carry bit except the first one
     */
    override fun part2(): Any = loader().let { (wires, operations) ->
        val lowOrderZ = "z45"
        val highOrderX = "x00"

        val bad = operations.filter { op ->
            if (op.out.isZ() && op.op != "XOR" && op.out != lowOrderZ) {
                // every output to zNN must be a XOR (except z45)
                true
            } else if (op.op == "XOR" && !op.out.isXYZ() && !op.a.isXYZ() && !op.b.isXYZ()) {
                // every XOR must have at least one x,y input or a z output
                true
            } else if (op.op == "AND" && op.a != highOrderX && op.b != highOrderX) {
                operations.any { sop -> sop.op != "OR" && (op.out == sop.a || op.out == sop.b) }
            } else if (op.op == "XOR") {
                operations.any { sop -> sop.op == "OR" && (op.out == sop.a || op.out == sop.b) }
            } else {
                false
            }
        }
        bad.sortedBy { it.out }.joinToString(",") { it.out }
    }

    private val xyz = listOf('x', 'y', 'z')
    private fun String.isZ() = startsWith('z')
    private fun String.isXYZ() = xyz.contains(first())

    private fun pulse(wires: MutableMap<String, Int?>, operations: List<Op>) = operations.forEach { op -> wires[op.out] = op.runOp(wires[op.a], wires[op.b]) }

    private fun loader() = load(delimiter = "\n\n").let { (rawWires, rawOps) ->
        val wires: MutableMap<String, Int?> = rawWires.split("\n").associate {
            val (name, value) = it.split(": ")
            name to value.toInt()
        }.toMutableMap()

        val ops = rawOps.split("\n").map { Op.fromString(it) }

        ops.forEach { op ->
            wires.putIfAbsent(op.a, null)
            wires.putIfAbsent(op.b, null)
            wires.putIfAbsent(op.out, null)
        }

        wires to ops
    }

    data class Op(
        val a: String,
        val op: String,
        val b: String,
        val out: String,
        var outVal: Int? = null
    ) {
        companion object {
            fun fromString(str: String): Op {
                val (function, output) = str.split(" -> ")
                val (a, op, b) = function.split(" ")
                return Op(a, op, b, output)
            }
        }

        fun runOp(aVal: Int?, bVal: Int?): Int? {
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
    }
}
