package org.elwaxoro.advent.y2019

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.printify
import kotlin.math.atan2

/**
 * Day 10: Monitoring Station
 */
class Dec10 : PuzzleDayTester(10, 2019) {

    /**
     * make a list of angles between each asteroid and all other asteroids
     * convert list to set, size of set is the size of
     *
     * Local run: returned (11,13) with value 227
     */
    override fun part1(): Any = loader().flatten().filter { it.d == '#' }.let { coords ->
        coords.maxOf { a ->
            coords.filter { it != a }.map { b ->
                a.angleTo(b)
            }.toSet().size
        }
    } == 227

    /**
     * (11,13) attach lazer! spins in a circle and blasts the suckas
     */
    override fun part2(): Any = Coord(11, 13, 'X').let { laser ->
        loader().flatten().filter { it.d == '#' && it != laser }.let { coords ->
            // group all the other asteroids by their angle from the laser (offset by 90 degrees counter-clockwise so 0 is up instead of right)
            val angleMap = coords.groupBy { (laser.angleTo(it) + 90) % 360 }.toSortedMap().toMutableMap()
            // any angles with more than one asteroid should be sorted by distance from the laser, closest first
            angleMap.keys.forEach { angle ->
                val asteroids = angleMap[angle]!!
                if (asteroids.size > 1) {
                    angleMap[angle] = asteroids.sortedBy { laser.distance(it) }
                }
            }
            // loop through the angles, which is a clockwise rotation of the laser
            // remove the first hit from each angle and add it to the deleted list
            val deleted = mutableListOf<Coord>()
            while (angleMap.any { it.value.isNotEmpty() }) {
                angleMap.keys.forEach { angle ->
                    if (angleMap[angle]!!.isNotEmpty()) {
                        val asteroid = angleMap[angle]!!.first()
                        angleMap[angle] = angleMap[angle]!!.drop(1)
                        deleted.add(asteroid)
                    }
                }
            }
            deleted[199].x * 100 + deleted[199].y
        }
    } == 604

    private fun loader(): List<List<Coord>> = load().mapIndexed { y, row ->
        row.toList().mapIndexed { x, char ->
            Coord(x, y, char)
        }
    }
}
