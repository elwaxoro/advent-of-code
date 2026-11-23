package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 13: Packet Scanners
 *
 * need to step thru a series of rotating scanners
 * clock tick = idx
 * caught idea: position at time % some multiple of the layer's range gives a collision, everything else is fine
 */
class Dec13 : PuzzleDayTester(13, 2017) {

    /**
     * can't be sneaky, gotta blast through taking every penalty
     * when caught, severity = depth * layer range
     * packet reaches depth at time == depth
     */
    override fun part1(): Any = loader().sumOf { (depth, range) ->
        if (caught(depth, range)) {
            depth * range
        } else {
            0
        }
    }

    /**
     * smallest delay where no scanner is at 0 on arrival
     * depth 0 has severity 0, but delay % cycle == 0 is still a catch
     */
    override fun part2(): Any = loader().let { network ->
        var delay = 0
        while (network.any { (depth, range) -> caught(depth, range, delay) }) {
            delay++
        }
        delay
    }

    private fun caught(depth: Int, range: Int, delay: Int = 0): Boolean = (depth + delay) % ((range - 1) * 2) == 0

    private fun loader() = load().map { it.split(": ").map(String::toInt) }
}
