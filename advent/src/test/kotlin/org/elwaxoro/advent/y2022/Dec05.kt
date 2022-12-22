package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.rowColSwap
import org.elwaxoro.advent.takeSplit

/**
 * Day 5: Supply Stacks
 */
class Dec05 : PuzzleDayTester(5, 2022) {

    /**
     * Presumably, a CrateMover 9000
     */
    override fun part1(): Any = loader().let { (stacks, moves) ->
        crateMover(stacks, moves, true)
    } == "FWSHSPJWM"

    /**
     * Wait this isn't a CrateMover 9000 it's a CrateMover 9001
     * OH SHIT
     */
    override fun part2(): Any = loader().let { (stacks, moves) ->
        crateMover(stacks, moves, false)
    } == "PWPWHGFZS"

    private fun crateMover(stacks: MutableMap<String, List<String>>, moves: List<List<String>>, isReversed: Boolean): String =
        stacks.also {
            moves.forEach { move ->
                stacks[move[1]]!!.takeSplit(move[0].toInt()).let { (toMove, toKeep) ->
                    stacks[move[1]] = toKeep
                    stacks[move[2]] = (toMove.takeUnless { isReversed } ?: toMove.reversed()).plus(stacks[move[2]]!!)
                }
            }
        }.map { it.value.first() }.joinToString("")

    /**
     * The award for most annoying parser goes to...
     */
    private fun loader() = load(delimiter = "\n\n").let { (stacks, moves) ->

        //parse the "stacks" section of input
        val parsedStacks = stacks.split("\n").map {
            it.chunked(4) // each stack is 4 characters wide
        }.rowColSwap().map { stack -> // vertical stacks are now rows, last value in row is the stack name
            // get rid of square braces, spaces, and empty garbage
            stack.map { it.replace("[", "").replace("]", "").trim() }.filterNot { it.isBlank() }
        }.filter { it.isNotEmpty() }.associate {
            // last item in list is the stack name, everything else is the stack contents
            // first in stack list is the top item
            it.last() to it.dropLast(1)
        }.toMutableMap()

        // parse the "instructions" section of input
        val parsedMoves = moves.split("\n").map { line ->
            // get rid of all the text, then split on whitespace
            line.replace("move ", "").replace("from ", "").replace("to ", "").split(" ")
        }
        parsedStacks to parsedMoves
    }


//    private fun crateMover(stacks: MutableMap<String, List<String>>, moves: List<List<String>>, isReversed: Boolean): String {
//        moves.forEach { move ->
//            val sourceStack = stacks[move[1]]!!
//            val destStack = stacks[move[2]]!!
//            val copyCount = move[0].toInt()
//            var toMove = sourceStack.take(copyCount)
//            if (isReversed) { // CrateMover 9001 drops crates in their original order
//                toMove = toMove.reversed()
//            }
//            stacks[move[1]] = sourceStack.drop(copyCount)
//            stacks[move[2]] = toMove.plus(destStack)
//        }
//        return stacks.map { it.value.first() }.joinToString("")
//    }
}
