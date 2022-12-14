package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify

/**
 * Day 14: Regolith Reservoir
 */
class Dec14: PuzzleDayTester(14, 2022) {

    override fun part1(): Any = loader().let { rocks ->
        val source = Coord(500, 0, '+')
        println(rocks.plus(source).printify())
        val maxY = rocks.maxOf { it.y }
        val sand = mutableSetOf<Coord>()
        var sandFellOut = false
        val both = rocks.toMutableSet()
        println(both)
        println("maxY: $maxY")
        while(!sandFellOut) {
            var grain = source.copyD(null)
            var grainActive = true
            while(grainActive) {
                val down = grain.add(0, 1)
                val left = grain.add(-1, 1)
                val right = grain.add(1, 1)
                if(both.contains(down)) {
                    if(both.contains(left)) {
                        if(both.contains(right)) {
                            both.add(grain)
                            sand.add(grain)
                            grainActive = false
                        } else {
                            grain = right
                        }
                    } else {
                        grain = left
                    }
                } else {
                    grain = down
                }
                if(grain.y >= maxY) {
                    sandFellOut = true
                    grainActive = false
                }
            }
        }
        println(rocks.plus(sand.map { it.copyD('o') }).plus(source).printify())
        sand.size
    }

    override fun part2(): Any = loader().addFloor().let { rocks ->
        val source = Coord(500, 0)
        println(rocks.plus(source).printify())
        val maxY = rocks.maxOf { it.y }
        val sand = mutableSetOf<Coord>()
        var sandFellOut = false
        val both = rocks.toMutableSet()
        println(both)
        println("maxY: $maxY")
        while(!sandFellOut) {
            var grain = source.copyD(null)
            var grainActive = true
            while(grainActive) {
                val down = grain.add(0, 1)
                val left = grain.add(-1, 1)
                val right = grain.add(1, 1)
                if(both.contains(down)) {
                    if(both.contains(left)) {
                        if(both.contains(right)) {
                            both.add(grain)
                            sand.add(grain)
                            grainActive = false
                            if(grain == source) {
                                // full to the brim!
                                sandFellOut = true
                            }
                        } else {
                            grain = right
                        }
                    } else {
                        grain = left
                    }
                } else {
                    grain = down
                }
                if(grain.y >= maxY) {
                    sandFellOut = true
                    grainActive = false
                }
            }
        }
        println(rocks.plus(sand.map { it.copyD('o') }).printify())
        sand.size
    }

//    private fun Set<Coord>.dropSand(): Int = this.let { rocks ->
//
//    }

    private fun Set<Coord>.addFloor(): Set<Coord> {
        val maxY = maxOf { it.y }
        return this.plus((500-maxY-5..500+maxY+5).map { Coord(it, maxY+2) }).toSet()
    }

    private fun loader() = load().map { it.split(" -> ").map(Coord::parse).fold(listOf<Coord>()) { acc, coord ->
        if(acc.isEmpty()) {
            acc.plus(coord)
        } else {
            acc.plus(acc.last().enumerateLine(coord))
        }
    }}.flatten().toSet()
}