package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.max

/**
 * Day 4: Scratchcards
 */
class Dec04 : PuzzleDayTester(4, 2023) {

    /**
     * 23750
     */
    override fun part1(): Any = loader().map { card ->
        card.actual.fold(0) { acc, number ->
            // first win is set to 1 with max(0, 1), next wins are acc * 2
            max(acc * 2, 1).takeIf { card.winning.contains(number) } ?: acc
        }
    }.sum()

    /**
     * 13261850
     */
    override fun part2(): Any = loader().map { card ->
        // swap to Pair<card number, win count for the card>
        card.number to card.actual.filter { card.winning.contains(it) }.size
    }.let { cards ->
        val finalCount: MutableMap<Long, Long> = mutableMapOf()
        // loop backwards, storing each card's final win count as you go
        // manual inspection of the input file shows the last few cards count 0 wins so index out of bounds is not an issue
        cards.reversed().forEach { (name, wins) ->
            // for each win, increase idx by 1 and see what THAT card's finalCount got, then add everything up!
            finalCount[name] = 0.until(wins).sumOf { idx ->
                finalCount[name + idx + 1]!!
            } + 1 // all cards count as at least 1, even if they won no extra cards
        }
        finalCount.values.sum()
    }

    private fun loader(): List<Card> = load().map { card ->
        val (name, tmp) = card.split(":")
        val (w, a) = tmp.split(" | ").map { it.trim().split("\\W+".toRegex()).map { it.toInt() } }
        Card(name.replace("Card", "").trim().toLong(), w, a)
    }

    private data class Card(
        val number: Long,
        val winning: List<Int>,
        val actual: List<Int>
    )
}
