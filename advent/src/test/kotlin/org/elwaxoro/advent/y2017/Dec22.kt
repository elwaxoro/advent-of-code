package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.Turn

/**
 * Day 22: Sporifica Virus
 */
class Dec22: PuzzleDayTester(22, 2017) {

    /**
     * idea: grid is "infinite" and clean unless otherwise marked, only track infected Coords
     */
    override fun part1(): Any = loader().let { infected -> 
        var pos = Coord(12, 12)
        var dir = Dir.N
        var ctr = 0
        repeat(10_000) {
            if (infected.contains(pos)) {
                // infected: turn R become clean
                dir = dir.turn(Turn.R)
                infected.remove(pos)
            } else {
                // clean: turn L become infected
                dir = dir.turn(Turn.L)
                infected.add(pos)
                ctr ++
            }
            pos = pos.move(dir)
        }
        ctr
    }

    /**
     * same as part 1 but more sets of things to track
     * not super fast but it gets the job done
     */
    override fun part2(): Any = loader().let { infected ->
        var pos = Coord(12, 12)
        var dir = Dir.N
        var ctr = 0
        val weakened = mutableSetOf<Coord>()
        val flagged = mutableSetOf<Coord>()

        repeat(10_000_000) {
            if (infected.contains(pos)) {
                // infected: turn R become flagged
                dir = dir.turn(Turn.R)
                infected.remove(pos)
                flagged.add(pos)
            } else if (weakened.contains(pos)) {
                // weakened: go straight become infected
                weakened.remove(pos)
                infected.add(pos)
                ctr ++
            } else if (flagged.contains(pos)) {
                // flagged: turn B become clean
                dir = dir.turn(Turn.B)
                flagged.remove(pos)
            } else {
                // clean: turn L become weakened
                dir = dir.turn(Turn.L)
                weakened.add(pos)
            }
            pos = pos.move(dir)
        }
        ctr
    }

    private fun loader() = load().reversed().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            Coord(x, y).takeIf { c == '#' }
        }
    }.toMutableSet()
}
