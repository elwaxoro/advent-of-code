package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.padTo

/**
 * Day 10: Cathode-Ray Tube
 */
class Dec10 : PuzzleDayTester(10, 2022) {

    /**
     * Capture x * cycleCount on really important cycles only
     */
    override fun part1(): Any {
        val importantCycles = mutableListOf<Int>()
        val importantCounts = listOf(20, 60, 100, 140, 180, 220)
        load().goCPUgo { x, cycleCount ->
            if (cycleCount in importantCounts) {
                importantCycles.add(x * cycleCount)
            }
        }
        return importantCycles.sum()// == 13860
    }

    /**
     * Time to create a replacement screen!
     * ###  #### #  # ####  ##    ##  ##  ###
     * #  #    # #  # #    #  #    # #  # #  #
     * #  #   #  #### ###  #       # #    ###
     * ###   #   #  # #    # ##    # #    #  #
     * # #  #    #  # #    #  # #  # #  # #  #
     * #  # #### #  # #     ###  ##   ##  ###
     */
    override fun part2(): Any {
        val crt = (0..5).map { listOf(' ').padTo(40).toMutableList() }
        load().goCPUgo { x, cycleCount ->
            val crtRow = (cycleCount - 1) / 40
            val crtPos = (cycleCount - 1) % 40
            crt[crtRow][crtPos] = '#'.takeIf { crtPos in (x - 1..x + 1) }  ?: ' '
        }
        return "\n\n${crt.joinToString("\n") { it.joinToString("") }}"
    }

    private fun List<String>.goCPUgo(doSomeBullshit: (x: Int, cycleCount: Int) -> Unit) {
        var x = 1
        var cycleCount = 1
        forEach { s ->
            doSomeBullshit(x, cycleCount)
            cycleCount++
            if (s != "noop") {
                doSomeBullshit(x, cycleCount)
                cycleCount++ // addx costs 2 because rasins
                x += s.split(" ").last().toInt()
            }
        }
    }
}