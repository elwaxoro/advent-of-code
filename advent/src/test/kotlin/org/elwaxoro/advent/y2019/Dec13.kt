package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

/**
 * Day 13: Care Package
 */
class Dec13 : PuzzleDayTester(13, 2019) {

    override fun part1(): Any = runBlocking { Game(loadToLong(delimiter = ",")).play().coords.values.count { it == 'X' } } == 260

    override fun part2(): Any = runBlocking { Game(loadToLong(delimiter = ","), play = true).play().score } == 12952

    private class Game(
        val code: List<Long>,
        val coords: MutableMap<Coord, Char> = mutableMapOf(),
        var paddle: Coord = Coord(-1, 0),
        var ball: Coord = Coord(-1, 0),
        var score: Int = 0,
        val xyz: MutableList<Int> = mutableListOf(),
        val play: Boolean = false,
    ) {

        private fun Int.toPixel(): Char =
            when (this) {
                0 -> ' ' // empty
                1 -> '#' // wall
                2 -> 'X' // block
                3 -> '-' // paddle
                4 -> 'o' // ball
                else -> throw IllegalStateException("Unknown tile id $this")
            }

        fun print() {
            if (coords.isNotEmpty()) {
                println(coords.map { it.key.copyD(it.value) }.printify())
            } else {
                println("NO DATA")
            }
            println("Score: $score")
        }

        private val joystick: () -> Long = {
            if (paddle.x == ball.x) {
                0
            } else if (paddle.x < ball.x) {
                1
            } else {
                -1
            }
        }

        private val reader: (i: Long) -> Unit = {
            xyz.add(it.toInt())
            if (xyz.size == 3) {
                if (xyz[0] < 0) {
                    score = xyz[2]
                } else {
                    val c = Coord(xyz[0], xyz[1])
                    val d = xyz[2].toPixel()
                    coords[c] = d
                    if (d == '-') {
                        paddle = c
                    } else if (d == 'o') {
                        ball = c
                    }
                }
                xyz.clear()
            }
        }

        suspend fun play(): Game {
            ElfCode(code).runner(
                setup = {
                    ElfCode.memExpander(100000).invoke(it)
                    if (play)
                        it[0] = 2
                },
                input = joystick,
                output = reader,
                exit = {
                    print()
                })
            return this
        }
    }
}
