package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 18: Duet
 */
class Dec18 : PuzzleDayTester(18, 2017) {

    override fun part1(): Any = loader().let { cmds ->
        val output = mutableListOf<Long>()
        val prog = Program(
            cmds = cmds, 
            input = mutableListOf(), 
            output = output, 
            isPart1 = true, 
            registerP = 0
        )
        prog.run()
        output.last()
    }

    override fun part2(): Any = loader().let { cmds ->

        val out0 = mutableListOf<Long>()
        val out1 = mutableListOf<Long>()

        val prog0 = Program(
            cmds = cmds,
            input = out1,
            output = out0,
            isPart1 = false,
            registerP = 0
        )

        val prog1 = Program(
            cmds = cmds,
            input = out0,
            output = out1,
            isPart1 = false,
            registerP = 1
        )

        do {
            prog0.run()
            prog1.run()
        } while (out0.isNotEmpty() || out1.isNotEmpty())

        prog1.outputsSent
    }

    private fun loader() = load().map { it.split(" ") }

    private class Program(
        val cmds: List<List<String>>,
        val input: MutableList<Long>,
        val output: MutableList<Long>,
        val isPart1: Boolean,
        val registerP: Long,
    ) {

        var outputsSent = 0
        val registers = mutableMapOf("p" to registerP)
        var idx = 0

        private fun MutableMap<String, Long>.getOrLong(key: String): Long = key.toLongOrNull() ?: getOrDefault(key, 0L)

        fun run() {
            var keepGoing = true
            while (keepGoing) {
                val cmd = cmds[idx]
                when (cmd[0]) {
                    "snd" -> {
                        // snd X plays a sound with a frequency equal to the value of X.
                        output.add(registers.getOrLong(cmd[1]))
                        outputsSent++
                        idx++
                    }

                    "set" -> {
                        // set X Y sets register X to the value of Y.
                        registers[cmd[1]] = registers.getOrLong(cmd[2])
                        idx++
                    }

                    "add" -> {
                        // add X Y increases register X by the value of Y.
                        registers[cmd[1]] = registers.getOrLong(cmd[1]) + registers.getOrLong(cmd[2])
                        idx++
                    }

                    "mul" -> {
                        // mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
                        registers[cmd[1]] = registers.getOrLong(cmd[1]) * registers.getOrLong(cmd[2])
                        idx++
                    }

                    "mod" -> {
                        // mod X Y sets register X to the remainder of dividing the value contained in register X by the value of Y (that is, it sets X to the result of X modulo Y).
                        registers[cmd[1]] = registers.getOrLong(cmd[1]) % registers.getOrLong(cmd[2])
                        idx++
                    }

                    "rcv" -> {
                        if (isPart1) {
                            // rcv X recovers the frequency of the last sound played, but only when the value of X is not zero. (If it is zero, the command does nothing.)
                            keepGoing = registers.getOrLong(cmd[1]) == 0L
                            idx++
                        } else {
                            if (input.isEmpty()) {
                                // don't increment idx, wait here until an input is available
                                keepGoing = false
                            } else {
                                registers[cmd[1]] = input.removeFirst()
                                idx++
                            }
                        }
                    }

                    "jgz" -> {
                        // jgz X Y jumps with an offset of the value of Y, but only if the value of X is greater than zero. (An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction, and so on.)
                        if (registers.getOrLong(cmd[1]) > 0) {
                            idx += registers.getOrLong(cmd[2]).toInt()
                        } else {
                            idx++
                        }
                    }

                    else -> throw IllegalStateException("Unknown cmd: $cmd")
                }
            }
        }
    }
}
