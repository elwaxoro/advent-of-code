package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.padTo

/**
 * Day 10: Cathode-Ray Tube
 */
class Dec10 : PuzzleDayTester(10, 2022) {

    data class CPU(val instructions: List<String>, val bullshit: (x: Int, cycleCount: Int) -> Unit) {
        
    }

    /**
     * "During" means the value from the previous idx
     */
    override fun part1(): Any = load().let { instructions ->
        var x = 1
        val importantCycles = mutableListOf<Int>()
        val importantCounts = listOf(20, 60, 100, 140, 180, 220)
        var cycleCount = 1
        instructions.forEach { s ->

            if (cycleCount in importantCounts) {
                importantCycles.add(x * cycleCount)
            }
            cycleCount++
            if (s != "noop") {
                if (cycleCount in importantCounts) {
                    importantCycles.add(x * cycleCount)
                }
                cycleCount++ // plus costs 2 because rasins
                x += s.split(" ").last().toInt() // set the x
            }
        }
        println(importantCycles)
        importantCycles.sum()
    } == 13860

    override fun part2(): Any = load().let { instructions ->
        var x = 1 // sprite starts out 0,1,2
        val crt = (0..5).map { listOf('.').padTo(40).toMutableList() }
        var cycleCount = 0
        instructions.forEach { s ->
            // begin: draw a sprite maybe
            var crtPos = cycleCount % 40
            crt[cycleCount / 40][crtPos] = if (crtPos in (x - 1..x + 1)) {
                '#'
            } else {
                ' '
            }
            cycleCount++
            if (s != "noop") {
                crtPos = cycleCount % 40
                crt[cycleCount / 40][crtPos] = if (crtPos in (x - 1..x + 1)) {
                    '#'
                } else {
                    ' '
                }
                cycleCount++ // plus costs 2 because rasins
                x += s.split(" ").last().toInt() // set the x
            }
        }
        "\n\n${crt.joinToString("\n") { it.joinToString("") }}"
    }
}