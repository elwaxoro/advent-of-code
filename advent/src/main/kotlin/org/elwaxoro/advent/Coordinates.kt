package org.elwaxoro.advent

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Cardinal directions
 */
enum class Dir {
    N, S, E, W;

    companion object {
        /**
         * Up, down, left, right to N, S, E, W
         */
        fun fromUDLR(uplr: Char): Dir =
            when(uplr) {
                'U' -> N
                'D' -> S
                'L' -> W
                'R' -> E
                else -> throw UnsupportedOperationException("Can't turn $uplr from UDLR to NSWE!")
            }
    }

    /**
     * Turn left or right, get a new Dir
     */
    fun turn(t: Turn): Dir =
        when (this) {
            N -> when (t) {
                Turn.R -> E
                Turn.L -> W
            }
            S -> when (t) {
                Turn.R -> W
                Turn.L -> E
            }
            E -> when (t) {
                Turn.R -> S
                Turn.L -> N
            }
            W -> when (t) {
                Turn.R -> N
                Turn.L -> S
            }
        }
}

enum class Turn {
    R,
    L
}

/**
 * 2D coordinate
 */
data class Coord(val x: Int = 0, val y: Int = 0) {

    companion object {
        /**
         * "x,y" string to coord
         */
        fun parse(str: String) = str.split(",").let {
            Coord(it[0].toInt(), it[1].toInt())
        }
    }

    fun move(dir: Dir, distance: Int = 1): Coord =
        when (dir) {
            Dir.N -> Coord(x, y + distance)
            Dir.S -> Coord(x, y - distance)
            Dir.E -> Coord(x + distance, y)
            Dir.W -> Coord(x - distance, y)
        }

    fun translate(dxy: Coord): Coord =
        Coord(x + dxy.x, y + dxy.y)

    fun neighbors(): List<Coord> =
        Dir.values().map { move(it) }

    fun edge(that: Coord): Dir =
        if (x == that.x) {
            if (y == that.y - 1) {
                Dir.N
            } else {
                Dir.S
            }
        } else if (y == that.y) {
            if (x == that.x - 1) {
                Dir.E
            } else {
                Dir.W
            }
        } else {
            throw IllegalStateException("Coord $that is not adjacent to $this")
        }

    fun rotate(rotation: Int): Coord =
        when (rotation) {
            0 -> this
            90 -> Coord(y, x * -1)
            180 -> Coord(x * -1, y * -1)
            270 -> Coord(y * -1, x)
            else -> throw IllegalStateException("Coord $this does not support rotation $rotation")
        }

    /**
     * Return the list of all coords in a rectangle from this coord to the target coord, treating the two coords as corners
     * ex: (0,0).enumerate(2,2) returns
     * [Coord(x=0, y=0), Coord(x=0, y=1), Coord(x=0, y=2), Coord(x=1, y=0), Coord(x=1, y=1), Coord(x=1, y=2), Coord(x=2, y=0), Coord(x=2, y=1), Coord(x=2, y=2)]
     */
    fun enumerateRectangle(toCoord: Coord): List<Coord> {
        val xs = listOf(x, toCoord.x).sorted()
        val ys = listOf(y, toCoord.y).sorted()
        return (xs[0]..xs[1]).map { mx ->
            (ys[0]..ys[1]).map { my ->
                Coord(mx, my)
            }
        }.flatten()
    }

    /**
     * Pythagoras figured this out so we don't have to
     */
    fun distance(to: Coord): Double = sqrt((to.x - x).toDouble().pow(2) + (to.y - y).toDouble().pow(2))

    /**
     * https://en.wikipedia.org/wiki/Taxicab_geometry
     */
    fun taxiDistance(to: Coord): Int = abs(to.x - x) + abs(to.y - y)
}

fun Pair<Coord, Coord>.contains(c: Coord): Boolean {
    val xs = listOf(first.x, second.x).sorted()
    val ys = listOf(first.y, second.y).sorted()
    return c.x >= xs.first() && c.x <= xs.last() && c.y >= ys.first() && c.y <= ys.last()
}

enum class HexDir { E, W, NE, NW, SE, SW }

/**
 * Hex coordinates based on https://www.redblobgames.com/grids/hexagons/
 * Uses cube coordinates
 */
data class Hex(val x: Int, val y: Int, val z: Int) {

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