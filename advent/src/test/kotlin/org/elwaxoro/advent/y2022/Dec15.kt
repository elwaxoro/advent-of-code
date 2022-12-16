package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test

class Dec15 : PuzzleDayTester(15, 2022) {

    override fun part1(): Any = loader().let { pairs ->
        val beacons = pairs.map { it.last() }.toSet()
        val targetY = 2000000
        pairs.buildLayer2(2000000).let { mergedRanges ->
            val bacons = beacons.filter { it.y == targetY }.map { it.x }.count { b -> mergedRanges.any { it.contains(b) } }
            mergedRanges.single().let {
                it.last - it.first + 1 - bacons
            }
        }
    } == 5367037

    override fun part2(): Any = loader().let { pairs ->
        (0..4000000).forEach { targetY ->
            pairs.buildLayer2(targetY).let {
                if (it.size > 1) {
                    println("FOUND ONE ON LAYER $targetY: $it  [${it.first().last + 1}, $targetY]")
                    println(((it.first().last + 1) * 4000000L) + targetY)
                }
            }
        }
        println("WE DIDNT FIND SHIT")
    }

    //@Test
    fun testo() {
        val abc = "abcdefghikjlmnopqrstuvwxyz"
        loader().let { pairs ->
            val sensors = pairs.map { it.first() }.toSet()
            val beacons = pairs.map { it.last() }.toSet()
            val zero = Coord(0, 0, '0')
                val map = pairs.mapIndexed { idx, (sensor, beacon) ->
                    val dist = sensor.taxiDistance(beacon)
                    (sensor.y - dist .. sensor.y + dist).map { y ->
                        val x1 = sensor.x - dist + abs(sensor.y - y)
                        val x2 = sensor.x + dist - abs(sensor.y - y)
                        listOf(x1, x2).sorted().let {(it.first() .. it.last())

                        }.map { x ->
                            Coord(x,y, abc[idx])
                        }
                    }.flatten().plus(sensor.copyD(abc[idx].uppercaseChar())).plus(beacon.copyD('B'))
                }.flatten().plus(sensors.mapIndexed { idx, s -> s.copyD(abc[idx]) }).plus(beacons.map { it.copyD('B') }).plus(zero)
//            val limits = listOf(Coord(-1, -1, '_'), Coord(21, -1, '_'), Coord(21, 21, '_'), Coord(-1, 21, '_'), Coord(-1, -1, '_')).enumerateLines()
//            println(map.plus(limits).printify())
            println(map.printify())
            val targetY = 3
            val slice = pairs.buildLayer2(targetY).map { ranges ->
                ranges.map { Coord(it, targetY, '?') }
            }.flatten()
            println(map.plus(slice).printify())
        }
    }

    private fun List<List<Coord>>.buildLayer2(targetY: Int) =
        mapNotNull { (sensor, beacon) ->
            val dist = sensor.taxiDistance(beacon)
            if (targetY <= sensor.y + dist && targetY >= sensor.y - dist) {
//            if (targetY in (sensor.y - targetY..sensor.y + targetY)) {
                val x1 = sensor.x - dist + abs(sensor.y - targetY)
                val x2 = sensor.x + dist - abs(sensor.y - targetY)

            (x1 .. x2)

//                if (x1 <= x2 && x2 > 0) {
//                    (max(0, x1)..min(4000000, x2))
////                        .also {
////                        println("sensor $sensor at Y $targetY has range $it")
////                    }
//                } else {
//                    null
//                }
//            } else {
//                null
//            }
            } else {
                null
            }
        }.mergeRanges()

    private fun List<List<Coord>>.buildLayer(targetY: Int) =
        mapNotNull { (sensor, beacon) ->
            val dist = sensor.taxiDistance(beacon)
            val targetDistY = targetY - sensor.y
            if (targetY in (sensor.y - targetY..sensor.y + targetY)) {
                val left = sensor.add(dist * -1, 0)
                val right = sensor.add(dist, 0)
                val leftIntercept = left.add(abs(targetDistY), targetDistY).x
                val rightIntercept = right.add(abs(targetDistY) * -1, targetDistY).x
                val range = listOf(leftIntercept, rightIntercept).sorted()
                (range.first()..range.last())
            } else {
                null
            }
        }.mergeRanges()

    private fun List<IntRange>.mergeRanges() = sortedBy { it.first }.fold(listOf<IntRange>()) { acc, range ->
        if (acc.isEmpty()) {
//            println("Ranges empty! adding $range")
            acc.plus(listOf(range))
        } else {
            val active = acc.last()
            if (active.contains(range.last)) {
//                println("$active completely contains $range [skip]")
                acc
            } else if (active.contains(range.first)) {
//                println("$active overlaps $range [merge]")
                acc.dropLast(1).plus(listOf(active.first..range.last))
            } else {
//                println("$active outside range $range [add]")
                acc.plus(listOf(range))
            }
        }
    }

    private fun loader() = load().map {
        it.replace("Sensor at x=", "").replace(" y=", "").split(": closest beacon is at x=").map(Coord::parse)
    }
}
