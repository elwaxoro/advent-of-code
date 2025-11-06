package org.elwaxoro.advent.y2020

import org.elwaxoro.advent.PuzzleDayTester
import java.util.Locale

/**
 * resource https://www.redblobgames.com/grids/hexagons/
 * was SUPER helpful on this one, used the 3D coordinate system for this puzzle
 */
class Dec24 : PuzzleDayTester(24, 2020) {

    override fun part1(): Any = flipYourShit().size

    override fun part2(): Any = (1..100).fold(flipYourShit()) { hexes, round ->
        // really slow (broken?), commenting out for now
        //flipAroundRightRoundLikeARecordBaby(hexes)
        hexes
    }.size

    private fun flipYourShit(): Set<Hex> = parse().let { moveLists ->
        val startingHex = Hex(0, 0, 0)
        moveLists.map { moveList ->
            moveList.fold(startingHex) { prevHex, move ->
                prevHex.move(move)
            }
        }.groupBy { it }.filter { it.value.size % 2 != 0 }.keys
    }

    private fun flipAroundRightRoundLikeARecordBaby(flippedHexes: Set<Hex>): Set<Hex> = flippedHexes.map { hex ->
        val neighbors = hex.neighbors()
        val flippedNeighbors = neighbors.intersect(flippedHexes).size

        val newFlippedNeighbors: List<Hex> = neighbors.filterNot { it in flippedHexes }.mapNotNull { neighbor ->
            val flippedNeighborNeighbors = flippedHexes.intersect(neighbor.neighbors()).size
            if (flippedNeighborNeighbors == 2) {
                neighbor
            } else {
                null
            }
        }

        if (flippedNeighbors == 0 || flippedNeighbors > 2) {
            newFlippedNeighbors
        } else {
            newFlippedNeighbors.plus(hex)
        }
    }.flatten().toSet()

    private data class Hex(val x: Int, val y: Int, val z: Int) {

        fun move(dir: HexDir): Hex =
            when (dir) {
                HexDir.E -> Hex(x + 1, y - 1, z)
                HexDir.W -> Hex(x - 1, y + 1, z)
                HexDir.NE -> Hex(x + 1, y, z - 1)
                HexDir.NW -> Hex(x, y + 1, z - 1)
                HexDir.SE -> Hex(x, y - 1, z + 1)
                HexDir.SW -> Hex(x - 1, y, z + 1)
            }

        fun neighbors(): List<Hex> = HexDir.values().map { move(it) }
    }

    private enum class HexDir { E, W, NE, NW, SE, SW }

    private fun parse(): List<List<HexDir>> = load().map {
        var str = it
        val moves = mutableListOf<HexDir>()
        while (str.isNotEmpty()) {
            val move: String = if (str.first() == 's' || str.first() == 'n') {
                str.substring(0, 2)
            } else {
                str.substring(0, 1)
            }
            moves.add(HexDir.valueOf(move.uppercase(Locale.getDefault())))
            str = str.drop(move.length)
        }
        moves
    }
}
