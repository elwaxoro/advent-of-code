package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import java.lang.IllegalStateException

/**
 * Day 21: Monkey Math
 */
class Dec21 : PuzzleDayTester(21, 2022) {

    /**
     * Root monkey shout. Computer monkey type that into AOC
     */
    override fun part1(): Any = loader().solveForRoot().doTheMonkeyMath() == 104272990112064

    /**
     * Narrow down the `humn` factor (lol) with a little binary search
     */
    override fun part2(): Any = loader().toMutableMap().let { initialMap ->
        // 3220993874133 OR 3220993874134
        var upper = 100000000000000000L
        var lower = 0L
        var guess = upper/2
        while(true) {
            initialMap["humn"] = "$guess"
            val (left, _, right) = initialMap.solveForRoot().split(" ")
            val diff = left.toLong() - right.toLong()
            if(left == right) {
                return guess
            } else if(diff > 0) {
                // guess is too low
                lower = guess
            } else {
                // guess is too high
                upper = guess
            }
            guess = ((upper-lower)/2) + lower
        }
    }

    private fun Map<String, String>.solveForRoot(): String = toMutableMap().let { unsolved ->
        val solvedMap = mutableMapOf<String, Long>()
        val digitRegex = "-*\\d+".toRegex()
        val root = unsolved.remove("root")!!
        val (rootL, rootO, rootR) = root.split(" ")
        while (!solvedMap.containsKey(rootL) || !solvedMap.containsKey(rootR)) {
            val iter = unsolved.entries.iterator()
            while (iter.hasNext()) {
                val (variable, equation) = iter.next()
                if (equation.matches(digitRegex)) {
                    solvedMap[variable] = equation.toLong()
                    iter.remove()
                } else {
                    val (left, operator, right) = equation.split(" ")
                    if (left.matches(digitRegex) && right.matches(digitRegex)) {
                        solvedMap[variable] = equation.doTheMonkeyMath()
                        iter.remove()
                    } else {
                        val leftFix = "${solvedMap[left] ?: left}"
                        val rightFix = "${solvedMap[right] ?: right}"
                        unsolved[variable] = "$leftFix $operator $rightFix"
                    }
                }
            }
        }
        "${solvedMap[rootL]} $rootO ${solvedMap[rootR]}"
    }

    private fun String.doTheMonkeyMath() = split(" ").let { (left, operator, right) ->
        when (operator) {
            "+" -> left.toLong() + right.toLong()
            "-" -> left.toLong() - right.toLong()
            "*" -> left.toLong() * right.toLong()
            "/" -> left.toLong() / right.toLong()
            else -> throw IllegalStateException("YUNO MATH?? $left $operator $right")
        }
    }

    private fun loader(): Map<String, String> = load().map { it.split(": ") }.associate { it[0] to it[1] }
}
