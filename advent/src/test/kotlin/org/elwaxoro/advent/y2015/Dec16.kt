package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 16: Aunt Sue
 */
class Dec16: PuzzleDayTester(16, 2015) {

    override fun part1(): Any = loader().single { (_, traits) ->
        traits.all { (trait, count) ->
            analysis.containsKey(trait) && analysis[trait] == count
        }
    }.first

    override fun part2(): Any = loader().single { (_, traits) ->
        traits.all { (trait, count) ->
            analysis.containsKey(trait) &&
            when (trait) {
                "cats" -> analysis[trait]!! < count
                "trees" -> analysis[trait]!! < count
                "pomeranians" -> analysis[trait]!! > count
                "goldfish" -> analysis[trait]!! > count
                else -> analysis[trait] == count
            }
        }
    }.first

    private fun loader() = load().map { line ->
        val sue = line.substringBefore(':')
        sue to line.substringAfter(':').split(",").map { item ->
            val (name, quantity) = item.split(":")
            name.trim() to quantity.trim().toInt()
        }.toMap()
    }

    private val analysis = """
        children: 3
        cats: 7
        samoyeds: 2
        pomeranians: 3
        akitas: 0
        vizslas: 0
        goldfish: 5
        trees: 3
        cars: 2
        perfumes: 1
    """.trimIndent().split("\n").map {
        val (name, quantity) = it.split(": ")
        name to quantity.toInt()
    }.toMap()
}