package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 4: Security Through Obscurity
 */
class Dec04 : PuzzleDayTester(4, 2016) {

    private val az = ('a'..'z').toList()
    private val match = Regex("\\W*([a-z\\-]+)-(\\d+)\\[([a-z]+)]")

    override fun part1(): Any = loader().mapNotNull { (name, sectorId, checksum) ->
        val sortedGroupedName = name.replace("-", "").groupBy { it }.toList().sortedWith(compareBy({ -it.second.size }, { it.first }))
        val validLetters = checksum.filterIndexed { idx, c -> sortedGroupedName[idx].first == c }
        sectorId.takeIf { validLetters.length == checksum.length }
    }.sumOf { it.toInt() }

    override fun part2(): Any = loader().mapNotNull { (name, sectorId, _) ->
        sectorId.toInt().takeIf { id ->
            name.replace("-", "").map { c ->
                az[(az.indexOf(c) + id) % az.size]
            }.joinToString("").contains("northpole")
        }
    }.single()

    private fun loader() = load().map { match.find(it)!!.destructured.toList() }
}
