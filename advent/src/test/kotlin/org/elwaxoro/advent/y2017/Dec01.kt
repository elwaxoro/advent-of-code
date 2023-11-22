package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.splitToInt

/**
 * Day 1: Inverse Captcha
 */
class Dec01: PuzzleDayTester(1, 2017)  {

    /**
     * 1393
     */
    override fun part1(): Any {
        val list = load().single().splitToInt()
        return list.plus(list.first()).zipWithNext { a, b -> a.takeIf { a == b } ?: 0 }.sum()
    }

    /**
     * 1292
     */
    override fun part2(): Any {
        val list = load().single().splitToInt()
        val half = list.size / 2
        return list.mapIndexed { index, i -> i.takeIf { i == list[(half + index) % list.size] } ?: 0 }.sum()
    }
}
