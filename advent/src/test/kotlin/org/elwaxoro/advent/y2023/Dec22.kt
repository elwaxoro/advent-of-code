package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.Bounds3D
import org.elwaxoro.advent.Coord3D
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Sand Slabs
 *
 * z: height
 */
class Dec22 : PuzzleDayTester(22, 2023) {

    /**
     * 451
     */
    override fun part1(): Any = loader().map { it.enumerateCube().toSet() }.let { cubes ->
        // first: let everything collapse as far down as it will go
        val collapsed = cubes.collapse().first
        // second: for each cube, disintegrate it and count it if nothing else falls
        collapsed.filter { cube -> collapsed.minusElement(cube).collapse().second == 0 }.size
    }

    /**
     * 66530
     */
    override fun part2() = loader().map { it.enumerateCube().toSet() }.let { cubes ->
        // first: let everything collapse as far down as it will go
        val collapsed = cubes.collapse().first
        // second: for each cube, disintegrate it and count everything that falls
        collapsed.sumOf { cube -> collapsed.minusElement(cube).collapse().second }
    }

    private fun Collection<Set<Coord3D>>.collapse(): Pair<List<Set<Coord3D>>, Int> {
        val collapsedCubes = mutableListOf<Set<Coord3D>>()
        val collapsedCoords = mutableSetOf<Coord3D>()
        var movedCount = 0
        // sort cubes by height (lowest first), then move it down until it falls off the bottom or bumps into something
        sortedBy { cube -> cube.minOf { it.z } }.forEach { cube ->
            var current = cube
            var keepGoing = true
            while (keepGoing) {
                val move = current.map { it.copy(z = it.z - 1) }.toSet()
                if (move.all { it.z >= 1 } && move.none { it in collapsedCoords }) {
                    // keep falling
                    current = move
                } else {
                    // smacked into something
                    collapsedCubes.add(current)
                    collapsedCoords.addAll(current)

                    // this cube actually fell
                    if (cube != current) {
                        movedCount++
                    }
                    keepGoing = false
                }
            }
        }
        return collapsedCubes to movedCount
    }

    private fun loader() = load().map { line ->
        val (a, b) = line.split("~")
        Bounds3D(Coord3D.parse(a), Coord3D.parse(b))
    }
}
