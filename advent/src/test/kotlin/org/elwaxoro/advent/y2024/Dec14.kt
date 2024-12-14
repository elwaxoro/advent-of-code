package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.*

class Dec14: PuzzleDayTester(14, 2024) {

    override fun part1(): Any = loader().let { robots ->
        val bounds: Pair<Coord, Coord> = Pair(Coord(0, 0), Coord(100, 102))
        (1..(101*103)).forEach {
            robots.forEach { robot -> robot.move() }
            if (robots.count { it.p.x in 25..75 && it.p.y in 25..75 } > robots.size/2) {
                println("Iteration $it looks pretty good")
                println(bounds.toList().plus(robots.map { it.p }).printify())
                return 0
            }
        }

        val midOut = robots.filter { it.p.x != bounds.second.x/2 && it.p.y != bounds.second.y/2 }
        val vertSplit = midOut.partition { it.p.x < bounds.second.x/2 }.toList()
        val horizSplit = vertSplit.flatMap { it.partition { it.p.y < bounds.second.y/2 }.toList() }
        println(bounds.toList().plus(midOut.map { it.p.copyD('R') }).printify())
        horizSplit.fold(1) { acc, split -> acc * split.size }
    }

    // 10403 too high
    override fun part2(): Any {
        return super.part2()
    }

    private fun loader() = load().map { Robot.load(it.remove("p=", "v=").split(" ").map { it.toCoord() }) }

    private data class Robot(
        var p: Coord,
        var v: Coord,
        val bounds: Pair<Coord, Coord> = Pair(Coord(0, 0), Coord(100, 102))
    ) {
        companion object {
            fun load(coords: List<Coord>): Robot = Robot(coords[0], coords[1])
        }

        fun move() {
            p = p.add(v)
            if (!bounds.contains(p)) {
                // wrap around
                var x = p.x
                var y = p.y
                if (p.x < 0) {
                    x = bounds.second.x + p.x + 1
                } else if (p.x > bounds.second.x) {
                    x = p.x - bounds.second.x - 1
                }
                if (p.y < 0) {
                    y = bounds.second.y + p.y + 1
                } else if (p.y > bounds.second.y) {
                    y = p.y - bounds.second.y - 1
                }
                p = Coord(x, y)
            }
        }
    }
}
