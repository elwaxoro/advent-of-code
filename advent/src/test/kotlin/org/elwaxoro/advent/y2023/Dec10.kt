package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.Dir.*
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.Turn.L

/**
 * Pipe Maze
 */
class Dec10 : PuzzleDayTester(10, 2023) {

    /**
     * 7107
     */
    override fun part1(): Any = loader().exploreLoop()

    /**
     * LAZY HACK: manually set the start tile and first entry direction
     * Run the loop until back to start, recording distance back to start on each tile as you go
     * After getting back to the start, the answer is the tile with distance loop size / 2
     */
    private fun List<List<Tile>>.exploreLoop(): Long {
        val startChar = '-'
        val startEntry = E
        val start = flatten().find { it.char == 'S' }!!
        var tile = start.copy(char = startChar, dist = 0)
        var entryDir = startEntry

        while (tile.char != 'S') {
            val exitDir = tile.exitDir(entryDir)
            val exitCoord = tile.exitCoord(exitDir)
            val exitTile = this[exitCoord.y][exitCoord.x]
            exitTile.dist = tile.dist + 1
            tile = exitTile
            entryDir = exitDir.turn(L).turn(L)
        }

        return tile.dist / 2L
    }

    /**
     * 281
     *
     * Idea: since the critter can squeeze between touching pipes, but those spaces don't exist as proper coordinates:
     * expand the ENTIRE space, then flood fill everything that can be reached. Any remaining tiles are fully enclosed.
     */
    override fun part2(): Any = loader().let { tiles ->
        // start out with the explored loop (don't need the answer from part 1)
        tiles.exploreLoop()

        // expand tiles on the X axis
        val explodedRows = tiles.map { row ->
            row.flatMapIndexed { index, tile ->
                var char = '?' // ? = valid candidate tile in the solution
                var nextChar = '.' // . = expansion tile that isn't part of the solution
                if (tile.dist >= 0) {
                    char = tile.char
                    if (index + 1 < row.size && char != '7' && char != '|' && char != 'J') {
                        nextChar = '-' // this expansion tile counts as part of the loop
                    }
                }
                listOf(
                    Coord(x = index * 2, y = tile.coord.y, d = char),
                    Coord(x = index * 2 + 1, y = tile.coord.y, d = nextChar)
                )
            }
        }
        // expand tiles on the Y axis
        val fullyExploded = explodedRows.flatMapIndexed { index, row ->
            val extraRow = mutableListOf<Coord>()
            val modRow = row.map { coord ->
                val nextChar = if (listOf('.', '?', '7', '-', 'F').contains(coord.d)) {
                    '.' // this expansion tile is unknown if inside/outside the loop but NOT a candidate for the solution
                } else {
                    '|' // this expansion tile counts as part of the loop
                }
                if (index + 1 < explodedRows.size) {
                    extraRow.add(Coord(coord.x, index * 2 + 1, nextChar))
                }
                Coord(coord.x, index * 2, coord.d)
            }
            listOf(modRow, extraRow)
        }

        val outside = mutableSetOf<Coord>()
        val candidates = mutableSetOf<Coord>()
        val loop = mutableSetOf<Coord>()
        val search = mutableSetOf<Coord>()
        fullyExploded.flatten().forEach {
            when (it.d) {
                '?' -> candidates.add(it.copyD()) // these were original tiles, unknown if they're inside or outside the loop
                '.' -> search.add(it.copyD()) // these are newly expanded tiles, part of the search space but NOT allowed to
                else -> loop.add(it)
            }
        }
        search.addAll(candidates)

        // keep exploring until tiles stop getting removed from the search set
        var keepGoing = true
        while (keepGoing) {
            // remove a tile if it's on the edge of the map or if any of its neighbors are already outside the loop (N,S,E,W, not diagonal)
            val remove = search.filter { coord ->
                coord.x == 0 || coord.y == 0 ||
                        coord.x == fullyExploded.size - 1 || coord.y == fullyExploded.size - 1 ||
                        coord.neighbors().any { outside.contains(it) }
            }.toSet()
            outside.addAll(remove)
            candidates.removeAll(remove)
            search.removeAll(remove)
            keepGoing = remove.isNotEmpty()
        }
        // any remaining candidates are inside the loop
        candidates.size
    }

    /**
     * reverse the rows (so highest y is first row instead of last) so that x,y works better with my existing coordinate helpers
     */
    private fun loader() = load().mapIndexed { y, s -> s.mapIndexed { x, pipe -> Tile(Coord(x, s.length - y - 1, pipe), pipe) } }.reversed()

    private data class Tile(
        val coord: Coord,
        val char: Char,
        var dist: Long = -1
    ) {
        fun exitCoord(exitDir: Dir): Coord = coord.move(exitDir)

        fun exitDir(entry: Dir): Dir =
            when (char) {
                '|' -> when (entry) {
                    N -> S
                    S -> N
                    else -> throw IllegalStateException("No BAD")
                }

                '-' -> when (entry) {
                    E -> W
                    W -> E
                    else -> throw IllegalStateException("No BAD")
                }

                'L' -> when (entry) {
                    N -> E
                    E -> N
                    else -> throw IllegalStateException("No BAD")
                }

                'J' -> when (entry) {
                    N -> W
                    W -> N
                    else -> throw IllegalStateException("No BAD")
                }

                '7' -> when (entry) {
                    W -> S
                    S -> W
                    else -> throw IllegalStateException("No BAD")
                }

                'F' -> when (entry) {
                    E -> S
                    S -> E
                    else -> throw IllegalStateException("No BAD")
                }

                else -> throw IllegalStateException("No BAD")
            }
    }
}
