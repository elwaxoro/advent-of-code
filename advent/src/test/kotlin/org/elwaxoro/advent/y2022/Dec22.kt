package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.*
import kotlin.math.max
import kotlin.system.exitProcess

/**
 * Day 22: Monkey Map
 */
class Dec22 : PuzzleDayTester(22, 2022) {

    private val empty = '_'

    override fun part1(): Any = loader().let { scenario ->

        val map = scenario.map
        val mapR = map.rowColSwap()

        val path = mutableListOf(scenario.start)
        var dir = Dir.N
        var pos = scenario.start
        scenario.moves.forEach { m ->
            dir = dir.turn(m.turn)
            (0 until m.dist).forEach { _ ->
                var newPos = pos.move(dir)
                var lookup = map[newPos.y][newPos.x]
                newPos = if (lookup.d == empty) {
                    when (dir) {
                        Dir.N -> mapR[newPos.x].wrapToStart()
                        Dir.S -> mapR[newPos.x].wrapToEnd()
                        Dir.E -> map[newPos.y].wrapToStart()
                        Dir.W -> map[newPos.y].wrapToEnd()
                    }
                } else {
                    newPos
                }
                lookup = map[newPos.y][newPos.x]
                if (lookup.d == '.') {
                    pos = newPos
                    path.add(pos)
                }
            }
        }
        println("Ending position: $pos facing $dir row is ${map.size - pos.y - 1}")

        val row = map.size - pos.y - 1
        val col = pos.x
        val face = when (dir) {
            Dir.N -> 3
            Dir.S -> 1
            Dir.E -> 0
            Dir.W -> 2
        }

        (row * 1000) + (4 * col) + face
    } == 123046

    private fun List<Coord>.wrapToStart(): Coord = first { it.d != empty }
    private fun List<Coord>.wrapToEnd(): Coord = last { it.d != empty }

    fun runPart2(size: Int, height: Int, start: Coord, startFace: Face, faces: Map<String, Face>, moves: List<Move>): Int {
        var pos = start
        var dir = Dir.N
        var face = startFace
        val trueStart = face.trueCoord(start).copyD('S')
        val path = mutableListOf(face.trueCoord(start))
        moves.forEach { m ->
            dir = dir.turn(m.turn)
            (0 until m.dist).forEach { _ ->
                val newPos = pos.move(dir)
                val wrap = if(newPos.x < 0) {
                    // wrap west (left side)
                    face.wrap(dir, pos)
                } else if(newPos.x == size) {
                    // wrap east (right side)
                    face.wrap(dir, pos)
                } else if(newPos.y < 0) {
                    // wrap south (bottom side)
                    face.wrap(dir, pos)
                } else if(newPos.y == size) {
                    // wrap north (top side)
                    face.wrap(dir, pos)
                } else {
                    Wrap(newPos, dir, face)
                }
                val target = wrap.coordAt()
                if(target.d == '.') {
                    dir = wrap.dir
                    pos = wrap.coord
                    face = wrap.face
                    path.add(face.trueCoord(pos))
                }
            }
//            println(faces.flatMap { it.value.trueCoords() }.plus(path.map { it.copyD('X') }).plus(trueStart).plus(path.last().copyD('E')).printify(invert = true, empty = ' '))
        }
        val last = path.last().copyD('E')
        //println(faces.flatMap { it.value.trueCoords() }.plus(path.map { it.copyD('X') }).plus(trueStart).plus(path.last().copyD('E')).printify(invert = true, empty = ' '))
//        println("final pos: ${path.last()} final facing: $dir")
        val dirScore = when (dir) {
            Dir.N -> 3
            Dir.S -> 1
            Dir.E -> 0
            Dir.W -> 2
        }

        val row = height - last.y // puzzle inverted, so we're looking from top down starting at 1 instead of zero
        val col = last.x + 1 // x counting starts at 1
//        println("row: $row, col: $col")
        return (row * 1000) + (4 * col) + dirScore
    }

    fun examplePart2(): Any = faceLoader(faceSize = 4).let { (faces, moves) ->
        initExample(faces)
        val size = 4
        val height = 12
        val start = Coord(0, 3, '.')
        runPart2(size, height, start, faces["2,2"]!!, faces, moves)
    }

    override fun part2(): Any = faceLoader(faceSize = 50).let { (faces, moves) ->
        initPart2(faces)
        val size = 50
        val height = 200
        val start = Coord(0, 49, '.')
        runPart2(size, height, start, faces["1,3"]!!, faces, moves)
    } == 195032

    /**
     * Part 1 loader is trying to force the puzzle into a square shape with dead coords all the way around to make
     * wrapping easier. This totally doesn't work at all for part 2.
     */
    private fun loader() = load(delimiter = "\n\n").let { (map, moves) ->
        val splitMap = map.split("\n")
        val maxX = splitMap.maxOf { it.length }
        val maxY = splitMap.size
        val maxLength = max(maxX, maxY)
        val bottomRow: List<Coord> = (0..maxLength + 1).map { Coord(it, 0, empty) }
        val topRows: List<List<Coord>> = (maxY + 1 until maxLength + maxLength - maxY + 2).map { y -> (0..maxLength + 1).map { x -> Coord(x, y, empty) } }
        val coords = splitMap.reversed().mapIndexed { y, row ->
            listOf(Coord(0, y + 1, empty)).plus(
                row.padEnd(maxLength, ' ').mapIndexed { x, c ->
                    if (c == '.' || c == '#') {
                        Coord(x + 1, y + 1, c)
                    } else {
                        Coord(x + 1, y + 1, empty)
                    }
                }).plus(Coord(maxLength + 1, y + 1, empty))
        }
        val start = coords.last().first { it.d == '.' }

        val finalCoords = mutableListOf<List<Coord>>()
        finalCoords.add(bottomRow)
        finalCoords.addAll(coords)
        finalCoords.addAll(topRows)
        Scenario(finalCoords, start, moves.loadMoves())
    }

    private fun faceLoader(faceSize: Int) = load(delimiter = "\n\n").let { (map, moves) ->
        val splitMap = map.split("\n")
        val faceMap = mutableMapOf<String, MutableList<MutableList<Coord>>>()
        splitMap.reversed().forEachIndexed { rawY, row ->
            val yF = rawY / faceSize
            val y = rawY % faceSize
            row.forEachIndexed { rawX, c ->
                val xF = rawX / faceSize
                val x = rawX % faceSize
                val face = faceMap.getOrPut("$xF,$yF") { mutableListOf() }
                if (face.size < y + 1) {
                    face.add(mutableListOf())
                }
                face[y].add(Coord(x, y, c))
            }
        }

        val faces = faceMap.map { it.key to Face(it.key, it.value) }.toMap()

        faces to moves.loadMoves()
    }

    private fun initPart2(faces: Map<String, Face>) {
        // 1 (v)
        faces["1,3"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["0,0"]!!, Dir.E, rY = true, iX = true, sXY = true)
            f.connections[Dir.S] = Connection(faces["1,2"]!!, Dir.S, mY = true)
            f.connections[Dir.E] = Connection(faces["2,3"]!!, Dir.E, rX = true)
            f.connections[Dir.W] = Connection(faces["0,1"]!!, Dir.E, iY = true)
        }
        // 2 (v)
        faces["2,3"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["0,0"]!!, Dir.N, rY = true)
            f.connections[Dir.S] = Connection(faces["1,2"]!!, Dir.W, iX = true, mY = true, sXY = true)
            f.connections[Dir.E] = Connection(faces["1,1"]!!, Dir.W, iY = true)
            f.connections[Dir.W] = Connection(faces["1,3"]!!, Dir.W, mX = true)
        }
        // 3 (v)
        faces["1,2"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["1,3"]!!, Dir.N, rY = true)
            f.connections[Dir.S] = Connection(faces["1,1"]!!, Dir.S, mY = true)
            f.connections[Dir.E] = Connection(faces["2,3"]!!, Dir.N, rX = true, iY = true, sXY = true)
            f.connections[Dir.W] = Connection(faces["0,1"]!!, Dir.S, mX = true, iY = true, sXY = true)
        }
        // 4 (v)
        faces["1,1"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["1,2"]!!, Dir.N, rY = true)
            f.connections[Dir.S] = Connection(faces["0,0"]!!, Dir.W, iX = true, mY = true, sXY = true)
            f.connections[Dir.E] = Connection(faces["2,3"]!!, Dir.W, iY = true)
            f.connections[Dir.W] = Connection(faces["0,1"]!!, Dir.W, mX = true)
        }
        // 5 (v)
        faces["0,1"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["1,2"]!!, Dir.E, iX = true, rY = true, sXY = true)
            f.connections[Dir.S] = Connection(faces["0,0"]!!, Dir.S, mY = true)
            f.connections[Dir.E] = Connection(faces["1,1"]!!, Dir.E, rX = true)
            f.connections[Dir.W] = Connection(faces["1,3"]!!, Dir.E, iY = true)
        }
        // 6 (v)
        faces["0,0"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["0,1"]!!, Dir.N, rY = true)
            f.connections[Dir.S] = Connection(faces["2,3"]!!, Dir.S, mY = true)
            f.connections[Dir.E] = Connection(faces["1,1"]!!, Dir.N, rX = true, iY = true, sXY = true)
            f.connections[Dir.W] = Connection(faces["1,3"]!!, Dir.S, mX = true, iY = true, sXY = true)
        }
    }

    private fun initExample(faces: Map<String, Face>) {
        // faces [0,0] [1,0] [0,2] [1,2] are all blank
        println(faces.keys)
        faces["2,0"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["2,1"]!!, Dir.N, rY = true)
            f.connections[Dir.S] = Connection(faces["0,1"]!!, Dir.N, iX = true)
            f.connections[Dir.E] = Connection(faces["3,0"]!!, Dir.E, rX = true)
            f.connections[Dir.W] = Connection(faces["1,1"]!!, Dir.N, sXY = true)
        }
        faces["3,0"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["2,1"]!!, Dir.W, sXY = true)
            f.connections[Dir.S] = Connection(faces["0,1"]!!, Dir.E, sXY = true)
            f.connections[Dir.E] = Connection(faces["2,2"]!!, Dir.W, iY = true)
            f.connections[Dir.W] = Connection(faces["2,0"]!!, Dir.W, mX = true)
        }
        faces["0,1"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["2,2"]!!, Dir.S, iX = true)
            f.connections[Dir.S] = Connection(faces["2,0"]!!, Dir.N, iX = true)
            f.connections[Dir.E] = Connection(faces["1,1"]!!, Dir.E, rX = true)
            f.connections[Dir.W] = Connection(faces["3,0"]!!, Dir.N, sXY = true)
        }
        faces["1,1"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["2,2"]!!, Dir.E, iX = true, rY = true, sXY = true)
            f.connections[Dir.S] = Connection(faces["2,0"]!!, Dir.E, sXY = true)
            f.connections[Dir.E] = Connection(faces["2,1"]!!, Dir.E, rX = true)
            f.connections[Dir.W] = Connection(faces["0,1"]!!, Dir.W, mX = true)
        }
        faces["2,1"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["2,2"]!!, Dir.N, rY = true)
            f.connections[Dir.S] = Connection(faces["2,0"]!!, Dir.S, mY = true)
            f.connections[Dir.E] = Connection(faces["3,0"]!!, Dir.S, sXY = true)
            f.connections[Dir.W] = Connection(faces["1,1"]!!, Dir.W, mX = true)
        }
        faces["2,2"]!!.let { f ->
            f.connections[Dir.N] = Connection(faces["0,1"]!!, Dir.S, iX = true)
            f.connections[Dir.S] = Connection(faces["2,1"]!!, Dir.S, mY = true)
            f.connections[Dir.E] = Connection(faces["3,0"]!!, Dir.W, iY = true)
            f.connections[Dir.W] = Connection(faces["1,1"]!!, Dir.S, iY = true, mX = true, sXY = true)
        }
    }

    private fun String.loadMoves(): List<Move> {
        var turn: Turn = Turn.R
        var dist = ""
        return mapNotNull { c ->
            if (c.isDigit()) {
                dist += c
                null
            } else {
                Move(turn, dist.toInt()).also {
                    dist = ""
                    turn = Turn.L.takeIf { c == 'L' } ?: Turn.R
                }
            }
        }.plus(Move(turn, dist.toInt()))
    }

    data class Scenario(val map: List<List<Coord>>, val start: Coord, val moves: List<Move>)
    data class Move(val turn: Turn, val dist: Int)

    data class Connection(val face: Face, val dir: Dir,
                          val sXY: Boolean = false,
                          val rX: Boolean = false, val mX: Boolean = false, val iX: Boolean = false,
                          val rY: Boolean = false, val mY: Boolean = false, val iY: Boolean = false) {

    }
    data class Wrap(val coord: Coord, val dir: Dir, val face: Face) {
        fun coordAt(): Coord = face.coords[coord.y][coord.x]
    }

    data class Face(val name: String, val coords: List<List<Coord>>) {

        private val size = coords.size
        private val xOffset = name.first().digitToInt()
        private val yOffset = name.last().digitToInt()
        val connections: MutableMap<Dir, Connection> = mutableMapOf()

        fun wrap(dir: Dir, coord: Coord): Wrap {
            val connection = connections[dir]!!
            val x = if(connection.rX) {
                0
            } else if (connection.mX) {
                size - 1
            } else if(connection.iX) {
                size - 1 - coord.x
            } else {
                coord.x
            }
            val y = if(connection.rY) {
                0
            } else if(connection.mY) {
                size - 1
            } else if (connection.iY) {
                size - 1 - coord.y
            } else {
                coord.y
            }
            val fx = if(connection.sXY) {
                y
            } else {
                x
            }
            val fy = if(connection.sXY) {
                x
            } else {
                y
            }
            //println("Wrap! $dir at $coord fx,fy: [$fx,$fy] c $connection")

            val target = connection.face.coords[fy][fx]
            return Wrap(target, connection.dir, connection.face)
        }

        fun trueCoord(coord: Coord): Coord = Coord(coord.x + (xOffset * size), coord.y + (yOffset * size), coord.d)

        fun trueCoords() = coords.flatMap { row -> row.map { trueCoord(it) } }
    }
}
