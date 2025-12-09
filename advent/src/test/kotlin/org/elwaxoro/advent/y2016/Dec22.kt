package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

/**
 * Day 22: Grid Computing
 */
class Dec22: PuzzleDayTester(22, 2016) {

    override fun part1(): Any = loader().let { nodes ->
        nodes.sumOf { n1 -> 
            nodes.filter { n2 ->
                n1 != n2 && n1.used > 0 && n1.used <= n2.avail
            }.size
        }
    } == 860

    /**
     * So I did this one by hand real quick just to see what a general strategy should be for the code
     * The code below prints the grid:
     * S.................................E
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * .....................##############
     * ...................................
     * ...................................
     * ..........................._.......
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * ...................................
     * Then by moving the keyboard cursor and counting moves, its 35 up to the first swap ...E_
     * then it's +5 moves to get the _ in front and swap it again - repeat this 33 times
     * 33 * 5 + 25 = 200 and done
     */
    override fun part2(): Any = loader().map { 
        if (it.used == 0) {
            it.pos.copyD('_')
        } else if (it.used > 400) {
            it.pos.copyD('#')
        } else if (it.pos == Coord()) {
            it.pos.copyD('S')
        } else if (it.pos.x == 34 && it.pos.y == 0) {
            it.pos.copyD('E')
        } else {
            it.pos.copyD('.')
        }
    }.printify()
    
    private val regex = """/dev/grid/node-x(\d+)-y(\d+)\W+(\d+)T\W+(\d+)T\W+(\d+)T\W+(\d+)%""".toRegex()
    private fun loader() = load().drop(2).map { line ->
        val (x, y, size, used, avail, _) = regex.find(line)!!.destructured
        Node(Coord(x.toInt(), y.toInt()), size.toInt(), used.toInt(), avail.toInt())
    }
    
    data class Node(
        val pos: Coord,
        val size: Int,
        val used: Int,
        val avail: Int
    )
}
