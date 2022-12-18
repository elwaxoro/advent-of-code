package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.*

/**
 * Day 17: Pyroclastic Flow
 *
 * 5 rock shapes, fall in repeated order
 * Jets push (input, loops) falling rocks every rep, then rock falls down one pixel
 * chamber is 7 pixels wide
 * rocks start out 2 pixels from left edge and 3 pixels from the top of the pile
 *
 * falling: first, jet input and move. next fall or settle. if settled: add a new rock
 */
class Dec17 : PuzzleDayTester(17, 2022) {

    private fun Iterable<Coord>.move(dir: Dir, dist: Int = 1): List<Coord> = map { it.move(dir, dist) }

    val rocks = listOf(
        listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0)), // horizontal bar
        listOf(Coord(1, 0), Coord(0, 1), Coord(1, 1), Coord(2, 1), Coord(1, 2)), // plus
        listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(2, 1), Coord(2, 2)), // backwards L
        listOf(Coord(0, 0), Coord(0, 1), Coord(0, 2), Coord(0, 3)), // vertical bar
        listOf(Coord(0, 0), Coord(1, 0), Coord(0, 1), Coord(1, 1)) // cube
    )

    override fun part1(): Any = playTetris(2022)// == 3114L
    override fun part2(): Any = playTetris(1000000000000)// == 1540804597682L

    private fun playTetris(targetFallenRocks: Long): Long {
        val moves = loader()
        var rockIdx = 0
        var moveIdx = 0
        var fallenRocks = 0L
        val minX = 0
        val maxX = 6
        val floor = setOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0), Coord(4, 0), Coord(5, 0), Coord(6, 0))
        val stack: MutableSet<Coord> = floor.toMutableSet()

        var activeRock = rocks[rockIdx].move(Dir.N, stack.maxY() + 4).move(Dir.E, 2)
        val snapshots = mutableMapOf<Snapshot, Pair<Long, Int>>()
        var lockedSnap: Snapshot? = null
        var stackOffset = 0L

        while (fallenRocks < targetFallenRocks) {
            val minY = activeRock.minY()
            val moveLR = activeRock.move(moves[moveIdx % moves.size])
            var collide = moveLR.any { stack.contains(it) } || moveLR.minX() < minX || moveLR.maxX() > maxX
            activeRock = moveLR.takeUnless { collide } ?: activeRock
            moveIdx++
            val moveDown = activeRock.move(Dir.S)
            collide = moveDown.any { stack.contains(it) }

            activeRock = moveDown.takeUnless { collide } ?: activeRock

            if (activeRock.minY() == minY) {
                stack.addAll(activeRock)
                rockIdx++
                fallenRocks++

                // capture a snapshot of the top of the stack
                val snapshot = Snapshot(stack.top(), fallenRocks % rocks.size, moveIdx % moves.size)

                // detect and keep the first loop only
                if (snapshots.containsKey(snapshot) && (lockedSnap == null || snapshot == lockedSnap)) {
                    val (rocksAtStart, heightAtStart) = snapshots[snapshot]!!
                    val rocksInLoop = fallenRocks - rocksAtStart
                    val heightInLoop = stack.maxY() - heightAtStart
                    val loopsNeeded = ((targetFallenRocks - rocksAtStart) / rocksInLoop) - 1 // there's already one loop in the stack currently

                    // snapshot has captured a repeating pattern that we can use to skip ahead
                    println("FOUND THE LOOP! $fallenRocks fallen at stack size ${stack.maxY()}. blocks at start: ${snapshots[snapshot]!!.first}. height: ${snapshots[snapshot]!!.second}. loop size is $rocksInLoop next hit at ${fallenRocks + rocksInLoop}. loop height is $heightInLoop. loops needed: $loopsNeeded")
                    println(snapshot)

                    lockedSnap = snapshot
                    // jump ahead, then let the normal loop play out till the end
                    fallenRocks += loopsNeeded * rocksInLoop
                    stackOffset = loopsNeeded * heightInLoop
                } else if (lockedSnap == null) {
                    snapshots[snapshot] = fallenRocks to stack.maxY()
                }

                activeRock = rocks[rockIdx % rocks.size].move(Dir.N, stack.maxY() + 4).move(Dir.E, 2)
            }
        }
        return stack.maxY() + stackOffset
    }

    /**
     * top of the stack for each x, moved down so the lowest y is at 0
     */
    private fun Set<Coord>.top() = groupBy { it.x }.map { (x, coords) -> Coord(x, coords.maxY()) }.let {
        it.move(Dir.S, it.minY())
    }

    private data class Snapshot(val top: List<Coord>, val blocks: Long, val moves: Int)

    private fun loader() = load().single().map { move -> Dir.E.takeIf { move == '>' } ?: Dir.W }
}
