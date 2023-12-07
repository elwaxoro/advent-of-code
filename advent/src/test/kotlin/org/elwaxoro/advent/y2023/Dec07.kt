package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Camel Cards
 *
 * Idea:
 * 1. convert cards to be A-Z based on rank (A=A,K=B,Q=C...)
 * 2. score the hand and assign a letter (5 of a kind = A, 4 of a kind = B...)
 * 3. store scored hand as a string (score+converted hand)
 * 4. simple string sort to get the order of the hands!
 */
class Dec07 : PuzzleDayTester(7, 2023) {

    /**
     * 256448566
     */
    override fun part1(): Any = cardReader().map { (hand, bid) ->
        hand.alphabetize().scoreHand() to bid
    }.gatherWinnings()

    /**
     * 254412181
     * Solve the same way as part 1, except convert all the jacks to be the same rank as the biggest set
     * If multiple sets have the same size, just pick one it doesn't matter which one, because tie-breaking only cares about the order of cards in the hand
     */
    override fun part2(): Any = cardReader().map { (hand, bid) ->
        val converted = hand.alphabetize(jokerMode = true)
        val rank = if (converted.contains('X')) {
            // find the best group to swap the jokers into
            converted.replace('X', converted.groupBy { it }.maxBy { set -> set.value.size.takeUnless { set.key == 'X' } ?: 0 }.key)
        } else {
            converted
        }.scoreHand()[0]
        "$rank$converted" to bid
    }.gatherWinnings()

    private fun List<Pair<String, String>>.gatherWinnings() = sortedByDescending { it.first }.mapIndexed { index, (_, bid) -> (index+1) * bid.toLong() }.sum()

    private fun String.scoreHand(): String = groupBy { it }.let { sets ->
        val max = sets.maxOf { it.value.size }
        when (sets.size) {
            1 -> 'A' // 5 of a kind!
            2 -> {
                when (max) {
                    4 -> 'B' // 4 of a kind
                    3 -> 'C' // full house
                    else -> 'Z'
                }
            }
            3 -> {
                when (max) {
                    3 -> 'D' // 3 of a kind
                    2 -> 'E' // 2 pair
                    else -> 'Z'
                }
            }
            4 -> 'F' // 1 pair
            5 -> 'G' // high card
            else -> 'Z'
        } + this
    }

    private fun String.alphabetize(jokerMode: Boolean = false): String = this.map {
        when (it) {
            'A' -> 'A'
            'K' -> 'B'
            'Q' -> 'C'
            'J' -> 'D'.takeUnless { jokerMode } ?: 'X'
            'T' -> 'E'
            '9' -> 'F'
            '8' -> 'G'
            '7' -> 'H'
            '6' -> 'I'
            '5' -> 'J'
            '4' -> 'K'
            '3' -> 'L'
            '2' -> 'M'
            '1' -> 'N'
            else -> 'Z'
        }
    }.joinToString("")

    private fun cardReader() = load().map { it.split(" ") }
}
