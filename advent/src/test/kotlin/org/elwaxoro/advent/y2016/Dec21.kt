package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import java.util.*

/**
 * Day 21: Scrambled Letters and Hash
 * part 1: aefgbcdh
 */
class Dec21 : PuzzleDayTester(21, 2016) {

    override fun part1(): Any = load().let { commands ->
        val input = "abcdefgh".toMutableList()

        commands.forEach { command ->
            if (command.startsWith("swap position")) {
                "swap position 6 with position 4"
                val a = command[14].digitToInt()
                val b = command.last().digitToInt()
                Collections.swap(input, a, b)
            } else if (command.startsWith("swap letter")) {
                "swap letter g with letter d"
                val a = command[12]
                val b = command.last()
                Collections.swap(input, input.indexOf(a), input.indexOf(b))
            } else if (command.startsWith("rotate based")) {
                "rotate based on position of letter e"
                val a = input.indexOf(command.last())
                if (a >= 4) {
                    Collections.rotate(input, a + 2)
                } else {
                    Collections.rotate(input, a + 1)
                }
            } else if (command.startsWith("rotate left")) {
                "rotate left 2 steps"
                val a = command[12].digitToInt()
                Collections.rotate(input, a * -1)
            } else if (command.startsWith("rotate right")) {
                "rotate right 2 steps"
                val a = command[13].digitToInt()
                Collections.rotate(input, a)
            } else if (command.startsWith("reverse positions")) {
                "reverse positions 0 through 7"
                val a = command[18].digitToInt()
                val b = command.last().digitToInt()
                input.reverseSpan(a, b)
            } else if (command.startsWith("move position")) {
                "move position 2 to position 7"
                // move position X to position Y means that the letter which is at index X should be removed from the string, then inserted such that it ends up at index Y.
                val a = command[14].digitToInt()
                val b = command.last().digitToInt()
                val c = input.removeAt(a)
                input.add(b, c)
            }
        }
        return input.joinToString("")
    }

    override fun part2(): Any {
        return "soon"
    }

    private fun <T> MutableList<T>.reverseSpan(a: Int, b: Int) {
        val i = (a..a + (b - a) / 2)
        val j = if (b - a % 2 == 0) {
            (b downTo a + (b - a) / 2)
        } else {
            (b downTo 1 + a + (b - a) / 2)
        }
        i.zip(j).forEach { (x, y) ->
            Collections.swap(this, x, y)
        }
    }

    private fun loader() = load()
}