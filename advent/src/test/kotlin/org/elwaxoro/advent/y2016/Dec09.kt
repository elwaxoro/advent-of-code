package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 9: Explosives in Cyberspace
 */
class Dec09 : PuzzleDayTester(9, 2016) {

    override fun part1(): Any = load().map { line ->
        var idx = 0
        var decompressed = ""
        while (idx < line.length) {
            when (line[idx]) {
                '(' -> {
                    val cmdEnd = line.indexOf(')', idx)
                    val (size, repeat) = line.substring(idx + 1, cmdEnd).split('x').map { it.trim().toInt() }
                    val target = line.substring(cmdEnd + 1, cmdEnd + 1 + size)
                    decompressed += target.repeat(repeat)
                    idx = cmdEnd + 1 + size
                }

                else -> {
                    decompressed += line[idx]
                    idx++
                }
            }
        }
        decompressed.length
    }

    override fun part2(): Any = load().map { line -> decompress(line) }

    private fun decompress(line: String): Long = computeIfAbsent(line) {
        if (line.isEmpty()) {
            0L
        } else if (line.startsWith('(')) {
            val cmdEnd = line.indexOf(')')
            val (size, repeat) = line.substring(1, cmdEnd).split('x').map { it.trim().toInt() }
            val target = line.substring(cmdEnd + 1, cmdEnd + 1 + size)
            val remainder = line.substring(cmdEnd + 1 + size)
            decompress(target.repeat(repeat)) + decompress(remainder)
        } else {
            1 + decompress(line.drop(1))
        }
    }

    private val memo = mutableMapOf<String, Long>()

    private fun computeIfAbsent(key: String, compute: () -> Long): Long =
        memo.getOrPut(key) { compute() }
}
