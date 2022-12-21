package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import java.lang.IllegalStateException

/**
 * Day 21: Monkey Math
 */
class Dec21 : PuzzleDayTester(21, 2022) {

    override fun part1(): Any = loader().solveForRoot().doMathShit() == 104272990112064

    override fun part2(): Any = loader().toMutableMap().let { initialMap ->
        var guess = 3220993873000L
        while(true) {
            initialMap["humn"] = "$guess"
            val (left, _, right) = initialMap.solveForRoot().split(" ")
            if(guess % 1000 == 0L) {
                println("guess: $guess yeilds $left == $right? ${left == right} diff ${left.toLong() - right.toLong()}")
            }
            if(left == right) {
                return guess
            }
            guess++
        }
    }

    private fun Map<String, String>.solveForRoot(): String = toMutableMap().let { unsolved ->
        val solvedMap = mutableMapOf<String, Long>()
        val digitRegex = "-*\\d+".toRegex()
        val root = unsolved.remove("root")!!
        val (rootL, rootO, rootR) = root.split(" ")
        var counter =0
        while (!solvedMap.containsKey(rootL) || !solvedMap.containsKey(rootR)) {
            counter++
            val iter = unsolved.entries.iterator()
            while (iter.hasNext()) {
                val (variable, equation) = iter.next()
                if (equation.matches(digitRegex)) {
                    solvedMap[variable] = equation.toLong()
                    iter.remove()
                } else {
                    val (left, operator, right) = equation.split(" ")
                    if (left.matches(digitRegex) && right.matches(digitRegex)) {
                        solvedMap[variable] = equation.doMathShit()
                        iter.remove()
                    } else {
                        val leftFix = "${solvedMap[left] ?: left}"
                        val rightFix = "${solvedMap[right] ?: right}"
                        unsolved[variable] = "$leftFix $operator $rightFix"
                    }
                }
            }
            if(counter > 1000) {
                println("giving up something is fucky")
                println(solvedMap)
                println(unsolved)
                return "a + b"
            }
        }
        "${solvedMap[rootL]} $rootO ${solvedMap[rootR]}"
    }

    private fun String.doMathShit() = split(" ").let { (left, operator, right) ->
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
