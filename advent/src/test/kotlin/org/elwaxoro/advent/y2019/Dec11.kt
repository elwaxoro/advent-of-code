package org.elwaxoro.advent.y2019

import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    override fun part1(): Any = runTheCompy(Coord(0,0, '0')).let { visited ->
        println(visited.printify(invert = true))
        visited.map { it.copyD('x') }.toSet().size
    } == 2054

    private fun Coord.dLong() = d!!.digitToInt().toLong()

    /**
     * Outputs some words! Good job hull painting robit
     * X  X XXX  XXXX XXXX  XX    XX X  X XXX
     * X X  X  X    X X    X  X    X X  X X  X
     * XX   X  X   X  XXX  X  X    X XXXX XXX
     * X X  XXX   X   X    XXXX    X X  X X  X
     * X X  X X  X    X    X  X X  X X  X X  X
     * X  X X  X XXXX XXXX X  X  XX  X  X XXX
     */
    override fun part2(): Any = runTheCompy(Coord(0,0, '1')).let { visited ->
        println(visited.filter { it.d == '1' }.map { it.copyD('X') }.printify(invert = true, empty = ' '))
        1
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun runTheCompy(startingCoord: Coord): List<Coord> = IntercodeV9(loadToLong(delimiter = ",")).let { compy ->
        runBlocking {
            var pos = startingCoord
            var facing = Dir.N
            val visited = mutableListOf<Coord>()
            compy.input.send(pos.dLong())
            compy.expandMem(2000)
            launch {
                compy.run()
            }
            while(!compy.output.isClosedForReceive) {
                // compy starts out every time already on the position its going to paint, so record the output color here as a new coord
                visited.add(pos.copyD(compy.output.receive().toInt().digitToChar()))
                // turn left or right
                facing = facing.turn(if(compy.output.receive() == 0L) { Turn.L } else { Turn.R })
                // move forward one space. if the space has been visited before, use that instead of creating a new black coord
                pos = pos.move(facing).copyD('0')
                val previousVisit = visited.findLast { it.x == pos.x && it.y == pos.y }
                if (previousVisit != null) {
                    pos = previousVisit
                }
                // let compy know what color its seeing
                compy.input.send(pos.dLong())
            }
            visited
        }
    }
}