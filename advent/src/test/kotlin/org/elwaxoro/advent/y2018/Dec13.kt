package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.*

/**
 * Day 13: Mine Cart Madness
 */
class Dec13 : PuzzleDayTester(13, 2018) {

    /**
     * a little janky because my coord code w/ directions and turns expects 0,0 to be bottom left instead of top left
     */
    override fun part1(): Any = loader().let { (carts, map) ->
//        println(map.map { it.key.copyD(it.value) }.plus(carts.map { it.coord.copyD(it.heading.toString()[0]) }).printify(empty = ' ', invert = true))
        val cartSet = carts.map { it.coord }.toMutableSet()
        val bounds = map.keys.bounds()
        var firstCrash: Coord? = null
        while (firstCrash == null) {
            runCarts(carts, cartSet, map) {
                if (firstCrash == null) {
                    firstCrash = Coord(it.x, bounds.second.y - it.y)
                }
            }
        }
        firstCrash!!
    }

    override fun part2(): Any = loader().let { (carts, map) ->
        val cartSet = carts.map { it.coord }.toMutableSet()
        val bounds = map.keys.bounds()
        while (cartSet.size > 1) {
            runCarts(carts, cartSet, map)
        }
        cartSet.single().let { Coord(it.x, bounds.second.y - it.y) }
    }

    private fun runCarts(carts: List<Cart>, unCrashedCarts: MutableSet<Coord>, map: Map<Coord, Char>, crashHandler: (coord: Coord) -> Unit = {}) {
        carts.sorted().forEach { cart ->
            // pull the cart out of the set, move it, put it back if it doesn't crash
            unCrashedCarts.remove(cart.coord)
            if (!cart.crashed) {
                cart.move()
                if (unCrashedCarts.contains(cart.coord)) {
                    // crash! another un-crashed cart is in the spot the cart just moved into
                    cart.crashed = true
                    carts.filter { it.coord == cart.coord }.map { it.crashed = true }
                    unCrashedCarts.remove(cart.coord)
                    crashHandler.invoke(cart.coord)
                } else {
                    unCrashedCarts.add(cart.coord)
                    cart.orient(map.getValue(cart.coord))
                }
            }
        }
    }

    private fun loader() = load().let { lines ->
        val carts = mutableListOf<Cart>()
        val map = mutableMapOf<Coord, Char>()
        lines.forEachIndexed { iy, row ->
            val y = lines.size - iy - 1
            row.forEachIndexed { x, c ->
                when (c) {
                    '>' -> {
                        map[Coord(x, y)] = '-'
                        carts.add(Cart(Coord(x, y), Dir.E))
                    }

                    '<' -> {
                        map[Coord(x, y)] = '-'
                        carts.add(Cart(Coord(x, y), Dir.W))
                    }

                    '^' -> {
                        map[Coord(x, y)] = '|'
                        carts.add(Cart(Coord(x, y), Dir.N))
                    }

                    'v' -> {
                        map[Coord(x, y)] = '|'
                        carts.add(Cart(Coord(x, y), Dir.S))
                    }

                    ' ' -> {} // ignore empty spots
                    else -> map[Coord(x, y)] = c // standard map tile
                }
            }
        }
        carts to map
    }

    private data class Cart(
        var coord: Coord,
        var heading: Dir,
        var nextTurn: Turn = Turn.L,
        var crashed: Boolean = false
    ) : Comparable<Cart> {

        fun move() {
            coord = coord.move(heading)
        }

        fun orient(tile: Char) {
            when (tile) {
                '-', '|' -> {} // do nothing, keep going
                '+' -> {
                    heading = heading.turn(nextTurn)
                    nextTurn = when (nextTurn) {
                        Turn.L -> Turn.A
                        Turn.A -> Turn.R
                        Turn.R -> Turn.L
                    }
                }

                '/' -> {
                    heading = when (heading) {
                        Dir.N -> Dir.E
                        Dir.S -> Dir.W
                        Dir.E -> Dir.N
                        Dir.W -> Dir.S
                    }
                }

                '\\' -> {
                    heading = when (heading) {
                        Dir.N -> Dir.W
                        Dir.S -> Dir.E
                        Dir.E -> Dir.S
                        Dir.W -> Dir.N
                    }
                }

                else -> throw IllegalStateException("unknown map tile '$tile'")
            }
        }

        /**
         * comparison favors y, then x, then d (if any)
         */
        override fun compareTo(other: Cart): Int =
            coord.y.compareTo(other.coord.y) * -100 + coord.x.compareTo(other.coord.x)
    }
}
