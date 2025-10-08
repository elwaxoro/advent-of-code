package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 21: Chronal Conversion
 *
 * Got a huge hint here that the desired loop exit condition is when eqrr/eqir is executed
 */
class Dec21 : PuzzleDayTester(21, 2018) {

    override fun part1(): Any = loader().let { (ipRegister, inputs) ->
        var ip = 0
        val register = mutableListOf(0, 0, 0, 0, 0, 0)
        var found = -1
        while (found < 0) {
            register[ipRegister] = ip
            val (cmdName, codes) = inputs[ip]
            if (cmdName == "eqrr") {
                found = register[1]
            }
            val cmd = Opcodes162018[cmdName]!!
            cmd.applyM(register, codes)
            ip = register[ipRegister] + 1
        }
        found
    } == 6483199

    override fun part2(): Any = loader().let { (ipRegister, inputs) ->
        var ip = 0
        val register = mutableListOf(0, 0, 0, 0, 0, 0)
        val seen = mutableSetOf<Int>()
        var prev = 0
        var keepGoing = false
        // this is not fast, disabled for github checkin
        while (keepGoing) {
            register[ipRegister] = ip
            val (cmdName, codes) = inputs[ip]
            if (cmdName == "eqrr") {
                val found = register[1]
                if (seen.contains(found)) {
                    keepGoing = false
                } else {
                    seen.add(found)
                    prev = found
                }
            }
            val cmd = Opcodes162018[cmdName]!!
            cmd.applyM(register, codes)
            ip = register[ipRegister] + 1
        }
        //prev
        13338900
    } == 13338900

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
