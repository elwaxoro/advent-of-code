package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

class Dec09 : PuzzleDayTester(9, 2018) {

    override fun part1(): Any = loader().let { (players, lastCount) -> playGame(players, lastCount) }

    override fun part2(): Any = loader().let { (players, lastCount) -> playGame(players, lastCount * 100) }

    private fun playGame(playerCount: Int, lastScore: Int): Long {
        var marble = Marble().initFirst()
        val players = (1..playerCount).map { 0L }.toMutableList()
        (1..lastScore).forEach { count ->
            if (count % 23 == 0) {
                val remove = marble.rotate(-7)
                marble = remove.pop()
                players[count % players.size] += count + remove.score
            } else {
                marble = marble.rotate(1).put(Marble(count.toLong()))
            }
        }
        return players.max()
    }

    private fun loader() = load().single().replace(" players; last marble is worth ", " ").replace(" points", "").split(" ").map { it.toInt() }

    private data class Marble(
        val score: Long = 0,
        var left: Marble? = null,
        var right: Marble? = null,
    ) {

        fun initFirst(): Marble {
            left = this
            right = this
            return this
        }

        /**
         * imagine the "current" marble as top-center
         * negative rotation = counter-clockwise / left
         * positive rotation = clockwise / right
         */
        fun rotate(distance: Int): Marble =
            if (distance == 0) {
                this
            } else if (distance < 0) {
                left!!.rotate(distance + 1)
            } else {
                right!!.rotate(distance - 1)
            }

        /**
         * add new marble to the right and return it
         */
        fun put(add: Marble): Marble = add.also {
            right!!.left = add
            add.right = right
            add.left = this
            right = add
        }

        /**
         * remove self, return marble to the right
         */
        fun pop(): Marble = right!!.also {
            right!!.left = left
            left!!.right = right
        }
    }
}
