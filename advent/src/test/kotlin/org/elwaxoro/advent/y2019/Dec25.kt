package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.combinations
import org.elwaxoro.advent.remove

/**
 * Day 25: Cryostasis
 */
class Dec25 : PuzzleDayTester(25, 2019) {

    /**
     * I did this by hand as I explored the maze, it gathers up all the non-deadly items and stops at the security checkpoint
     * From there, just gotta try all combinations of items by dropping the others
     */
    override fun part1(): Any = runBlocking {
        val collectPassages = """
            west
            take mutex
            south
            south
            east
            take weather machine
            west
            south
            take polygon
            north
            north
            north
            east
            south
            take hologram
            north
            inv
            north
            north
            north
            take semiconductor
            east
            take prime number
            west
            west
            take monolith
            east
            south
            west
            north
            take jam
            west

        """.trimIndent()

        // now just need to try every combo of the items!
        val items = listOf("mutex", "weather machine", "polygon", "hologram", "semiconductor", "prime number", "monolith", "jam")
        (1..items.size).firstNotNullOf { i ->
            items.combinations(i).toList().mapNotNull { combo ->
                val toDrop = items.minus(combo)
                val commands = collectPassages + toDrop.joinToString("\n") { "drop $it" } + "\nnorth\n\n"
                Droid(loadToLong(delimiter = ","), commands).runner()
            }.singleOrNull()
        }
    } == 35717128

    private class Droid(
        val code: List<Long>,
        val raw: String,
    ) {
        var lastOutput: Long = 0
        var outputStr = ""
        val input = raw.toList().map { it.code.toLong() }.toMutableList()

        fun input(): Long =
            if (input.isNotEmpty()) {
                input.removeFirst()
            } else {
                Long.MAX_VALUE
            }

        fun output(out: Long) {
            lastOutput = out
            val char = out.toInt().toChar()
            outputStr += char
//            if (char == '\n') {
//                println(outputStr)
//                outputStr = ""
//            }
        }

        suspend fun runner(): Int? {
            ElfCode(code).runner(
                setup = ElfCode.memExpander(500),
                input = { input() },
                output = { output(it) },
            )
            return if (!outputStr.contains("you are ejected back to the checkpoint")) {
                // "Oh, hello! You should be able to get in by typing 35717128 on the keypad at the main airlock."
                outputStr.split("\n").takeLast(2).first().remove("\"Oh, hello! You should be able to get in by typing ", " on the keypad at the main airlock.\"").toInt()
            } else {
                null
            }
        }
    }
}