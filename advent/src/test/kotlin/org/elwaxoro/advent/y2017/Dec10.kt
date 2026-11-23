package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 10: Knot Hash
 */
class Dec10 : PuzzleDayTester(10, 2017) {

    companion object {

        private val startingList = (0..255).toList()
        private val input = "197,97,204,108,1,29,5,71,0,50,2,255,248,78,254,63"
        private val lengths = input.split(",").map { it.toInt() }
        private val suffix = "17, 31, 73, 47, 23".split(", ").map { it.toInt() }

        /**
         * Part 2 reworked to be reusable for Dec14
         */
        fun knotHash(input: String): String {
            val lengths = input.map { it.code }.plus(suffix)
            val list = (0..255).toMutableList()
            (0..<64).fold(0 to 0) { (idx, skip), _ ->
                list.knotHash(idx, skip, lengths)
            }
            return list.chunked(16).joinToString("") { chunk ->
                chunk.reduce { acc, i -> acc xor i }.toString(16).padStart(2, '0')
            }
        }

        fun MutableList<Int>.knotHash(startIdx: Int = 0, startSkip: Int = 0, lengths: List<Int>): Pair<Int, Int> {
            var idx = startIdx
            var skip = startSkip
            lengths.forEach { length ->
                val toReverse = mutableListOf<Int>()
                (idx..<idx + length).forEach { fetch ->
                    toReverse.add(this[fetch % this.size])
                }
                toReverse.reversed().forEachIndexed { set, value ->
                    this[(idx + set) % this.size] = value
                }

                idx += length + skip
                skip++
            }
            return idx to skip
        }
    }

    override fun part1(): Any {
        val list = startingList.toMutableList()
        list.knotHash(0, 0, lengths)
        return list[0] * list[1]
    }

    override fun part2(): Any = knotHash(input)

}
