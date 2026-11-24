package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.Turn

/**
 * Day 19: A Series of Tubes
 */
class Dec19: PuzzleDayTester(19, 2017) {
    
    override fun part1(): Any = explore().first

    override fun part2(): Any = explore().second

    /**
     * reverse the input lines so that while iterating, 0, 0 is bottom left instead of top left
     * and existing Coord / N S E W coordinate math will work as expected
     * mostly just means the starting Y will be at rows.size - 1 instead of 0
     */
    private fun explore(): Pair<String, Int> = load().reversed().let { rows ->
        var steps = 0
        var pos = Coord(rows.last().indexOfFirst { it != ' ' }, rows.size - 1)
        var dir = Dir.S
        var keepGoing = true
        val seen = mutableListOf<Char>()
        while (keepGoing) {
            pos = pos.move(dir)
            steps++
            when (val char = rows[pos.y][pos.x]) {
                ' ' -> keepGoing = false // we fell off the track. probably just stop it's either a bug or we're done
                '|' -> {} // keep going
                '-' -> {} // keep going
                '+' -> {
                    // navigate the turn, it will be either L or R one will be a valid position the other will be a space
                    // handle the actual newPos on the following loop
                    dir = listOf(dir.turn(Turn.L), dir.turn(Turn.R)).single { newDir ->
                        val newPos = pos.move(newDir)
                        val newChar = rows[newPos.y][newPos.x]
                        newChar != ' '
                    }
                }
                in 'A'..'Z' -> seen.add(char) // capture the letter, keep going
            }
        }
        // part 1 to part 2
        seen.joinToString("") to steps
    }
}
