package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs
import kotlin.math.sign

class Dec09 : PuzzleDayTester(9, 2022) {

    override fun part1(): Any = loader().followMe().distinct().size == 5513

    override fun part2(): Any = loader().let { headPath ->
        (0..8).fold(headPath) { prevPath, _ ->
            prevPath.followMe()
        }.distinct().size
    } == 2427

    private fun List<Coord>.followMe(): List<Coord> =
        fold(mutableListOf(Coord(0, 0))) { tailTrack, next ->
            val tail = tailTrack.last()
            val dx = next.x - tail.x
            val dy = next.y - tail.y
            if (abs(dx) > 1 || abs(dy) > 1) {
                tailTrack.add(Coord(tail.x + dx.sign, tail.y + dy.sign))
            }
            tailTrack
        }

    private fun loader() = load().map {
        it.split(" ").let { (udlr, dist) ->
            Dir.fromUDLR(udlr[0]) to dist.toInt()
        }
    }.let { moves ->
        moves.fold(listOf(Coord(0, 0))) { prev, (dir, dist) ->
            prev.plus(prev.last().enumerateLine(prev.last().move(dir, dist)))
        }
    }
}
