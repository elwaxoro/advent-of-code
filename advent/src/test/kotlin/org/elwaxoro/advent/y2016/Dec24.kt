package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.permutations

/**
 * Day 24: Air Duct Spelunking
 *
 * idea: this sounds a lot like all pairs shortest path followed by some set organization
 * gather the shortest distance from each number to every other number
 * brute force all possible paths thru every number and take the shortest one
 */
class Dec24 : PuzzleDayTester(24, 2016) {

    override fun part1(): Any = loader().let { input ->
        val targets = input.filter { it.d != '.' }
        val coords = input.map { it.copyD() }.toSet()
        val distances = buildDistances(targets, coords)
        val choices = targets.map { it.d!! }.permutations().filter { it.first() == '0' }
        choices.minOf { path -> path.zipWithNext().sumOf { distances[toKey(it.first, it.second)]!! } }
    }

    /**
     * Same as part 1, except for every permutation of possible answers just add the start to the end
     */
    override fun part2(): Any = loader().let { input ->
        val targets = input.filter { it.d != '.' }
        val coords = input.map { it.copyD() }.toSet()
        val distances = buildDistances(targets, coords)
        val choices = targets.map { it.d!! }.permutations().filter { it.first() == '0' }.map { it.plus('0') }
        choices.minOf { path -> path.zipWithNext().sumOf { distances[toKey(it.first, it.second)]!! } }
    }

    private fun buildDistances(targets: List<Coord>, coords: Set<Coord>): Map<String, Int> {
        val distances = mutableMapOf<String, Int>()
        targets.forEach { target ->
            // if at a number, add that number with path length to map
            // enumerate all neighbors, filter already visited
            // base case: if no candidates, return (just search the entire thing its not very large)
            val stack: MutableList<Pair<Coord, Int>> = mutableListOf()
            stack.add(Pair(target.copyD(), 0))
            val visited = mutableSetOf(target.copyD())
            while (stack.isNotEmpty()) {
                val (c, d) = stack.removeFirst()
                targets.firstOrNull { it.x == c.x && it.y == c.y }?.takeUnless { it.d == target.d }?.let { hit ->
                    val key = toKey(target.d, hit.d)
                    distances[key] = distances.getOrDefault(key, d)
                }
                c.neighbors().filterNot { visited.contains(it) }.filter { coords.contains(it) }.forEach { neighbor ->
                    visited.add(neighbor)
                    stack.add(Pair(neighbor, d + 1))
                }
            }
        }
        return distances
    }

    private fun toKey(vararg c: Char?) = c.filterNotNull().sorted().joinToString("")

    private fun loader() = load().flatMapIndexed { y, line ->
        line.mapIndexed { x, d ->
            Coord(x, y, d)
        }
    }.toSet().filter { it.d != '#' }
}
