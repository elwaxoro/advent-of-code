package org.elwaxoro.advent

import kotlin.math.*

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
            when (uplr) {
                'U' -> N
                'D' -> S
                'L' -> W
                'R' -> E
                else -> throw UnsupportedOperationException("Can't turn $uplr from UDLR to NSWE!")
            }

        fun fromCarets(caret: Char): Dir =
            when (caret) {
                '^' -> N
                'v' -> S
                '>' -> E
                '<' -> W
                else -> throw UnsupportedOperationException("Can't turn $caret from ^v>< to NSWE!")
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
                Turn.A -> N
            }

            S -> when (t) {
                Turn.R -> W
                Turn.L -> E
                Turn.A -> S
            }

            E -> when (t) {
                Turn.R -> S
                Turn.L -> N
                Turn.A -> E
            }

            W -> when (t) {
                Turn.R -> N
                Turn.L -> S
                Turn.A -> W
            }
        }

    fun toChar(): Char = when (this) {
        N -> 'N'
        S -> 'S'
        E -> 'E'
        W -> 'W'
    }

    /**
     * Use invert when top left is 0,0 so going "south" means decreasing Y which means going "up"
     */
    fun toCaret(invert: Boolean = false): Char =
        if (invert) {
            when (this) {
                N -> 'v'
                S -> '^'
                E -> '>'
                W -> '<'
            }
        } else {
            when (this) {
                N -> '^'
                S -> 'v'
                E -> '>'
                W -> '<'
            }
        }
}

enum class Turn {
    R,
    L,
    A // straight ahead
}

/**
 * 2D coordinate
 * Note: these coords are immutable, any mutating call return a new Coord with the changes leaving the original untouched
 * Note2: these coords are x,y which works well for 2D graph / coordinate math but doesn't map well for row/column uses which should be thought of as y,x
 * Note3: when thinking about images, keep in mind where your 0,0 is (top left or bottom left) and where your y+1 direction needs to go (up or down)
 * Note4: if using different d (display char) equals and hash code will NOT work!
 */
data class Coord(val x: Int = 0, val y: Int = 0, val d: Char? = null) {
    companion object {
        /**
         * "x,y" string to coord
         */
        fun parse(str: String) = str.replace("(", "").replace(")", "").split(",").map { it.trim() }.let {
            Coord(it[0].toInt(), it[1].toInt())
        }
    }

    /**
     * Copy, but with new d (display char)
     */
    fun copyD(n: Char? = null): Coord = Coord(x, y, n)

    override fun toString(): String = "($x,$y)"

    fun move(dir: Dir, distance: Int = 1, md: Char? = null): Coord =
        when (dir) {
            Dir.N -> Coord(x, y + distance, md ?: d)
            Dir.S -> Coord(x, y - distance, md ?: d)
            Dir.E -> Coord(x + distance, y, md ?: d)
            Dir.W -> Coord(x - distance, y, md ?: d)
        }

    fun add(dx: Int, dy: Int): Coord =
        Coord(x + dx, y + dy, d)

    fun add(dxy: Coord): Coord =
        Coord(x + dxy.x, y + dxy.y, d)

    fun subtract(dxy: Coord): Coord =
        Coord(x - dxy.x, y - dxy.y, d)

    /**
     * List of neighbors in cardinal directions only [N,S,E,W], excluding this coord
     */
    fun neighbors(): List<Coord> =
        Dir.entries.map { move(it) }

    /**
     * Returns a 3x3 grid with all neighbor coords, including this coord at the center if includeSelf is set
     */
    fun neighbors9(includeSelf: Boolean = true): List<List<Coord>> =
        listOf(
            listOf(Coord(x - 1, y - 1), Coord(x, y - 1), Coord(x + 1, y - 1)),
            listOfNotNull(Coord(x - 1, y), this.takeIf { includeSelf }, Coord(x + 1, y)),
            listOf(Coord(x - 1, y + 1), Coord(x, y + 1), Coord(x + 1, y + 1))
        )

    /**
     * Gets the direction of an adjacent Coord
     */
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

    /**
     * Simplistic rotations for multiples of 90 degrees only
     */
    fun rotate(rotation: Int): Coord =
        when (rotation % 360) {
            0 -> this
            90 -> Coord(y, x * -1, d)
            180 -> Coord(x * -1, y * -1, d)
            270 -> Coord(y * -1, x, d)
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
                Coord(mx, my, d)
            }
        }.flatten()
    }

    /**
     * With this coord at the center, get all coords within a rectangle where x or y is dist away
     * (corners will be x+dist, y+dist and x-dist, y-dist)
     */
    fun enumerateRectangle(dist: Int): List<Coord> = add(dist, dist).enumerateRectangle(add(dist * -1, dist * -1))

    /**
     * Return the list of all coords in a line from this coord to the target coord, including both start and end coords
     * Coords are in order from this to toCoord
     * ex (0,0) to (5,-5) produces: [(0,0), (1,-1), (2,-2), (3,-3), (4,-4), (5,-5)]
     * Only produces straight lines for vertical, horizontal, and 45deg lines
     * ex (0,0) to (5,2) produces: [(0,0), (1,1), (2,2), (3,2), (4,2), (5,2)]
     */
    fun enumerateLine(toCoord: Coord): List<Coord> =
        (abs(x - toCoord.x) + 1 to abs(y - toCoord.y) + 1).let { (dx, dy) ->
            x.toward(toCoord.x).padTo(dy).zip(y.toward(toCoord.y).padTo(dx)).map { (x, y) ->
                Coord(x, y, d)
            }
        }

    /**
     * Pythagoras figured this out so we don't have to
     */
    fun distance(to: Coord): Double = sqrt((to.x - x).toDouble().pow(2) + (to.y - y).toDouble().pow(2))

    /**
     * Manhattan distance / taxicab distance
     * https://en.wikipedia.org/wiki/Taxicab_geometry
     */
    fun taxiDistance(to: Coord): Int = abs(to.x - x) + abs(to.y - y)

    /**
     * Angle from this coord to that coord, in degrees
     * 0 degrees is to the right, increasing clockwise from there
     */
    fun angleTo(that: Coord): Double {
        val angle = Math.toDegrees(atan2(that.y.toDouble() - y, that.x.toDouble() - x))
        return if (angle < 0) {
            angle + 360
        } else {
            angle
        }
    }

    fun slopeTo(that: Coord): Double = (that.y - y) / (that.x.toDouble() - x)

    /**
     * Equals check that ignores 'd' and just compares x,y values
     */
    fun equalsCoord(coord: Coord) = x == coord.x && y == coord.y

    /**
     * Gets the cardinal direction towards the other coord
     * If other coord is not exactly in any direction, an exception is thrown
     * If other coord is at the same position, an exception is thrown
     */
    fun dirTo(that: Coord): Dir =
        if (x == that.x && y == that.y) {
            throw IllegalStateException("No direction possible: $this == $that ")
        } else if (x == that.x) {
            if (y < that.y) {
                Dir.N
            } else {
                Dir.S
            }
        } else if (y == that.y) {
            if (x < that.x) {
                Dir.E
            } else {
                Dir.W
            }
        } else {
            throw IllegalStateException("No direction possible: $this vs $that")
        }
}

/**
 * "x,y" formatted string to a Coord(x, y)
 */
fun String.toCoord(): Coord = Coord.parse(this)

/**
 * Contains check that ignores 'd' and just compares x,y values
 */
fun Iterable<Coord>.containsCoord(coord: Coord) = any { it.x == coord.x && it.y == coord.y }

fun Iterable<Coord>.copyD(c: Char? = null) = map { it.copyD(c) }

/**
 * Creates an in-order list of all coordinates visited by drawing a line from each Coord to the next
 * ex: [(0,0), (2,0), (5,5)] -> [(0,0), (1,0), (2,0), (3,1), (4,2), (5,3), (5,4), (5,5)]
 * Note: duplicates ARE maintained! If line segments intersect, all passes through that coordinate are maintained
 * Can be useful for testing if a path doubles back on itself (and where)
 */
fun List<Coord>.enumerateLines(): List<Coord> = zipWithNext { a, b -> a.enumerateLine(b).dropLast(1) }.flatten().plus(last())

/**
 * Creates a set of every Coord intersected by two List<Coord> (path)
 * Optionally, provide a set of Coords to ignore (ex: if both paths start at the same point, but you don't want that to count)
 * Note: no duplicate intersections (set)
 */
fun List<Coord>.intersections(that: List<Coord>, filterCoords: Set<Coord> = emptySet()): Set<Coord> = enumerateLines().toSet().intersect(that.enumerateLines().toSet()).minus(filterCoords)

/**
 * Extension of getOrNull that returns the input row and col as a Coord, along with the item if found
 * IMPORTANT! if thinking of the list of lists as a coordinate grid, row = y and col = x
 */
fun <T> List<List<T>>.getCoordOrNull(row: Int, col: Int): Pair<Coord, T>? =
    getOrNull(row, col)?.let { Pair(Coord(col, row), it) }

/**
 * Extension of neighbors that returns the neighbors as a Coord along with the item if found
 * IMPORTANT! if thinking of the list of lists as a coordinate grid, row = y and col = x
 */
fun <T> List<List<T>>.neighborCoords(row: Int, col: Int, includeDiagonal: Boolean = false): List<Pair<Coord, T>> =
    listOfNotNull(
        getCoordOrNull(row - 1, col),
        getCoordOrNull(row + 1, col),
        getCoordOrNull(row, col - 1),
        getCoordOrNull(row, col + 1),
        getCoordOrNull(row - 1, col - 1).takeIf { includeDiagonal },
        getCoordOrNull(row - 1, col + 1).takeIf { includeDiagonal },
        getCoordOrNull(row + 1, col - 1).takeIf { includeDiagonal },
        getCoordOrNull(row + 1, col + 1).takeIf { includeDiagonal },
    )

/**
 * Returns true if given coord is contained by the rectangle formed by the pair of coordinates
 */
fun Pair<Coord, Coord>.contains(c: Coord): Boolean {
    val xs = listOf(first.x, second.x).sorted()
    val ys = listOf(first.y, second.y).sorted()
    return c.x >= xs.first() && c.x <= xs.last() && c.y >= ys.first() && c.y <= ys.last()
}

/**
 * Creates two Coords from a collection to describe the corners of a rectangle that contain all the Coords
 */
fun Collection<Coord>.bounds(): Pair<Coord, Coord> {
    val xs = map { it.x }.sorted()
    val ys = map { it.y }.sorted()
    return (Coord(xs.first(), ys.first()) to Coord(xs.last(), ys.last()))
}

fun Iterable<Coord>.maxX(): Int = maxOf { it.x }
fun Iterable<Coord>.maxY(): Int = maxOf { it.y }
fun Iterable<Coord>.minX(): Int = minOf { it.x }
fun Iterable<Coord>.minY(): Int = minOf { it.y }

/**
 * Creates a visual representation of collection of Coords, required for some puzzles
 * If there are duplicates, last in wins
 * NOTE: default print behavior is "top down" where (0,0) is top left and positive Y values go down the page.
 * For "bottom up" where (0,0) is bottom left, set invert = true
 * ex:
 * [0,0] to [38,5]
 *    ##  #  #  ##   ##  ###   ##   ##  #  #
 *   #  # #  # #  # #  # #  # #  # #  # #  #
 *   #  # #### #    #    #  # #    #  # #  #
 *   #### #  # # ## #    ###  # ## #### #  #
 *   #  # #  # #  # #  # #    #  # #  # #  #
 *   #  # #  #  ###  ##  #     ### #  #  ##
 */
fun Collection<Coord>.printify(full: Char = '#', empty: Char = '.', invert: Boolean = false): String {
    val xs = map { it.x }.sorted()
    val ys = map { it.y }.sorted()
    val xtranslate = 0 - xs.first()
    val ytranslate = 0 - ys.first()

    return "[${xs.first()},${ys.first()}] to [${xs.last()},${ys.last()}]\n" +
            (0..(ys.last() - ys.first())).map {
                MutableList(xs.last() - xs.first() + 1) { empty }
            }.also { screen ->
                forEach { coord ->
                    screen[coord.y + ytranslate][coord.x + xtranslate] = coord.d ?: full
                }
            }.let {
                if (invert) {
                    it.reversed()
                } else {
                    it
                }
            }.joinToString("\n") { it.joinToString("") }
}

/**
 * Parses a list of Strings into a set of Coords with each char of the string stored inside
 * Set filter to chars that should not produce a Coord (usually '.')
 * Set blank to skip setting the char into the Coord (useful with filter, where "blank" coords are not needed)
 * Each String is treated as a y row starting with the first row as y=0
 * Top left will be 0,0
 * Use invert to have the last String be y=0, bottom left will be 0,0
 * Invert is useful when working with N,S,E,W to have Y increase while going "up"
 */
fun List<String>.parseCoords(invert: Boolean = false, filter: Set<Char> = setOf(), blank: Boolean = false): Set<Coord> = flatMapIndexed { r, s ->
    val y = r.takeUnless { invert } ?: (size - r + 1)
    s.mapIndexedNotNull { x, d ->
        Coord(x, y, d.takeUnless { blank }).takeUnless { filter.contains(d) }
    }
}.toSet()

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

    fun neighbors(): List<Hex> = HexDir.entries.map { move(it) }
}

/**
 * The 'w' is for WTF
 * Based on javax.vecmath.Tuple4d
 */
data class Coord3D(val x: Int = 0, val y: Int = 0, val z: Int = 0, val w: Int = 1) {
    companion object {
        fun parse(string: String): Coord3D = string.split(",").map { it.trim() }.let { (a, b, c) ->
            Coord3D(a.toInt(), b.toInt(), c.toInt())
        }
    }

    fun add(dx: Int, dy: Int, dz: Int): Coord3D = Coord3D(x + dx, y + dy, z + dz, w)
    fun add(that: Coord3D): Coord3D = Coord3D(x + that.x, y + that.y, z + that.z, w)
    fun subtract(that: Coord3D): Coord3D = Coord3D(x - that.x, y - that.y, z - that.z, w)
    fun manhattan(that: Coord3D): Int = abs(x - that.x) + abs(y - that.y) + abs(z - that.z)
    fun toMatrix(): Matrix4 = Matrix4(
        1, 0, 0, x,
        0, 1, 0, y,
        0, 0, 1, z,
        0, 0, 0, w
    )

    fun neighbors(): Set<Coord3D> = setOf(
        Coord3D(x - 1, y, z),
        Coord3D(x + 1, y, z),
        Coord3D(x, y + 1, z),
        Coord3D(x, y - 1, z),
        Coord3D(x, y, z + 1),
        Coord3D(x, y, z - 1),
    )
}

fun Collection<Coord3D>.bounds(pad: Int = 0): Bounds3D {
    val maxX = maxOf { it.x } + pad
    val maxY = maxOf { it.y } + pad
    val maxZ = maxOf { it.z } + pad
    val minX = minOf { it.x } - pad
    val minY = minOf { it.y } - pad
    val minZ = minOf { it.z } - pad
    return Bounds3D(Coord3D(minX, minY, minZ), Coord3D(maxX, maxY, maxZ))
}

/**
 * Basically Pair<Coord3D, Coord3D>
 */
data class Bounds3D(val min: Coord3D, val max: Coord3D) {
    init {
        check(min.x <= max.x) { "X: ${min.x} must be <= ${max.x} [$this]" }
        check(min.y <= max.y) { "Y: ${min.y} must be <= ${max.y} [$this]" }
        check(min.z <= max.z) { "Z: ${min.z} must be <= ${max.z} [$this]" }
    }

    fun size(): Long = abs(max.x - min.x).toLong() * abs(max.y - min.y) * abs(max.z - min.z)

    fun contains(coord: Coord3D): Boolean =
        coord.x <= max.x && coord.x >= min.x
                && coord.y <= max.y && coord.y >= min.y
                && coord.z <= max.z && coord.z >= min.z

    // TODO verify
    fun intersects(that: Bounds3D): Boolean = contains(that.min) || contains(that.max) || that.contains(min) || that.contains(max)

    // TODO verify
    fun intersection(that: Bounds3D): Bounds3D =
        Bounds3D(
            min = Coord3D(max(min.x, that.min.x), max(min.y, that.min.y), max(min.z, that.min.z)),
            max = Coord3D(min(max.x, that.max.x), min(max.y, that.max.y), min(max.z, that.max.z))
        )

    // TODO verify
    fun enumerateCube(): List<Coord3D> =
        (min.x..max.x).flatMap { x ->
            (min.y..max.y).flatMap { y ->
                (min.z..max.z).map { z ->
                    Coord3D(x, y, z)
                }
            }
        }
}

/**
 * javax.vecmath.Matrix4f / javax.media.j3d.Transform3D
 */
data class Matrix4(
    val m00: Int, val m10: Int, val m20: Int, val m30: Int,
    val m01: Int, val m11: Int, val m21: Int, val m31: Int,
    val m02: Int, val m12: Int, val m22: Int, val m32: Int,
    val m03: Int, val m13: Int, val m23: Int, val m33: Int,
) {
    fun multiply(m: Matrix4): Matrix4 = Matrix4(
        m00 * m.m00 + m10 * m.m01 + m20 * m.m02 + m30 * m.m03,
        m00 * m.m10 + m10 * m.m11 + m20 * m.m12 + m30 * m.m13,
        m00 * m.m20 + m10 * m.m21 + m20 * m.m22 + m30 * m.m23,
        m00 * m.m30 + m10 * m.m31 + m20 * m.m32 + m30 * m.m33,

        m01 * m.m00 + m11 * m.m01 + m21 * m.m02 + m31 * m.m03,
        m01 * m.m10 + m11 * m.m11 + m21 * m.m12 + m31 * m.m13,
        m01 * m.m20 + m11 * m.m21 + m21 * m.m22 + m31 * m.m23,
        m01 * m.m30 + m11 * m.m31 + m21 * m.m32 + m31 * m.m33,

        m02 * m.m00 + m12 * m.m01 + m22 * m.m02 + m32 * m.m03,
        m02 * m.m10 + m12 * m.m11 + m22 * m.m12 + m32 * m.m13,
        m02 * m.m20 + m12 * m.m21 + m22 * m.m22 + m32 * m.m23,
        m02 * m.m30 + m12 * m.m31 + m22 * m.m32 + m32 * m.m33,

        m03 * m.m00 + m13 * m.m01 + m23 * m.m02 + m33 * m.m03,
        m03 * m.m10 + m13 * m.m11 + m23 * m.m12 + m33 * m.m13,
        m03 * m.m20 + m13 * m.m21 + m23 * m.m22 + m33 * m.m23,
        m03 * m.m30 + m13 * m.m31 + m23 * m.m32 + m33 * m.m33,
    )

    fun multiply(c: Coord3D): Coord3D = Coord3D(
        m00 * c.x + m10 * c.y + m20 * c.z + m30 * 1,
        m01 * c.x + m11 * c.y + m21 * c.z + m31 * 1,
        m02 * c.x + m12 * c.y + m22 * c.z + m32 * 1,
        m03 * c.x + m13 * c.y + m23 * c.z + m33 * 1,
    )

    override fun toString(): String =
        """
            $m00,$m10,$m20,$m30
            $m01,$m11,$m21,$m31
            $m02,$m12,$m22,$m32
            $m03,$m13,$m23,$m33
        """.trimIndent()
}

/**
 * Some basic 90 degree matrix rotations
 */
enum class Rotation4(val matrix: Matrix4) {
    IDENTITY(
        Matrix4(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        )
    ),
    X90(
        Matrix4(
            1, 0, 0, 0,
            0, 0, -1, 0,
            0, 1, 0, 0,
            0, 0, 0, 1
        )
    ),
    Y90(
        Matrix4(
            0, 0, 1, 0,
            0, 1, 0, 0,
            -1, 0, 0, 0,
            0, 0, 0, 1
        )
    ),
    Z90(
        Matrix4(
            0, -1, 0, 0,
            1, 0, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        )
    );

    companion object {
        /**
         * All possible combined 90 degree rotations of a matrix
         * This makes 64 rotations but only 24 of them are unique. That's called efficiency
         */
        fun allTheThings(): Set<Matrix4> =
            (0..3).map { x ->
                (0..3).map { y ->
                    (0..3).map { z ->
                        List(x) { X90.matrix } + List(y) { Y90.matrix } + List(z) { Z90.matrix }
                    }
                }.flatten()
            }.flatten().filter { it.isNotEmpty() }.map {
                it.reduce { acc, rotation4 ->
                    acc.multiply(rotation4)
                }
            }.toSet()
    }
}

operator fun <E> List<List<E>>.get(coord: Coord) = this[coord.y][coord.x]

operator fun <E> Collection<Collection<E>>.contains(coord: Coord): Boolean =
    this.isNotEmpty() && coord.y in this.indices && coord.x in this.first().indices

data class LCoord(val x: Long, val y: Long) {
    fun taxiDistance(to: LCoord): Long = abs(to.x - x) + abs(to.y - y)

    fun move(dir: Dir, distance: Long = 1): LCoord =
        when (dir) {
            Dir.N -> LCoord(x, y + distance)
            Dir.S -> LCoord(x, y - distance)
            Dir.E -> LCoord(x + distance, y)
            Dir.W -> LCoord(x - distance, y)
        }
}

/**
 * Area of a simple polygon
 * NOTE: coords must be in a positively oriented (counterclockwise) order to guarantee accuracy
 * NOTE2: only used this for one puzzle and had to add + 2 to the output to get the right answer
 * https://en.wikipedia.org/wiki/Shoelace_formula
 */
fun List<LCoord>.shoelaceArea(): Double {
    val n = size
    var a = 0.0
    for (i in 0 until n - 1) {
        a += this[i].x * this[i + 1].y - this[i + 1].x * this[i].y
    }
    return abs(a + this[n - 1].x * this[0].y - this[0].x * this[n - 1].y) / 2.0
}

/**
 * Points on the interior + half the boundary -1 = total points
 * https://en.wikipedia.org/wiki/Pick%27s_theorem
 */
fun picksTheorem(interior: Double, boundary: Double): Double = interior + (boundary / 2.0) - 1