package org.elwaxoro.advent.y2019

import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.*

/**
 * Day 17: Set and Forget
 * Aft Scaffolding Control and Information Interface (ASCII LOL)
 */
class Dec17 : PuzzleDayTester(17, 2019) {

    override fun part1(): Any = runBlocking {
        val ascii = Ascii(loadToLong(delimiter = ","))
        ascii.viewer()
        ascii.coords.filter { it.neighbors().all { n -> ascii.coords.contains(n) } }.sumOf { it.x * (40 - it.y) }
    } == 8408

    override fun part2(): Any = runBlocking {
        val ascii = Ascii(loadToLong(delimiter = ","))
        // run the viewer first to build the map
        // NOTE: I think running it in input mode still builds the map first?
        ascii.viewer()
        ascii.explorer(buildInput(buildPath(ascii)))
        ascii.lastOut
    } == 1168948L

    /**
     * Build a path of turns + straight moves to go from start to end
     */
    private fun buildPath(ascii: Ascii): String {
        // NOTE: end coord calculated by hand using the printify function, won't work for all input
        val end = Coord(28, 12)
        //println(ascii.coords.plus(ascii.robot).plus(end.copyD('E')).printify(empty = ' ', invert = true))

        var robot = ascii.robot.copyD()
        var dir = ascii.heading
        var actions = Turn.A.name
        val path = mutableListOf<Coord>()
        val options = listOf(Turn.A, Turn.R, Turn.L)
        var steps = 0
        while (robot != end) {
            val turn = options.first { turn ->
                ascii.coords.contains(robot.move(dir.turn(turn)))
            }
            dir = dir.turn(turn)
            robot = robot.move(dir)

            if (turn == Turn.A) {
                steps++
            } else {
                actions += ",$steps $turn"
                steps = 1
            }
            path.add(robot)
        }
        actions += ",$steps"
        return actions
    }

    /**
     * Split the actions up into turn + distance pairs
     * Assign each pair a letter
     * Manually find a few patterns to break everything into exactly 3 sets of patterns
     * Combine into a single long ascii string to feed into the robot
     * NOTE: this is not automatic, I hand-identified the groups and hand-build the input string
     */
    private fun buildInput(actions: String): MutableList<Int> {
        val split = actions.split(" ").drop(1)
        val abc = "ABCDEFGHIJKLMNOP".toList()
        val names = mutableMapOf<String, Char>()
        var i = 0
        val simplified = split.map {
            if (!names.containsKey(it)) {
                names[it] = abc[i]
                i++
            }
            names[it]!!
        }.joinToString("")
        /*
         * From here, output was
         * R,6, L,10, R,8, R,8, R,12, L,8, L,10, R,6, L,10, R,8, R,8, R,12, L,10, R,6, L,10, R,12, L,8, L,10, R,12, L,10, R,6, L,10, R,6, L,10, R,8, R,8, R,12, L,8, L,10, R,6, L,10, R,8, R,8, R,12, L,10, R,6, L,10
         * becoming
         * ABCCDEBABCCDBABDEBDBABABCCDEBABCCDBAB
         * there are only 5 distinct movements!
         * hand reduced to 3 groups: ABCC, DEB, DBAB:
         * ABCC DEB ABCC DBAB DEB DBAB ABCC DEB ABCC DBAB
         * for the intcode inputs:
         * fn A = ABCC = R,6,L,10,R,8,R,8
         * fn B = DEB = R,12,L,8,L,10
         * fn C = DBAB = R,12,L,10,R,6,L,10
         * main: A,B,A,C,B,C,A,B,A,C
         */

        val main = "A,B,A,C,B,C,A,B,A,C\n"
        val a = "R,6,L,10,R,8,R,8\n"
        val b = "R,12,L,8,L,10\n"
        val c = "R,12,L,10,R,6,L,10\n"
        val yn = "n\n"
        return (main + a + b + c + yn).toASCII().toMutableList()
    }

    private fun String.toASCII(): List<Int> = map { it.code }

    private class Ascii(
        val code: List<Long>,
        val coords: MutableSet<Coord> = mutableSetOf(),
        var x: Int = 0,
        var y: Int = 40,
        var robot: Coord = Coord(0, 0),
        var heading: Dir = Dir.N,
        var isDead: Boolean = false,
        var lastOut: Long = -1,
    ) {

        /**
         *  camera outputs:
         *  35 = # (scaffold)
         *  46 = . (empty)
         *  10 = newline
         *  draw left to right
         *  random ascii text for part 2
         */
        fun output(out: Long) {
            val c = Coord(x, y)
            when (out) {
                35L -> coords.add(c)
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
                    heading = Dir.N
                }

                118L -> {
                    robot = c.copyD('v')
                    heading = Dir.S
                }

                10L -> {
                    x = -1
                    y--
                }

                else -> {}
            }
            lastOut = out
            x++
        }

        suspend fun viewer() {
            ElfCode(code).runner(
                setup = ElfCode.memExpander(4000),
                output = { output(it) }
            )
        }

        /**
         * Note: this feeds all input as a single list, just getting the next one when requested
         * After running the program I noticed there's some nice output text it provides to prompt each input first
         */
        suspend fun explorer(input: MutableList<Int>) {
            ElfCode(code).runner(
                setup = {
                    ElfCode.memExpander(4000).invoke(it)
                    it[0] = 2
                },
                input = { input.removeFirst().toLong() },
                output = { output(it) },
            )
        }
    }
}