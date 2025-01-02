package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

/**
 * Day 17: Set and Forget
 * Aft Scaffolding Control and Information Interface (ASCII LOL)
 * camera outputs:
 * 35 = # (scaffold)
 * 46 = . (empty)
 * 10 = newline
 * draw left to right
 */
class Dec17: PuzzleDayTester(17, 2019) {

    override fun part1(): Any = runBlocking {
        val ascii = Ascii(loadToLong(delimiter = ","))
        ascii.viewer()
        val intersections = ascii.coords.filter {
            it.neighbors().all { n -> ascii.coords.contains(n) }
        }
        println(ascii.coords.plus(ascii.robot).printify(empty = ' '))
        intersections.sumOf { it.x * it.y }
    } == 8408

    /**
     * awaken robot by setting code[0] = 2
     * input is a line of ascii codes, then a 10 (newline)
     * input 1: main movement routine (calls move functions A, B, C) as a list separated by comma (ascii 44)
     * input 2: movement function A
     * input 3: movement function B
     * input 4: movement function C
     * input 5: y or n to output lots of coord updates
     * movement function: turn L or R, or move forward X units
     * main + ABC can only be 20 chars each + \n
     * last output: space dust collected
     */
    override fun part2(): Any {
        return super.part2()
    }

    private class Ascii(
        val code: List<Long>,
        val coords: MutableSet<Coord> = mutableSetOf(),
        var x: Int = 0,
        var y: Int = 0,
        var robot: Coord = Coord(0, 0),
        var heading: Dir = Dir.N,
        var isDead: Boolean = false
    ) {

        fun output(out: Long) {
            val c = Coord(x, y)
            when (out) {
                35L -> coords.add(c)
                //46L -> coords[Coord(x, y)] = '.'
                46L -> {} // empty space, skip it
                60L -> {
                    robot = c.copyD('<')
                    heading = Dir.W
                }
                62L -> {
                    robot = c.copyD('>')
                    heading = Dir.E
                }
                88L -> {
                    isDead = true
                    robot = c.copyD('X')
                    heading = Dir.N
                }
                94L -> {
                    robot = c.copyD('^')
                    heading = Dir.S // 0, 0 is top left, so south is "up" as in decreasing Y
                }
                118L -> {
                    robot = c.copyD('v')
                    heading = Dir.N  // 0, 0 is top left, so north is "down" as in increasing Y
                }
                10L -> {
                    x = -1
                    y ++
                }
                else -> throw IllegalStateException("Unknown output $out")
            }
            x++
        }

        suspend fun viewer() {
            ElfCode(code).runner (
                setup = ElfCode.memExpander(4000),
                output = { output(it) }
            )
        }

    }
}