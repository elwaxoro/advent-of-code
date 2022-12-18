package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Coord3D
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.bounds

class Dec18: PuzzleDayTester(18, 2022) {
    override fun part1(): Any = loader().let { coords ->
        coords.sumOf { coord ->
            6 - coords.count { that -> coord.manhattan(that) == 1 }
        }
    }// == 4242

    override fun part2(): Any = loader().let { lavaCoords ->
        val bounds = lavaCoords.bounds(1)
        val flooded = mutableSetOf<Coord3D>()
        val contacts = mutableMapOf<Coord3D, Int>()
        var active = setOf(bounds.min)
        while(active.isNotEmpty()) {
            active = active.flatMap { coord ->
                val potential = coord.neighbors().filter {
                    bounds.contains(it) && !flooded.contains(it)
                }
                val rocks = potential.filter { lavaCoords.contains(it) }
                if (rocks.isNotEmpty()) {
                    contacts[coord] = rocks.size
                }
                flooded.add(coord)
                potential.minus(rocks)
            }.toSet()
            println("loop complete. flooded: ${flooded.size} active: ${active.size} rock touches: ${contacts.map { it.value }.sum()}")
        }
        contacts.map { it.value }.sum()
    }// == 2428

    private fun loader() = load().map(Coord3D::parse).toSet()
}
