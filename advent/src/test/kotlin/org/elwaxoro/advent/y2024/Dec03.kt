package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.findAllNonStupid

/**
 * Day 3: Mull It Over
 */
class Dec03 : PuzzleDayTester(3, 2024) {

    override fun part1(): Any = load().fold(Acc()) { acc, input -> acc.also { """mul\(\d+,\d+\)""".findAllNonStupid(input).map { cmd -> acc.mul(cmd) } } }.sum

    override fun part2(): Any = load().fold(Acc()) { acc, input -> acc.also { """do\(\)|don't\(\)|mul\(\d+,\d+\)""".findAllNonStupid(input).map { cmd -> acc.process(cmd) } } }.sum

    private data class Acc(
        var sum: Long = 0L,
        var enabled: Boolean = true
    ) {
        fun process(cmd: String) =
            when (cmd) {
                "do()" -> enabled = true
                "don't()" -> enabled = false
                else -> mul(cmd)
            }

        fun mul(cmd: String) {
            if (enabled) {
                sum += """\d+""".findAllNonStupid(cmd).fold(1L) { a, b -> a * b.toInt() }
            }
        }
    }
}
