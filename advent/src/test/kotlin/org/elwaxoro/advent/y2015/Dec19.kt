package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.takeSplit

/**
 * Day 19: Medicine for Rudolph
 */
class Dec19 : PuzzleDayTester(19, 2015) {

    override fun part1(): Any = loader().let { (choices, input) ->
        choices.flatMap { (a, b) ->
            input.mapIndexedNotNull { i, _ ->
                if (input.substring(i).startsWith(a)) {
                    input.replacer(a, b, i)
                } else {
                    null
                }
            }
        }.distinct().size
    }

    /**
     * There's a reduction that always gets stuck:
     * CRnSiRnFYCaRnFArArFArAl
     * Just keep trying random replacements until it succeeds!
     */
    override fun part2(): Any = loader().let { (choices, input) ->
        var replacements: Int
        do {
            replacements = randomReplacer(choices, input)
        } while (replacements < 0)
        replacements
    }

    private fun String.replacer(a: String, b: String, i: Int): String =
        if (i < 0) {
            this
        } else {
            val (x, y) = takeSplit(i)
            x + y.replaceFirst(a, b)
        }

    private fun randomReplacer(choices: List<List<String>>, input: String): Int {
        var working = input
        var replacements = 0
        while (working != "e") {
            val last = replacements
            choices.shuffled().forEach { (a, b) ->
                if (working.contains(b)) {
                    replacements++
                    working = working.replacer(b, a, working.lastIndexOf(b))
                }
            }
            if (last == replacements) {
                println("Got stuck: $working")
                return -1
            }
        }
        return replacements
    }

    private fun loader() = load(delimiter = "\n\n").let { (r, i) ->
        r.split("\n").map { it.split(" => ") } to i
    }
}