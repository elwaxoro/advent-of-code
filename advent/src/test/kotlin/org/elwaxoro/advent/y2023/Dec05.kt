package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.max
import kotlin.math.min

/**
 * If You Give A Seed A Fertilizer
 */
class Dec05: PuzzleDayTester(5, 2023) {

    /**
     * 227653707
     */
    override fun part1(): Any = loader().let { (seeds, almanac) ->
        seeds.minOf { seed ->
            var currentName = "seed"
            var currentNumber = seed
            while (currentName != "location") {
                almanac.single { it.source == currentName }.let { entry ->
                    entry.ranges.find { range ->
                        range.sourceStart <= currentNumber && (range.sourceStart + range.size + 1) >= currentNumber
                    }?.let { matchingRange ->
                        currentNumber = matchingRange.destinationStart + currentNumber - matchingRange.sourceStart
                    }
                    currentName = entry.destination
                }
            }
            currentNumber
        }
    } == 227653707L

    /**
     * 78775051
     */
    override fun part2(): Any = loader().let { (seedRaw, almanac) ->
        val seeds = seedRaw.chunked(2).map { (a, b) -> a to b }
        seeds.map { seedRange ->
            var currentName = "seed"
            var currentRanges = listOf(seedRange)
            while (currentName != "location") {
                almanac.single { it.source == currentName }.let { entry ->
                    // for each current range, partition into a list of ranges that overlap the entry's ranges, keeping non-overlapping ranges as-is
                    // ranges everywhere are pairs of starting number and size
                    var pendingRanges = currentRanges
                    val matchedRanges = mutableListOf<Pair<Long,Long>>()
                    entry.ranges.forEach { range ->
                        val rangeSourceStart = range.sourceStart
                        val rangeSourceEnd = range.sourceStart + range.size - 1
                        pendingRanges = pendingRanges.flatMap { (pendingStart, pendingSize) ->
                            val pendingEnd = pendingStart + pendingSize - 1
                            if (pendingEnd < rangeSourceStart || pendingStart >= rangeSourceEnd) {
                                // no overlap at all, leave the pending range alone
                                listOf(pendingStart to pendingSize)
                            } else {
                                // overlap! calculate it, along with any parts of the pending range that fall off the start or the end
                                val overlap = max(pendingStart, rangeSourceStart) to min(pendingEnd, rangeSourceEnd)
                                val overlapSize = overlap.second - overlap.first
                                val before = (pendingStart to overlap.first - pendingStart).takeIf { pendingStart < overlap.first }
                                val after = (overlap.second + 1 to pendingEnd - overlap.second).takeIf { pendingEnd > overlap.second }
                                val destinationOverlap = range.destinationStart + overlap.first - range.sourceStart to overlapSize
                                matchedRanges.add(destinationOverlap)
                                listOfNotNull(before, after) // only pass along portions of the range that don't overlap
                            }
                        }
                    }
                    currentRanges = matchedRanges.plus(pendingRanges)
                    currentName = entry.destination
                }
            }
            currentRanges.minBy { it.first }
        }.minOf { it.first }
    } == 78775051L

    private fun loader() = load(delimiter = "\n\n").let { input ->
        val seeds = input[0].replace("seeds: ", "").split(" ").map { it.toLong() }
        seeds to input.drop(1).map { AlmanacEntry.fromString(it) }
    }

    private data class AlmanacEntry(
        val source: String,
        val destination: String,
        val ranges: List<Range>
    ) {
        companion object {
            fun fromString(input: String): AlmanacEntry = input.split("\n").let { split ->
                val (source, destination) = split[0].replace(" map:", "").split("-to-")
                val ranges = split.drop(1).map { Range.fromString(it) }
                AlmanacEntry(source, destination, ranges)
            }
        }
    }

    private data class Range(
        val destinationStart: Long,
        val sourceStart: Long,
        val size: Long
    ) {
        companion object {
            fun fromString(input: String): Range = input.split(" ").map { it.toLong() }.let { (destination, source, length) ->
                Range(destination, source, length)
            }
        }
    }
}
