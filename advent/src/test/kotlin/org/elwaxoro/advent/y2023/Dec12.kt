package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Hot Springs
 */
class Dec12 : PuzzleDayTester(12, 2023) {

    /**
     * idea: first: check if record + checksum is impossible get next char in record.
     * if ., drop it (recursive step)
     * if ?, replace with both . and # (recursive step)
     * if #, take next checksum item and convert things to # until item is satisfied (hitting a . before satisfied = broken attempt, give up)
     * if next char after satisfied item is #, give up
     * if next char after satisfied item is ? convert that to .
     * if record is empty but checksum has items, give up
     * if checksum is empty but record still has #, give up
     */
    override fun part1(): Any = loader().sumOf { (record, checksum) -> recursiveSpringalator6001(record, checksum) } == 7344L

    override fun part2() = loader().sumOf { (record, checksum) -> recursiveSpringalator6001(record.repeatSeparator(5), checksum.repeat(5)) } == 1088006519007L

    private fun String.repeatSeparator(size: Int): String = (0 until size).joinToString("") { "$this?" }.dropLast(1)

    private fun List<Int>.repeat(size: Int): List<Int> = (0 until size).flatMap { this }

    private val memo = mutableMapOf<String, Long>()

    private fun computeIfAbsent(key: String, compute: () -> Long): Long =
        if (memo.containsKey(key)) {
            memo.getValue(key)
        } else {
            val result = compute.invoke()
            memo[key] = result
            result
        }

    private fun recursiveSpringalator6001(record: String, checksum: List<Int>): Long = computeIfAbsent(record + checksum.joinToString()) {
        if (record.isEmpty() && checksum.isEmpty()) {
            1 // base case: success! out of records and the checksum is empty
        } else if (record.isEmpty() && checksum.isNotEmpty()) {
            0 // base case: failure. out of records but still checksums to check
        } else if (record.first() == '.') {
            recursiveSpringalator6001(record.drop(1), checksum) // recursive case: strip the . and go again
        } else if (record.first() == '?') {
            recursiveSpringalator6001(record.drop(1), checksum) + // recursive case: same as replacing with a '.' that gets dropped next recursion
                    (recursiveSpringalator6001('#' + record.drop(1), checksum).takeIf { checksum.isNotEmpty() } ?: 0L) // recursive case: attempt to start a set of springs, if any of the checksum is left
        } else if (record.first() == '#') {
            if (checksum.isEmpty()) {
                0 // base case: invalid record: ran out of checksums but still have springs
            } else {
                val item = checksum.first()
                if (record.length < item) {
                    0 // base case: invalid record: remaining record isn't big enough for the checksum
                } else {
                    val testing = record.take(item)
                    val remainder = record.drop(item).let {
                        if (it.isNotEmpty() && it.first() == '?') {
                            '.' + it.drop(1)
                        } else {
                            it
                        }
                    }
                    if (testing.all { it == '#' || it == '?' } && (remainder.isEmpty() || remainder.first() == '.')) {
                        // success! this checksum item matched the record correctly
                        // recurse without the matched record or the checksum item
                        recursiveSpringalator6001(remainder, checksum.drop(1))
                    } else {
                        // base case: invalid record: either the testing chunk couldn't be converted to springs or the next char after the chunk is also a spring
                        0
                    }
                }
            }
        } else {
            0 // idk just give up man this ain't workin
        }
    }

    private fun loader() = load().map { line ->
        val (record, checksum) = line.split(" ")
        record to checksum.split(",").map { it.toInt() }
    }
}
