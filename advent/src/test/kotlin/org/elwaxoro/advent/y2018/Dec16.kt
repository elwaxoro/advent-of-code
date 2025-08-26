package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 16: Chronal Classification
 *
 *
 */
class Dec16 : PuzzleDayTester(16, 2018) {

    val opcodes = listOf<Pair<String, Opcode>>(
        "addr" to Opcode { r, i -> r[i[1]] + r[i[2]] }, // addr (add register) stores into register C the result of adding register A and register B
        "addi" to Opcode { r, i -> r[i[1]] + i[2] }, // addi (add immediate) stores into register C the result of adding register A and value B
        "mulr" to Opcode { r, i -> r[i[1]] * r[i[2]] }, // mulr (multiply register) stores into register C the result of multiplying register A and register B
        "muli" to Opcode { r, i -> r[i[1]] * i[2] }, // muli (multiply immediate) stores into register C the result of multiplying register A and value B
        "banr" to Opcode { r, i -> r[i[1]] and r[i[2]] }, // banr (bitwise AND register) stores into register C the result of the bitwise AND of register A and register B
        "bani" to Opcode { r, i -> r[i[1]] and i[2].toLong() }, // bani (bitwise AND immediate) stores into register C the result of the bitwise AND of register A and value B
        "borr" to Opcode { r, i -> r[i[1]] or r[i[2]] }, // borr (bitwise OR register) stores into register C the result of the bitwise OR of register A and register B
        "bori" to Opcode { r, i -> r[i[1]] or i[2].toLong() }, // bori (bitwise OR immediate) stores into register C the result of the bitwise OR of register A and value B
        "setr" to Opcode { r, i -> r[i[1]] }, // setr (set register) copies the contents of register A into register C. (Input B is ignored.)
        "seti" to Opcode { r, i -> i[1].toLong() }, // seti (set immediate) stores value A into register C. (Input B is ignored.)
        "gtir" to Opcode { r, i -> 1L.takeIf { i[1] > r[i[2]] } ?: 0L }, // gtir (greater-than immediate/register) sets register C to 1 if value A is greater than register B. Otherwise, register C is set to 0
        "gtri" to Opcode { r, i -> 1L.takeIf { r[i[1]] > i[2] } ?: 0L }, // gtri (greater-than register/immediate) sets register C to 1 if register A is greater than value B. Otherwise, register C is set to 0
        "gtrr" to Opcode { r, i -> 1L.takeIf { r[i[1]] > r[i[2]] } ?: 0L }, // gtrr (greater-than register/register) sets register C to 1 if register A is greater than register B. Otherwise, register C is set to 0
        "eqir" to Opcode { r, i -> 1L.takeIf { i[1].toLong() == r[i[2]] } ?: 0L }, // eqir (equal immediate/register) sets register C to 1 if value A is equal to register B. Otherwise, register C is set to 0
        "eqri" to Opcode { r, i -> 1L.takeIf { r[i[1]] == i[2].toLong() } ?: 0L }, // eqri (equal register/immediate) sets register C to 1 if register A is equal to value B. Otherwise, register C is set to 0
        "eqrr" to Opcode { r, i -> 1L.takeIf { r[i[1]] == r[i[2]] } ?: 0L }, // eqrr (equal register/register) sets register C to 1 if register A is equal to register B. Otherwise, register C is set to 0
    ).toMap()

    override fun part1(): Any = loader().let { (tests, _) ->
        tests.map { test ->
            opcodes.filter { (_, opcode) -> opcode.passes(test) }
        }.filter { it.size >= 3 }.size
    } == 605

    override fun part2(): Any = loader().let { (tests, inputs) ->
        //assign all possible IDs to opcodes in a list
        val nameToId = opcodes.keys.associateWith { (0..15).toMutableList() }
        //run all tests thru one opcode, for each opcode it fails - remove the ID from the ID list
        tests.forEach { test ->
            nameToId.forEach { (name, ids) ->
                if (!opcodes[name]!!.passes(test)) {
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
        inputs.fold(listOf(0L, 0L, 0L, 0L)) { acc, input ->
            opcodes[idMap[input[0]]]!!.apply(acc, input)
        }[0]
    } == 653L

    fun interface Opcode {

        fun internal(registers: MutableList<Long>, input: List<Int>): Long

        fun apply(registers: List<Long>, input: List<Int>): List<Long> = registers.toMutableList().also {
            it[input[3]] = internal(it, input)
        }

        fun passes(test: Test): Boolean = apply(test.before, test.input) == test.after
    }

    data class Test(
        val before: List<Long>,
        val after: List<Long>,
        val input: List<Int>,
    ) {
        fun id(): Int = input[0]
    }

    fun loader() = load(delimiter = "\n\n\n\n").let { (tests, inputs) ->
        tests.split("\n\n").map { lines ->
            lines.split("\n").map { line ->
                line.replace("Before: [", "").replace("After:  [", "").replace("]", "").replace(", ", ",").replace(" ", ",").split(",").map {
                    it.toLong()
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
