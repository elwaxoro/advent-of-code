package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.abs

/**
 * Day 20: Grove Positioning System
 */
class Dec20 : PuzzleDayTester(20, 2022) {

    override fun part1(): Any = loader(decryptionKey = 1).groveCoordinates() == 3466L
    override fun part2(): Any = loader(decryptionKey = 811589153L).groveCoordinates(10) == 9995532008348

    private fun List<Item>.groveCoordinates(reps: Int = 1): Long {
        (0 until reps).forEach { _ -> forEach { it.move() } }
        val zero = single { it.num == 0L }
        // it would be more efficient to seek these all together, but still runs fast enough so whatever
        return zero.seek(1000).num + zero.seek(2000).num + zero.seek(3000).num
    }

    private fun loader(decryptionKey: Long = 1): List<Item> = loadToLong().let { input ->
        val items = input.map { Item(it * decryptionKey, input.lastIndex) }
        return items.onEachIndexed { index, item ->
            item.prev = items[(index - 1 + input.size) % input.size]
            item.next = items[(index + 1 + input.size) % input.size]
        }
    }

    /**
     * Doubly-linked list. Yey
     */
    data class Item(var num: Long, val size: Int) {
        var prev: Item = this
        var next: Item = this

        /**
         * Move from current position left or right by `num` steps
         */
        fun move() {
            // part2's numbers are HUGE so we gotta cut it down or it'll never finish
            val modMove = num % size
            (0 until abs(modMove)).forEach { _ ->
                if (modMove < 0) {
                    moveTo(prev.prev, prev)
                } else {
                    moveTo(next, next.next)
                }
            }
        }

        private fun moveTo(newPrev: Item, newNext: Item) {
            // connect previous to next (unhook this item)
            prev.next = next
            next.prev = prev

            // connect this to new neighbors
            prev = newPrev
            next = newNext

            // connect new neighbors to this
            prev.next = this
            next.prev = this
        }

        fun seek(num: Int): Item {
            var target = this
            (0 until num).forEach { _ -> target = target.next }
            return target
        }
    }
}
