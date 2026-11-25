package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: Coprocessor Conflagration
 */
class Dec23 : PuzzleDayTester(23, 2017) {

    /**
     * Basically Dec 18 with tweaks
     */
    override fun part1(): Any = loader().let { cmds ->
        val prog = Program(cmds, registerA = 0)
        prog.run()
        prog.mulCtr
    }

    /**
     * oh crap this will never finish.
     * I got some hints for this one, tl;dr is: convert the assembly into kotlin directly and run that
     */
    override fun part2(): Any = loader().let { cmds ->
        val seed = cmds[0][2].toLong() // Reads 93 from "set b 93"
        val startB = seed * 100L + 100_000L // 109,300
        val endC = startB + 17_000L // 126,300
        val step = -cmds[30][2].toLong() // Reads -17 from "sub b -17" -> 17

        (startB..endC step step).count { !isPrime(it) }
    }

    private fun isPrime(n: Long): Boolean {
        if (n < 2L) return false
        if (n == 2L || n == 3L) return true
        if (n % 2L == 0L || n % 3L == 0L) return false
        var i = 5L
        while (i * i <= n) {
            if (n % i == 0L || n % (i + 2L) == 0L) return false
            i += 6L
        }
        return true
    }

    private fun loader() = load().map { it.split(" ") }

    private class Program(
        val cmds: List<List<String>>,
        val registerA: Long,
    ) {

        val registers = mutableMapOf("a" to registerA)
        var idx = 0
        var mulCtr = 0

        private fun MutableMap<String, Long>.getOrLong(key: String): Long = key.toLongOrNull() ?: getOrDefault(key, 0L)

        fun run() {
            var keepGoing = true
            while (keepGoing) {
                val cmd = cmds[idx]
                when (cmd[0]) {
                    "set" -> {
                        // set X Y sets register X to the value of Y.
                        registers[cmd[1]] = registers.getOrLong(cmd[2])
                        idx++
                    }

                    "sub" -> {
                        // sub X Y decreases register X by the value of Y
                        registers[cmd[1]] = registers.getOrLong(cmd[1]) - registers.getOrLong(cmd[2])
                        idx++
                    }

                    "mul" -> {
                        // mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
                        registers[cmd[1]] = registers.getOrLong(cmd[1]) * registers.getOrLong(cmd[2])
                        mulCtr++
                        idx++
                    }

                    "jnz" -> {
                        // jnz X Y jumps with an offset of the value of Y, but only if the value of X is not zero. (An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction, and so on.)
                        if (registers.getOrLong(cmd[1]) != 0L) {
                            idx += registers.getOrLong(cmd[2]).toInt()
                        } else {
                            idx++
                        }
                    }

                    else -> throw IllegalStateException("Unknown cmd: $cmd")
                }
                if (idx < 0 || idx >= cmds.size) {
                    keepGoing = false
                }
            }
        }
    }
}