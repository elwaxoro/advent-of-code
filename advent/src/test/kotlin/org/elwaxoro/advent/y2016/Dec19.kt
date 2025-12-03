package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 19: An Elephant Named Joseph
 *
 * Part 1 takes ~200ms
 * Part 2 had to seek halfway thru the list every time which is WAY too slow, resorted to looking up the math approach instead of deriving it myself. maybe someday!
 */
class Dec19 : PuzzleDayTester(19, 2016) {

    override fun part1(): Any {
        var node = init(3014387)
        while (node.idx != node.next!!.idx) {
            node = node.stealPresents()
        }
        return node.idx == 1834471
    }

    override fun part2(): Any {
        val n = 3014387
        var p = 1
        while (p * 3 <= n) {
            p *= 3
        }

        return (if (n == p) {
            n
        } else if (n <= 2 * p) {
            n - p
        } else {
            2 * n - 3 * p
        }) == 1420064
    }

    fun init(size: Int): Node {
        val start = Node(1, 1)
        val last = (2..size).fold(start) { prev, idx ->
            Node(idx, 1).also {
                prev.next = it
                it.prev = prev
            }
        }
        last.next = start
        start.prev = last
        return start
    }

    data class Node(
        val idx: Int,
        var presents: Int,
        var prev: Node? = null,
        var next: Node? = null
    ) {

        fun stealPresents(): Node {
            presents += next!!.presents
            next = next!!.next
            next!!.prev = this
            return next!!
        }

        fun remove() {
            next!!.prev = prev
            prev!!.next = next
        }
    }
}
