package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import java.lang.IllegalStateException
import kotlin.math.pow

/**
 * Day 25: Full of Hot Air
 */
class Dec25 : PuzzleDayTester(25, 2022) {

    override fun part1(): Any = load().sumOf { raw ->
        raw.snafuToLong()
    }.toSnafu() == "2-=2==00-0==2=022=10"

    override fun part2(): Any = "NO PART 2! MERRY CHRISTMAS!"

    private fun String.snafuToLong(): Long = reversed().mapIndexed { i, c ->
        5.toDouble().pow(i.toDouble()).toLong() * when (c) {
            '2' -> 2L
            '1' -> 1L
            '0' -> 0L
            '-' -> -1L
            '=' -> -2L
            else -> throw IllegalStateException("UNKNOWN ELF SHIT HAPPENING IN HERE $c")
        }
    }.sum()

    private fun Long.toSnafu(): String =
        when (this) {
            0L -> "0"
            1L -> "1"
            else -> {
                var total = this
                var snafu = ""
                while (total > 1) {
                    val digit = total % 5
                    total /= 5
                    if (digit > 2) {
                        total++
                    }
                    snafu = "$digit$snafu"
                }
                if (total == 1L) {
                    snafu = "$total$snafu"
                }
                snafu.replace("4", "-").replace("3", "=")
            }
        }
}
