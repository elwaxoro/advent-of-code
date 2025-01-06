package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 18: Many-Worlds Interpretation
 * With plenty of help from https://todd.ginsberg.com/post/advent-of-code/2019/day18/
 */
class Dec18 : PuzzleDayTester(18, 2019) {

    override fun part1(): Any = loader().let { vault ->
        vault.minSteps(vault.starts)
    }

    override fun part2(): Any = "Part 1 with hand edits to map"

    private fun loader(): Vault = load().let { lines ->
        val coords = lines.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                (Coord(x, y) to c).takeUnless { c == '.' }
            }
        }.toMap()

        val walls = coords.filter { it.value == '#' }.keys.toSet()
        val starts = coords.filter { it.value == '@' }.keys.toSet()
        val keys = coords.filter { it.value in ('a'..'z') }
        val doors = coords.filter { it.value in ('A'..'Z') }
        return Vault(starts, walls, keys, doors)
    }

    private class Vault(
        val starts: Set<Coord>,
        val walls: Set<Coord>,
        val keys: Map<Coord, Char>,
        val doors: Map<Coord, Char>,
    ) {

        /**
         * From each coord, with some set of found keys, how far can we get?
         * Note: not looking for all doors or keys, just roam around BFS style collecting keys and passing doors as we go
         */
        fun minSteps(coords: Set<Coord>, foundKeys: Set<Char> = setOf(), cache: MutableMap<Pair<Set<Coord>, Set<Char>>, Int> = mutableMapOf()): Int = (coords to foundKeys).let { state ->
            if (state in cache) {
                cache.getValue(state)
            } else {
                val totalDist = reachable(coords, foundKeys).map { (k, v) ->
                    val (at, dist, cause) = v
                    dist + minSteps((coords - cause) + at, foundKeys + k, cache)
                }.minOrNull() ?: 0
                cache[state] = totalDist
                totalDist
            }
        }

        fun reachable(from: Set<Coord>, foundKeys: Set<Char>): Map<Char, Triple<Coord, Int, Coord>> = from.flatMap { coord ->
            reachableKeys(coord, foundKeys).map { (k, v) ->
                k to Triple(v.first, v.second, coord)
            }
        }.toMap()

        /**
         * BFS looking for any NEW keys we can get to from the starting coord, returned as a coord + dist for each
         */
        fun reachableKeys(start: Coord, foundKeys: Set<Char>): Map<Char, Pair<Coord, Int>> {
            val queue = mutableListOf(start)
            val dist = mutableMapOf(start to 0)
            val keyDist = mutableMapOf<Char, Pair<Coord, Int>>()
            while (queue.isNotEmpty()) {
                val coord = queue.removeFirst()
                coord.neighbors().filter { it !in walls && it !in dist }.map { n ->
                    dist[n] = dist.getValue(coord) + 1
                    val door = doors[n]
                    val key = keys[n]
                    // neighbor is either not a door or we have a key for that door already
                    if (door == null || door.lowercaseChar() in foundKeys) {
                        // new key!
                        if (key != null && key !in foundKeys) {
                            keyDist[key] = n to dist.getValue(n)
                        } else {
                            // more space to explore
                            queue.add(n)
                        }
                    }
                }
            }
            return keyDist
        }
    }
}