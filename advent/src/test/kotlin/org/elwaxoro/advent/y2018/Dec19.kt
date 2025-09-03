package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.sumOfFactors

/**
 * Day 19: Go With The Flow
 */
class Dec19 : PuzzleDayTester(19, 2018) {

    override fun part1(): Any = loader().let { (ipRegister, inputs) ->
        var ip = 0
        val register = mutableListOf(0, 0, 0, 0, 0, 0)
        while (ip < inputs.size) {
            register[ipRegister] = ip
            val (cmdName, codes) = inputs[ip]
            val cmd = Opcodes162018[cmdName]!!
            cmd.applyM(register, codes)
            ip = register[ipRegister] + 1
        }
        register[0]
    } == 1500

    /**
     * Seems to run forever
     * manual inspection: by loop 25 or so the "very large number" had appeared and some sort of calculation was ongoing
     * for my input, "very large number" register was 1
     * hint from online: the calculation was a sum of factors
     */
    override fun part2(): Any = loader().let { (ipRegister, inputs) ->
        var ip = 0
        val register = mutableListOf(1, 0, 0, 0, 0, 0)
        repeat(25) {
            register[ipRegister] = ip
            val (cmdName, codes) = inputs[ip]
            val cmd = Opcodes162018[cmdName]!!
            cmd.applyM(register, codes)
            ip = register[ipRegister] + 1
            println("$ip $cmdName $codes $register")
        }
        register[1].sumOfFactors()
    } == 18869760

    private fun loader() = load().let { lines ->
        val ipRegister = lines.first().replace("#ip ", "").toInt()
        val inputs = lines.drop(1).map { line ->
            val split = line.split(" ")
            val cmdName = split.first()
            val codes = listOf(0).plus(split.drop(1).map { it.toInt() })
            cmdName to codes
        }
        ipRegister to inputs
    }
}
