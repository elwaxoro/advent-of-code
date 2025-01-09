package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester

class Dec21 : PuzzleDayTester(21, 2019) {

    /**
     * Droid notes from examples
     * droid jump covers 4 tiles (landing on 5th)
     * ABCD refers to the 4 tiles directly in front of the droid at every step
     * From the sample, NOT D J means "if D is not ground, jump"
     * This causes the droid to jump into the hole, 4 tiles away from where the jump starts
     *
     * NOT C J <--- is C is a hole?
     * AND D J <--- C is a hole AND D is solid
     * NOT A T <--- there is a hole right in front of us!
     * OR T J <--- if either one got set: JUMP
     */
    override fun part1(): Any = runBlocking {
        SpringDroid(loadToLong(delimiter = ",")).runner(
            """
            NOT C J
            AND D J 
            NOT A T 
            OR T J
            WALK

        """.trimIndent().toMutableList()
        )
    } == 19355645L

    /**
     * RUN don't WALK!
     * now have access to tiles EFGHI
     * Rules are similar, but we can look farther ahead and avoid falling into stupid spots
     *
     * NOT C J <--- is C a hole?
     * AND D J <--- C is a hole AND D is solid
     * AND H J <--- H is also solid (4 away from landing on D) this is a safe 2 jump chain
     * NOT B T <--- is B a hole?
     * AND D T <--- B is a hole AND D is solid
     * OR T J <--- if either is set to jump, JUMP
     * NOT A T <--- jump or die, it's right in front of us
     * OR T J <--- last check, JUMP
     * RUN <--- FAST MODE GO
     */
    override fun part2(): Any = runBlocking {
        SpringDroid(loadToLong(delimiter = ",")).runner(
            """
            NOT C J 
            AND D J 
            AND H J
            NOT B T 
            AND D T 
            OR T J
            NOT A T 
            OR T J
            RUN

        """.trimIndent().toMutableList()
        )
    } == 1137899149L

    private class SpringDroid(
        val code: List<Long>
    ) {

        var lastOutput: Long = 0
        var outputStr = ""

        val input = """
            NOT C J 
            AND D J 
            NOT A T 
            OR T J
            WALK

        """.trimIndent().toList().map { it.code }.toMutableList()

        fun output(out: Long) {
            lastOutput = out
            val char = out.toInt().toChar()
            outputStr += char
            if (char == '\n') {
                println(outputStr)
                outputStr = ""
            }
        }

        fun input(): Long =
            if (input.isNotEmpty()) {
                input.removeFirst().toLong()
            } else {
                0L
            }

        suspend fun runner(input: MutableList<Char>) =
            ElfCode(code).runner(
                setup = ElfCode.memExpander(5000),
                input = { input.removeFirst().code.toLong() },
                output = { output(it) },
            ).let {
                lastOutput
            }
    }
}