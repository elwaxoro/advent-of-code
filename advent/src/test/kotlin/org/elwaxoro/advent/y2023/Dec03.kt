package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

/**
 * Day 3: Gear Ratios
 */
class Dec03 : PuzzleDayTester(3, 2023) {

    /**
     * 519444
     * A number is a "part number" if any of its component digit coordinates has a symbol for a neighbor
     * Find them, then add them up!
     */
    override fun part1(): Any = loader().let { schematic ->
        schematic.numbers.filter { number ->
            number.coords.any { c ->
                c.neighbors9().flatten().any { schematic.symbols.contains(it) }
            }
        }.sumOf { it.number }
    }

    /**
     * 74528807
     * A symbol is a gear if it has exactly two numbers for neighbors
     * Multiply those two numbers together to get gear ratio
     * Add up all gear ratios
     */
    override fun part2(): Any = loader().let { schematic ->
        // find all the * symbols (gears) and copy them without their value
        schematic.printableSymbols.filter { it.d == '*' }.map { it.copyD() }.map { gear ->
            // find all the numbers that touch this gear
            schematic.numbers.filter { partNumber ->
                partNumber.coords.any { c ->
                    c.neighbors9().flatten().any { it == gear }
                }
            // a gear is only REALLY a gear if it touches exactly two numbers
            }.takeIf { it.size == 2 }?.fold(1) { acc, partNumber ->
                acc * partNumber.number
            } ?: 0
        }.sum()
    }

    private fun loader(): EngineSchematic = load().let { lines ->
        val numbers: MutableList<PartNumber> = mutableListOf()
        val symbols: MutableList<Coord> = mutableListOf()

        lines.forEachIndexed { y, line ->
            var pendingNumber: MutableList<Coord> = mutableListOf()
            line.forEachIndexed { x, c ->
                if (c.isDigit()) {
                    // Start a new number or continue an existing one
                    pendingNumber.add(Coord(x, y, c))
                } else {
                    // End any pending number
                    if (pendingNumber.isNotEmpty()) {
                        val num = PartNumber.fromCoords(pendingNumber)
                        numbers.add(num)
                        pendingNumber = mutableListOf()
                    }
                    if (c != '.') {
                        // This is a symbol
                        symbols.add(Coord(x, y, c))
                    }
                }
            }
            // End any pending number
            if (pendingNumber.isNotEmpty()) {
                numbers.add(PartNumber.fromCoords(pendingNumber))
            }
        }

        EngineSchematic(numbers, symbols.map { it.copyD() }.toSet(), symbols)
    }

    /**
     * symbols is a set of raw Coord (x,y) without the value, so set contains works there
     * printableSymbols contain the value as well as the coord
     */
    private data class EngineSchematic(
        val numbers: List<PartNumber>,
        val symbols: Set<Coord>,
        val printableSymbols: List<Coord>
    ) {
        fun printify(): String = numbers.flatMap { it.coords }.plus(printableSymbols).printify()
    }

    private data class PartNumber(
        val number: Int,
        val coords: List<Coord>
    ) {
        companion object {
            fun fromCoords(input: List<Coord>): PartNumber = PartNumber(
                number = input.map { it.d!! }.joinToString("").toInt(),
                coords = input
            )
        }
    }
}
