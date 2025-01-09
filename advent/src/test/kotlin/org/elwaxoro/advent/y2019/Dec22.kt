package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.remove

/**
 * Day 22: Slam Shuffle
 */
class Dec22 : PuzzleDayTester(22, 2019) {

    override fun part1(): Any = load().fold((0..10006).toList()) { deck, shuffle ->
        if (shuffle == "deal into new stack") {
            deck.stack()
        } else if (shuffle.startsWith("cut")) {
            deck.cut(shuffle.remove("cut ").toInt())
        } else if (shuffle.startsWith("deal with increment")) {
            deck.deal(shuffle.remove("deal with increment ").toInt())
        } else {
            throw IllegalStateException("I Dont' know what $shuffle is")
        }
    }.indexOf(2019)

    override fun part2(): Any = "no way am I working this out on my own, I used something from the subreddit just so I can move on with life"

    private fun List<Int>.stack(): List<Int> = reversed()

    private fun List<Int>.cut(n: Int): List<Int> =
        if (n > 0) {
            drop(n) + take(n)
        } else {
            takeLast(-1 * n) + dropLast(-1 * n)
        }

    private fun List<Int>.deal(n: Int): List<Int> {
        val l = toMutableList()
        val out = IntArray(size)
        var i = 0
        while (l.isNotEmpty()) {
            out[i] = l.removeFirst()
            i = (i + n) % size
        }
        return out.toList()
    }
}