package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.*

/**
 * Day 8: Two-Factor Authentication
 */
class Dec08 : PuzzleDayTester(8, 2016) {

    /**
     * Lucked out here! Adding printify to verify part 1 behavior is the solution to part 2
     * Empty is ' ' instead of '.' for legibility
     */
    override fun part1(): Any = load().let { commands ->
        val lcd = (0..<6).map { (0..<50).map { ' ' }.toMutableList() }.toMutableList()
        commands.forEach { cmd ->
            lcd.command(cmd)
        }
        println(lcd.printify())
        lcd.sumOf { row -> row.count { it == '#' } }
    } // 106

    override fun part2(): Any = "See part 1 println"

    private fun MutableList<MutableList<Char>>.command(cmd: String) {
        if (cmd.startsWith("rect")) {
            this.rect(cmd)
        } else if (cmd.startsWith("rotate column")) {
            this.rotateColumn(cmd)
        } else if (cmd.startsWith("rotate row")) {
            this.rRow(cmd)
        } else {
            throw IllegalStateException("Unknown cmd: $cmd")
        }
    }

    private fun MutableList<MutableList<Char>>.rect(cmd: String) {
        val (a, b) = cmd.replace("rect ", "").split("x").map { it.trim().toInt() }
        (0..<b).forEach { y ->
            (0..<a).forEach { x ->
                this[y][x] = '#'
            }
        }
    }

    private fun <T> MutableList<MutableList<T>>.rotateColumn(cmd: String) =
        cmd.replace("rotate column x=", "").split(" by ").map { it.trim().toInt() }.let { (a, b) ->
            rotateColumn(a, b)
        }

    private fun MutableList<MutableList<Char>>.rRow(cmd: String) {
        val (a, b) = cmd.replace("rotate row y=", "").split(" by ").map { it.trim().toInt() }
        this[a] = this[a].rotate(b).toMutableList()
    }


}
