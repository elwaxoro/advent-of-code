package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Coord3D
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.bounds

/**
 * Day 18: Boiling Boulders
 */
class Dec18: PuzzleDayTester(18, 2022) {

    /**
     * Pretty simple: for each lava block, count the neighbors that aren't also lava blocks
     */
    override fun part1(): Any = loader().let { lava ->
        lava.sumOf { it.neighbors().minus(lava).size }
    }// == 4242

    /**
     * Just like minecraft! :D
     * Fill the entire space with water blocks using BFS
     * As water expands, check each block's neighbors for lava blocks and already visited water blocks
     * Water blocks can touch multiple rocks at once, but each rock face may only be touched by a single water block
     * Keep track of any water blocks that touch lava
     */
    override fun part2(): Any = loader().let { lava ->
        val bounds = lava.bounds(1) // pad the lava bounds by 1 so water can get all the way around it
        val flooded = mutableSetOf<Coord3D>() // visited blocks
        val contacts = mutableMapOf<Coord3D, Int>() // water blocks that touched lava
        var active = setOf(bounds.min) // water blocks that could still expand
        while(active.isNotEmpty()) {
            active = active.flatMap { waterBlock ->
                val neighbors = waterBlock.neighbors().filter {
                    bounds.contains(it) && !flooded.contains(it)
                }
                val lavaNeighbors = neighbors.filter { lava.contains(it) }
                if (lavaNeighbors.isNotEmpty()) {
                    contacts[waterBlock] = lavaNeighbors.size
                }
                flooded.add(waterBlock)
                neighbors.minus(lavaNeighbors)
            }.toSet()
            // println("loop complete. flooded: ${flooded.size} active: ${active.size} rock touches: ${contacts.map { it.value }.sum()}")
        }
        contacts.map { it.value }.sum()
    }// == 2428

    private fun loader() = load().map(Coord3D::parse).toSet()
}
