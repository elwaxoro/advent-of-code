package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import java.lang.IllegalStateException

/**
 * Day 2: Rock Paper Scissors
 */
class Dec02 : PuzzleDayTester(2, 2022) {

    /**
     * Parse to RPS on both sides, then play as last against first
     */
    override fun part1(): Any = load().map { it.split(" ").map { it.parseRPS() } }.sumOf { it.last().play(it.first()) }// == 10718

    /**
     * Parse first to RPS, leave last alone, play as whatever RPS is appropriate for the desired outcome
     */
    override fun part2(): Any = load().map {
        it.split(" ").let { it.first().parseRPS() to it.last() }
    }.sumOf { (them, us) ->
        when (us) {
            "X" -> them.wins().play(them) // we need to lose
            "Y" -> them.play(them) // we need to tie
            "Z" -> them.loses().play(them) // we need to win
            else -> throw IllegalStateException("I can't believe you've done this to $us")
        }
    }// == 14652

    /**
     * Why an enum? Not really sure :shrug:
     * It didn't buy me much
     */
    private enum class RPS(val baseScore: Int) {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        fun play(that: RPS): Int =
            when (that) {
                wins() -> 6 // this wins
                this -> 3 // this ties
                else -> 0 // this loses
            } + baseScore

        fun wins(): RPS =
            when (this) {
                ROCK -> SCISSORS
                PAPER -> ROCK
                SCISSORS -> PAPER
            }

        fun loses(): RPS =
            when (this) {
                ROCK -> PAPER
                PAPER -> SCISSORS
                SCISSORS -> ROCK
            }
    }

    private fun String.parseRPS(): RPS = when (this) {
        "A" -> RPS.ROCK
        "B" -> RPS.PAPER
        "C" -> RPS.SCISSORS
        "X" -> RPS.ROCK
        "Y" -> RPS.PAPER
        "Z" -> RPS.SCISSORS
        else -> throw IllegalStateException("Can't parse $this into RPS")
    }
}
