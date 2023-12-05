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
            var sourceName = "seed"
            var sourceIdx = seed
            while (sourceName != "location") {
                val entry = almanac.single { it.source == sourceName }
                val range = entry.ranges.find { range ->
                    range.sourceStart <= sourceIdx && (range.sourceStart + range.length + 1) >= sourceIdx
                }
                sourceName = entry.destination
                if (range != null) {
                    val offset = sourceIdx - range.sourceStart
                    val destIdx = range.destinationStart + offset
                    sourceIdx = destIdx
                }
            }
            sourceIdx
        }
    } == 227653707L

    /**
     * 78775051
     */
    override fun part2(): Any = loader().let { (seedRaw, almanac) ->
        val seeds = seedRaw.chunked(2).map { (a, b) -> a to b }
        seeds.map { seedRange ->
            var sourceName = "seed"
            var sourceRanges = listOf(seedRange)
            while (sourceName != "location") {
                val entry = almanac.single { it.source == sourceName }
                // for each source range, partition into a list of ranges that overlap the entry's ranges, keeping non-overlapping ranges as-is
                var pendingRanges = sourceRanges
                val matchedRanges = mutableListOf<Pair<Long,Long>>()
                entry.ranges.forEach { range ->
                    val s2 = range.sourceStart
                    val e2 = range.sourceStart + range.length - 1
                    pendingRanges = pendingRanges.flatMap { (s1, size) ->
                        val e1 = s1 + size - 1
                        if (e1 < s2 || s1 >= e2) {
                            listOf(s1 to size) // no overlap at all
                        } else {
                            val overlap = max(s1, s2) to min(e1, e2)
                            val overlapSize = overlap.second - overlap.first
                            val before = (s1 to overlap.first - s1).takeIf { s1 < overlap.first }
                            val after = (overlap.second + 1 to e1 - overlap.second).takeIf { e1 > overlap.second }
                            val destOverlap = range.destinationStart + overlap.first - range.sourceStart to overlapSize
                            matchedRanges.add(destOverlap)
                            listOfNotNull(before, after)
                        }
                    }
                }
                sourceRanges = matchedRanges.plus(pendingRanges)
                sourceName = entry.destination
            }
            sourceRanges.minBy { it.first }
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
        val length: Long
    ) {
        companion object {
            fun fromString(input: String): Range = input.split(" ").map { it.toLong() }.let { (destination, source, length) ->
                Range(destination, source, length)
            }
        }
    }
}
