package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.reverseSpan
import java.util.*

/**
 * Day 21: Scrambled Letters and Hash
 */
class Dec21 : PuzzleDayTester(21, 2016) {

    override fun part1(): Any = load().let { commands ->
        val input = "abcdefgh".toMutableList()
        commands.forEach { cmd ->
            if (cmd.startsWith("swap position")) {
                Collections.swap(input, cmd[14].digitToInt(), cmd.last().digitToInt())
            } else if (cmd.startsWith("swap letter")) {
                Collections.swap(input, input.indexOf(cmd[12]), input.indexOf(cmd.last()))
            } else if (cmd.startsWith("rotate based")) {
                val a = input.indexOf(cmd.last())
                if (a >= 4) {
                    Collections.rotate(input, a + 2)
                } else {
                    Collections.rotate(input, a + 1)
                }
            } else if (cmd.startsWith("rotate left")) {
                Collections.rotate(input, cmd[12].digitToInt() * -1)
            } else if (cmd.startsWith("rotate right")) {
                Collections.rotate(input, cmd[13].digitToInt())
            } else if (cmd.startsWith("reverse positions")) {
                input.reverseSpan(cmd[18].digitToInt(), cmd.last().digitToInt())
            } else if (cmd.startsWith("move position")) {
                val c = input.removeAt(cmd[14].digitToInt())
                input.add(cmd.last().digitToInt(), c)
            }
        }
        return input.joinToString("") == "aefgbcdh"
    }

    override fun part2(): Any = load().reversed().let { commands ->
        val lookup = listOf(7, 7, 2, 6, 1, 5, 0, 4)
        val input = "fbgdceah".toMutableList()

        commands.forEach { cmd ->
            if (cmd.startsWith("swap position")) {
                // same as part 1
                Collections.swap(input, cmd[14].digitToInt(), cmd.last().digitToInt())
            } else if (cmd.startsWith("swap letter")) {
                // same as part 1
                Collections.swap(input, input.indexOf(cmd[12]), input.indexOf(cmd.last()))
            } else if (cmd.startsWith("rotate based")) {
                // hand-crafted lookup list, rotate based on that
                Collections.rotate(input, lookup[input.indexOf(cmd.last())])
            } else if (cmd.startsWith("rotate left")) {
                // rotate right instead
                Collections.rotate(input, cmd[12].digitToInt())
            } else if (cmd.startsWith("rotate right")) {
                // rotate left instead
                Collections.rotate(input, cmd[13].digitToInt() * -1)
            } else if (cmd.startsWith("reverse positions")) {
                // same as part 1
                input.reverseSpan(cmd[18].digitToInt(), cmd.last().digitToInt())
            } else if (cmd.startsWith("move position")) {
                // swap in the opposite direction
                val c = input.removeAt(cmd.last().digitToInt())
                input.add(cmd[14].digitToInt(), c)
            }
        }
        return input.joinToString("") == "egcdahbf"
    }

    /**
     * Playground to figure out how to reverse the "rotate based on position of x"
     */
    private fun testerThing() {
        val lookup = listOf(7, 7, 2, 6, 1, 5, 0, 4)
        val input = "-------a".toMutableList()
        println(input.joinToString(""))
        val a = input.indexOf('a')
        if (a >= 4) {
            Collections.rotate(input, a + 2)
        } else {
            Collections.rotate(input, a + 1)
        }
        println(input.joinToString(""))
        val newA = input.indexOf('a')
        Collections.rotate(input, lookup[newA])
        println(input.joinToString(""))
    }
}
