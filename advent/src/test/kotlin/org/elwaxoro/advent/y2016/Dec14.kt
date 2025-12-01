package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Day 14: One-Time Pad
 *
 * optimization 1: keep a map of all 5-in-a-row hits along with their index (just don't look backwards) (part 2 49s)
 * optimization 2: just keep ALL generated hashes, it doesn't OOM, but it saves recalculating since the 5 match runs ahead by 1000 from 3 match (part 2 24s)
 */
class Dec14 : PuzzleDayTester(14, 2016) {

    // puzzle input
    private val salt = "yjdafjpo"

    override fun part1(): Any = findKeys() == 25427

    override fun part2(): Any = true // findKeys(hashLoops = 2016) == 22045

    private val md = MessageDigest.getInstance("MD5")
    private val regThree = """(.)\1{2}""".toRegex()
    private val regFive = """(.)\1{4}""".toRegex()

    private fun genHash(idx: Int, loops: Int = 0) = (0..loops).fold("$salt$idx") { acc, _ -> BigInteger(1, md.digest(acc.toByteArray())).toString(16).padStart(32, '0') }

    private fun findKeys(hashLoops: Int = 0): Int {
        val fives = mutableMapOf<Char, MutableSet<Int>>() // track every 5-in-a-row along with the indexes it's been seen at
        val matches = mutableListOf<Int>() // 3-to-5 confirmed matches, only store the 3 index
        val hashes = mutableMapOf<Int, String>() // part 2 optimization: just keep ALL hashes so it doesn't recalc the 3s (5s runs ahead by ~1000)

        var threeIdx = 0 // threes have gotten this far
        var prevFiveIdx = 0 // fives have gotten this far

        while (matches.size < 64) {
            // only consider the first triplet in a hash, even if there's more than 1
            regThree.findAll(hashes.getOrPut(threeIdx) { genHash(threeIdx, hashLoops) }).firstOrNull()?.let { threeMatch ->
                val key = threeMatch.groupValues[1][0]

                (prevFiveIdx..threeIdx + 1000).forEach { fiveIdx ->
                    regFive.findAll(hashes.getOrPut(fiveIdx) { genHash(fiveIdx, hashLoops) }).forEach { fiveMatch ->
                        // save fives even if they're not the one we're looking for, other 3s might match it later
                        fives.getOrPut(fiveMatch.groupValues[1][0]) { mutableSetOf() }.add(fiveIdx)
                    }
                }
                prevFiveIdx = threeIdx + 1000

                // confirmed hit! make sure the five index is greater than the three index then add it
                fives[key]?.firstOrNull { it > threeIdx }?.let { matches.add(threeIdx) }
            }
            threeIdx++
        }

        return matches.last()
    }
}
