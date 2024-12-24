package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.toBinaryLong

/**
 * Day 24: Crossed Wires
 */
class Dec24 : PuzzleDayTester(24, 2024) {

    override fun part1(): Any = loader().let { (wires, connections) ->
        while (wires.any { it.key.startsWith("z") && it.value == null }) {
            pulse(wires, connections)
        }
        wires.filter { it.key.startsWith("z") }.toSortedMap().values.mapNotNull { it }.reversed().toBinaryLong()
    }

    private fun pulse(wires: MutableMap<String, Int?>, connections: List<Op>) = connections.forEach { op -> wires[op.out] = op.runOp(wires[op.a], wires[op.b]) }

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
