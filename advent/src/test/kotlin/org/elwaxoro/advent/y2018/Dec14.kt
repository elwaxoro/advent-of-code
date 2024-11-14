package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 14: Chocolate Charts
 */
class Dec14 : PuzzleDayTester(14, 2018) {

    /**
     * String concat works pretty good here, takes 2s
     */
    override fun part1(): Any {
        var scores = "37"
        var elf1 = 0
        var elf2 = 1
        val target = 323081
        var tenScore = ""
        while (tenScore.length < 10) {
            val elf1Score = scores[elf1].digitToInt()
            val elf2Score = scores[elf2].digitToInt()
            scores += "${elf1Score + elf2Score}"
            elf1 = (elf1 + elf1Score + 1) % scores.length
            elf2 = (elf2 + elf2Score + 1) % scores.length
            tenScore = scores.drop(target).take(10)
        }
        return tenScore
    }

    /**
     * string concatenation approach was taking forever, mutable list took 7s so yay I guess
     */
    override fun part2(): Any {
        val scores = mutableListOf(3, 7)
        var elf1 = 0
        var elf2 = 1
        val target = "323081"
        while (!scores.takeLast(10).joinToString("").contains(target)) {
            val elf1Score = scores[elf1]
            val elf2Score = scores[elf2]
            scores.addAll((elf1Score + elf2Score).toString().map { it.digitToInt() })
            elf1 = (elf1 + elf1Score + 1) % scores.size
            elf2 = (elf2 + elf2Score + 1) % scores.size
        }
        return scores.joinToString("").indexOf(target)
    }
}
