package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.y2018.Dec16.Test

/**
 * Day 16: Chronal Classification
 */
class Dec16 : PuzzleDayTester(16, 2018) {

    override fun part1(): Any = loader().let { (tests, _) ->
        tests.map { test ->
            Opcodes162018.filter { (_, opcode) -> opcode.passes(test) }
        }.filter { it.size >= 3 }.size
    } == 605

    override fun part2(): Any = loader().let { (tests, inputs) ->
        //assign all possible IDs to opcodes in a list
        val nameToId = Opcodes162018.keys.associateWith { (0..15).toMutableList() }
        //run all tests thru one opcode, for each opcode it fails - remove the ID from the ID list
        tests.forEach { test ->
            nameToId.forEach { (name, ids) ->
                if (!Opcodes162018[name]!!.passes(test)) {
                    ids.remove(test.id())
                }
            }
        }
        // split lists with a single entry from the rest, then remove those singles from larger lists in a loop until each key has a single value left
        while (nameToId.any { (_, v) -> v.size > 1 }) {
            val (done, pending) = nameToId.entries.partition { (_, v) -> v.size == 1 }
            done.forEach { (_, dv) -> pending.forEach { (_, pv) -> pv.removeAll(dv) } }
        }
        val idMap = nameToId.entries.associate { (k, v) -> v.single() to k }
        // run the inputs!
        inputs.fold(listOf(0, 0, 0, 0)) { acc, input ->
            Opcodes162018[idMap[input[0]]]!!.apply(acc, input)
        }[0]
    } == 653

    data class Test(
        val before: List<Int>,
        val after: List<Int>,
        val input: List<Int>,
    ) {
        fun id(): Int = input[0]
    }

    fun loader() = load(delimiter = "\n\n\n\n").let { (tests, inputs) ->
        tests.split("\n\n").map { lines ->
            lines.split("\n").map { line ->
                line.replace("Before: [", "").replace("After:  [", "").replace("]", "").replace(", ", ",").replace(" ", ",").split(",").map {
                    it.toInt()
                }
            }.let { (before, input, after) ->
                Test(before, after, input.map { it.toInt() })
            }
        } to
                inputs.split("\n").map { line ->
                    line.split(" ").map { it.toInt() }
                }
    }
}

val Opcodes162018 = listOf(
    "addr" to Opcode162018 { r, i -> r[i[1]] + r[i[2]] }, // addr (add register) stores into register C the result of adding register A and register B
    "addi" to Opcode162018 { r, i -> r[i[1]] + i[2] }, // addi (add immediate) stores into register C the result of adding register A and value B
    "mulr" to Opcode162018 { r, i -> r[i[1]] * r[i[2]] }, // mulr (multiply register) stores into register C the result of multiplying register A and register B
    "muli" to Opcode162018 { r, i -> r[i[1]] * i[2] }, // muli (multiply immediate) stores into register C the result of multiplying register A and value B
    "banr" to Opcode162018 { r, i -> r[i[1]] and r[i[2]] }, // banr (bitwise AND register) stores into register C the result of the bitwise AND of register A and register B
    "bani" to Opcode162018 { r, i -> r[i[1]] and i[2] }, // bani (bitwise AND immediate) stores into register C the result of the bitwise AND of register A and value B
    "borr" to Opcode162018 { r, i -> r[i[1]] or r[i[2]] }, // borr (bitwise OR register) stores into register C the result of the bitwise OR of register A and register B
    "bori" to Opcode162018 { r, i -> r[i[1]] or i[2] }, // bori (bitwise OR immediate) stores into register C the result of the bitwise OR of register A and value B
    "setr" to Opcode162018 { r, i -> r[i[1]] }, // setr (set register) copies the contents of register A into register C. (Input B is ignored.)
    "seti" to Opcode162018 { r, i -> i[1] }, // seti (set immediate) stores value A into register C. (Input B is ignored.)
    "gtir" to Opcode162018 { r, i -> 1.takeIf { i[1] > r[i[2]] } ?: 0 }, // gtir (greater-than immediate/register) sets register C to 1 if value A is greater than register B. Otherwise, register C is set to 0
    "gtri" to Opcode162018 { r, i -> 1.takeIf { r[i[1]] > i[2] } ?: 0 }, // gtri (greater-than register/immediate) sets register C to 1 if register A is greater than value B. Otherwise, register C is set to 0
    "gtrr" to Opcode162018 { r, i -> 1.takeIf { r[i[1]] > r[i[2]] } ?: 0 }, // gtrr (greater-than register/register) sets register C to 1 if register A is greater than register B. Otherwise, register C is set to 0
    "eqir" to Opcode162018 { r, i -> 1.takeIf { i[1] == r[i[2]] } ?: 0 }, // eqir (equal immediate/register) sets register C to 1 if value A is equal to register B. Otherwise, register C is set to 0
    "eqri" to Opcode162018 { r, i -> 1.takeIf { r[i[1]] == i[2] } ?: 0 }, // eqri (equal register/immediate) sets register C to 1 if register A is equal to value B. Otherwise, register C is set to 0
    "eqrr" to Opcode162018 { r, i -> 1.takeIf { r[i[1]] == r[i[2]] } ?: 0 }, // eqrr (equal register/register) sets register C to 1 if register A is equal to register B. Otherwise, register C is set to 0
).toMap()

fun interface Opcode162018 {
    fun internal(registers: MutableList<Int>, input: List<Int>): Int
    fun apply(registers: List<Int>, input: List<Int>): List<Int> = registers.toMutableList().also { it[input[3]] = internal(it, input) }
    fun applyM(registers: MutableList<Int>, input: List<Int>) { registers[input[3]] = internal(registers, input) }
    fun passes(test: Test): Boolean = apply(test.before, test.input) == test.after
}
