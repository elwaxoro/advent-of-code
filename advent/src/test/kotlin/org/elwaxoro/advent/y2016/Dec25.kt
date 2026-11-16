package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 25: Clock Signal
 */
class Dec25: PuzzleDayTester(25, 2016) {

    override fun part1(): Any = load().let { code ->
        (0..1000).first { a ->
            code.toMutableList().solve(
                    mutableMapOf(
                    "a" to a,
                    "b" to 0,
                    "c" to 0,
                    "d" to 0,
                    )
            ) > 0
        }
    }

    override fun part2(): Any = "Suddenly, you see the sleigh fly past you!"

    /**
     * Using Dec23 solver with an added outputBuffer that starts at 1 (since the output sequence must be 0, 1, 0, 1...)
     */
    private fun MutableList<String>.solve(r: MutableMap<String, Int>): Int {
        val outputBuffer = mutableListOf(1)
        var i = 0
        while (i < this.size) {
            val s = this[i].split(" ")
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
                "out" -> {
                    // new command for Dec25
                    val out = r.getOrInt(s[1])
                    if ((out == 0 || out == 1) && outputBuffer.last() != out) {
                        outputBuffer.add(out)
                        if (outputBuffer.size > 10) {
                            println("SUCCESS? $outputBuffer")
                            return r["a"]!!
                        }
                    } else {
                        // broken run. out was either not a 0 or 1 or it matched the last item in the output buffer
                        return -1
                    }
                    i++
                }
            }
        }
        // broken run or some other bug
        return -1000
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