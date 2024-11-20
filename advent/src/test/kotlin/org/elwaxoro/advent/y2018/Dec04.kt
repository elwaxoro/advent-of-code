package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 4: Repose Record
 */
class Dec04 : PuzzleDayTester(4, 2018) {

    /**
     * part 1: organize each guard into map of every sleeping minute between 00-59 along with the number of times that minute was spent asleep across all shifts
     * Find the guard who spent the most total minutes asleep across all shifts, then get the most sleepy minute
     */
    override fun part1(): Any = loader().let { sleepMap ->
        val maxNapper = sleepMap.maxBy { guard -> guard.napMap.map { it.value }.sum() }
        val maxMinute = maxNapper.napMap.maxBy { it.value }
        maxNapper.id * maxMinute.key
    }

    /**
     * part 2 lucked out that this is just a variation of the same map operations as part 1
     * Find the guard with the most sleepy minute
     */
    override fun part2(): Any = loader().let { sleepMap ->
        val maxSleeper = sleepMap.maxBy { sleeper -> sleeper.napMap.maxOfOrNull { it.value } ?: 0 }
        val maxMinute = maxSleeper.napMap.maxBy { it.value }
        maxSleeper.id * maxMinute.key
    }

    private data class Guard(
        val id: Int = -9999,
        val napMap: MutableMap<Int, Int> = mutableMapOf(),
        var isAsleep: Boolean = false,
        var prevMinute: Int = 0
    ) {

        fun advance(isNowAsleep: Boolean, minute: Int) {
            if (isAsleep) {
                (prevMinute..<minute).forEach {
                    napMap[it] = napMap.getOrDefault(it, 0) + 1
                }
            }
            isAsleep = isNowAsleep
            prevMinute = minute
        }

        fun endShift() {
            if (isAsleep) {
                (prevMinute..<60).forEach {
                    napMap.getOrDefault(it, 0) + 1
                }
            }
        }
    }

    private fun loader(): Collection<Guard> = load().sorted().let { lines ->
        val sleepMap = mutableMapOf<Int, Guard>()
        var guard = Guard()
        lines.forEach { line ->
            val minute = line.substring(15, 17).toInt() // from the timestamp, ONLY minutes matter, toss the rest
            if (line.contains("Guard")) {
                guard.endShift() // end the previous guard shift
                val id = line.substring(19).replace(Regex("\\D+"), "").toInt()
                guard = sleepMap.getOrPut(id) { Guard(id) }
            } else {
                // advance the shift forward in time, each event is either waking up or falling asleep
                guard.advance(line.contains("falls asleep"), minute)
            }
        }
        guard.endShift()
        return sleepMap.values
    }
}
