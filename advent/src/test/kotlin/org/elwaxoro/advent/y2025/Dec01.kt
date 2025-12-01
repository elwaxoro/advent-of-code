package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs

/**
 * Day 1: Secret Entrance
 */
class Dec01: PuzzleDayTester(1, 2025) {

    override fun part1(): Any = loader().let { input ->
        var d = 50
        input.map { i ->
            d = (d + i) % 100
            d
        }.count { it == 0 }
    }

    override fun part2(): Any = loader().let { input ->
        var d = 50
        var zeroes = 0
        input.map { i ->
            (1..abs(i)).forEach {
                if(i <0){
                    d--
                }else{
                    d++
                }
                d%=100
                if(d==0){
                    zeroes++
                }
            }
        }
        zeroes
    }

    private fun loader() = load().map {
        val d = it.drop(1).toInt()
        if (it.startsWith('L')) {
            d * -1
        } else {
            d
        }
    }
}