package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 25: The Halting Problem
 */
class Dec25: PuzzleDayTester(25, 2017) {

    override fun part1(): Any {
        return super.part1()
    }

    private fun parser() = load(delimiter = "\n\n").let { blocks -> 
        val (s, d) = blocks.first().split("\n")
        val startState = s.replace("Begin in state ", "").replace(".", "")
        val diagnostic = d.replace("Perform a diagnostic checksum after ", "").replace(" steps.", "").toInt()
        
        blocks.drop(1).map { block -> 
            val lines = block.split("\n")
            val name = lines[0].replace("In state ", "").replace(".", "")
            /*
              If the current value is 0:
            - Write the value 1.
            - Move one slot to the right.
            - Continue with state B.
             */
        }
    }
    
    private fun parseAction(lines: List<String>) {
        val compare = 0.takeIf { lines[0].contains("0") } ?: 1
        val write = lines[1].replace("    - Write the value ", "").replace(".", "").toInt()
        val moveRight = lines[2].contains("right")
        val nextState = lines[3].replace("    - Continue with state ", "").replace(".", "")
    }
    
    
}
