package org.elwaxoro.advent.y2019

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.*

/**
 * Day 11: Space Police
 */
class Dec11: PuzzleDayTester(11, 2019) {

    /**
     * Let the hull painting robot wander around randomly and make a weird lookin' blob
     */
    override fun part1(): Any = goRobot(Coord(0, 0, '0')).map { it.copyD('x') }.toSet().size == 2054

    /**
     * 2019-11 Puzzle 2: [1,-5] to [39,0]
     * X  X XXX  XXXX XXXX  XX    XX X  X XXX
     * X X  X  X    X X    X  X    X X  X X  X
     * XX   X  X   X  XXX  X  X    X XXXX XXX
     * X X  XXX   X   X    XXXX    X X  X X  X
     * X X  X X  X    X    X  X X  X X  X X  X
     * X  X X  X XXXX XXXX X  X  XX  X  X XXX  [70ms]
     */
    override fun part2(): Any = goRobot(Coord(0, 0, '1')).filter { it.d == '1' }.map { it.copyD('X') }.printify(invert = true, empty = ' ')

    @OptIn(DelicateCoroutinesApi::class)
    private fun goRobot(start: Coord) = runBlocking {
        ElfCode(loadToLong(delimiter = ",")).let { robot ->
            var pos = start
            var dir = Dir.N
            val visited = mutableListOf<Coord>()
            val input = Channel<Long>(capacity = Channel.UNLIMITED)
            val output = Channel<Long>(capacity = Channel.UNLIMITED)
            input.send(pos.dLong())
            launch {
                robot.runner({it.addAll(listOf(0L).padTo(2000))}, { input.receive() }, { output.send(it) }, { output.close() })
            }
            while (!output.isClosedForReceive) {
                // robot starts out every time already on the position it's going to paint, so record the output color here as a new coord
                val out = output.receive()
                visited.add(pos.copyD(out.toInt().digitToChar()))
                // turn left or right
                dir = dir.turn(Turn.L.takeIf { output.receive() == 0L } ?: Turn.R)
                // move forward one space.
                pos = pos.move(dir, md = '0')
                // if the space has been visited before, use that instead of creating a new black coord
                val previousVisit = visited.findLast { it.x == pos.x && it.y == pos.y }
                if (previousVisit != null) {
                    pos = previousVisit
                }
                // let compy know what color its seeing
                input.send(pos.dLong())
            }
            visited
        }
    }

    private fun Coord.dLong() = d!!.digitToInt().toLong()
}
