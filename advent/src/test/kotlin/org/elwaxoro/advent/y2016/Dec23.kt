package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: Safe Cracking
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

    /**
     * Got some help here to make it not run forever
     */
    override fun part2(): Any = load().toMutableList().solve(
        mutableMapOf(
            "a" to 12,
            "b" to 0,
            "c" to 0,
            "d" to 0,
        )
    )

    private fun MutableList<String>.solve(r: MutableMap<String, Int>): Int {
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
                    // cpy X Y / inc A / dec Y / jnz Y -2 / dec B / jnz B -5
                    // is A += X * B. Part 2 counts this up to 12!, one inc at a time.
                    val mul = multiply(i)
                    if (mul != null && r[mul.factorReg]!! > 0) {
                        r[mul.into] = r[mul.into]!! + r.getOrInt(s[1]) * r[mul.factorReg]!!
                        r[s[2]] = 0
                        r[mul.factorReg] = 0
                        i += 6
                    } else {
                        r[s[2]] = r.getOrInt(s[1])
                        i++
                    }
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
    
    /**
     * The add loop above `tgl` multiplies. Everything else is the factorial
     * countdown plus a final `94 * 80` added onto `a` (7! + 7520 = 12560).
     */
    private fun List<String>.multiply(i: Int): Mul? {
        if (i + 5 >= size) return null
        val inc = this[i + 1].split(" ")
        val decY = this[i + 2].split(" ")
        val jnzY = this[i + 3].split(" ")
        val decB = this[i + 4].split(" ")
        val jnzB = this[i + 5].split(" ")
        val y = this[i].split(" ")[2]
        if (inc.size == 2 && inc[0] == "inc" &&
            decY.size == 2 && decY[0] == "dec" && decY[1] == y &&
            jnzY.size == 3 && jnzY[0] == "jnz" && jnzY[1] == y && jnzY[2] == "-2" &&
            decB.size == 2 && decB[0] == "dec" &&
            jnzB.size == 3 && jnzB[0] == "jnz" && jnzB[1] == decB[1] && jnzB[2] == "-5"
        ) {
            return Mul(inc[1], decB[1])
        }
        return null
    }

    private data class Mul(val into: String, val factorReg: String)

    private fun Map<String, Int>.getOrInt(x: String) = this[x] ?: x.toInt()
    
}
