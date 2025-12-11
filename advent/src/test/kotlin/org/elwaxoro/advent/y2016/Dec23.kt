package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: Safe Cracking
 * 7 eggs on painting
 */
class Dec23: PuzzleDayTester(23, 2016) {

    override fun part1(): Any = load().toMutableList().solve(
        mutableMapOf(
            "a" to 7,
            "b" to 0,
            "c" to 0,
            "d" to 0,
        )
    ) == 12560

    override fun part2(): Any = load().toMutableList().solve(
        mutableMapOf(
            "a" to 12,
            "b" to 0,
            "c" to 0,
            "d" to 0,
        )
    )

    private fun MutableList<String>.solve(r: MutableMap<String, Int>): Int {
//        return 1 // TODO
        var i = 0
        while (i < this.size) {
            val s = this[i].split(" ")
//            println("before: idx $i $r cmd: $s;  $this")
            when (s[0]) {
                "tgl" -> {
                    // instruction modifier!
                    // tgl X
                    val target = r.getOrInt(s[1]) + i
                    if (target < this.size) {
                        val instruction = this[target]
                        val split = instruction.split(" ")
                        val argCount = split.size - 1
                        if (argCount == 1) {
                            if (split[0] == "inc") {
                                this[target] = instruction.replace("inc", "dec")
                            } else {
                                this[target] = instruction.replace(split[0], "inc")
                            }
                        } else {
                            if (split[0] == "jnz") {
                                this[target] = instruction.replace("jnz", "cpy")
                            } else {
                                this[target] = instruction.replace(split[0], "jnz")
                            }
                        }
//                        println("changed $instruction into ${this[target]}")
                    } else {
//                        println("tried to toggle out of bounds: $target")
                    }
                    i++
                }
                "cpy" -> {
                    r[s[2]] = r.getOrInt(s[1])
                    i++
                }
                "inc" -> {
                    r[s[1]] = r[s[1]]!! + 1
                    i++
                }
                "dec" -> {
                    r[s[1]] = r[s[1]]!! - 1
                    i++
                }
                "jnz" -> {
                    val x = r.getOrInt(s[1])
                    val y = r.getOrInt(s[2])
                    if (x == 0) {
                        i++
                    } else {
                        i += y
                    }
                }
            }
//            println("after: idx $i $r cmd: $s;  $this")
        }
        return r["a"]!!
    }
    
    private fun String.isInt(): Boolean =
        try {
            this.toInt()
            true
        } catch (ex: Exception) {
            false
        }

    private fun Map<String, Int>.getOrInt(x: String) = this[x] ?: x.toInt()
    
}
