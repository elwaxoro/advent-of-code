package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.*

private const val MAX_X = 100
private const val MAX_Y = 102

class Dec14 : PuzzleDayTester(14, 2024) {

    override fun part1(): Any = loader().let { robots ->
        (1..100).forEach { _ ->
            robots.forEach { robot -> robot.move() }
        }
        robots.count { it.p.x < MAX_X / 2 && it.p.y < MAX_Y / 2 } *
                robots.count { it.p.x > MAX_X / 2 && it.p.y < MAX_Y / 2 } *
                robots.count { it.p.x < MAX_X / 2 && it.p.y > MAX_Y / 2 } *
                robots.count { it.p.x > MAX_X / 2 && it.p.y > MAX_Y / 2 }
    }

    /**
     * IDK what to even do with this.
     * Idea: eventually a bunch of coords will get close enough together to make a picture
     */
    override fun part2(): Any = loader().let { robots ->
        (1..20000).forEach { i ->
            robots.forEach { robot -> robot.move() }
            if (robots.count { it.p.x in 25..75 && it.p.y in 25..75 } > robots.size / 2) {
                println("Iteration $i looks pretty good")
                println(robots.map { it.p }.printify())
                return i
            }
        }
        return -1
    }

    private fun loader() = load().map { Robot.load(it.remove("p=", "v=").split(" ").map { it.toCoord() }) }

    private data class Robot(
        var p: Coord,
        var v: Coord
    ) {
        companion object {
            fun load(coords: List<Coord>): Robot = Robot(coords[0], coords[1])
        }

        fun move() {
            p = p.add(v)
            val x = when {
                p.x < 0 -> MAX_X + p.x + 1
                p.x > MAX_X -> p.x - MAX_X - 1
                else -> p.x
            }
            val y = when {
                p.y < 0 -> MAX_Y + p.y + 1
                p.y > MAX_Y -> p.y - MAX_Y - 1
                else -> p.y
            }
            p = Coord(x, y)
        }
    }
}
