package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.splitToInt

/**
 * Day 9: Disk Fragmenter
 * Basic idea: expand disk so that it's full of -1s for empty space or repeated ids for filled space
 * Use list of int so ids can go over 9
 * Half expected part 2 to run forever or blow up mem, but it completes in about 2.5s
 */
class Dec09 : PuzzleDayTester(9, 2024) {

    /**
     * trim the data from the end until no more -1s exist
     * unlike the example, trim the list so the end never contains free space (gets smaller as it sorts)
     */
    override fun part1(): Any = load().single().splitToInt().expand().flatten().toMutableList().also { disk ->
        var idx = disk.indexOf(-1)
        do {
            while (disk.last() == -1) {
                disk.removeLast() // make sure there's no free space at the end
            }
            if (disk[idx] == -1) {
                disk[idx] = disk.removeLast()
            }
            idx++
        } while (idx < disk.size)
    }.checksum()

    override fun part2(): Any = load().single().splitToInt().expand().let { disk ->
        val defrag = disk.flatten().toMutableList()
        (disk.size - 1 downTo 1).forEach { idx ->
            val chunk = disk[idx]
            if (chunk.isNotEmpty() && chunk[0] != -1) {
                val dataIdx = defrag.indexOfFirst { it == chunk[0] }
                val emptyIdx = defrag.findSpace(chunk.size, dataIdx)
                if (emptyIdx > -1) {
                    (dataIdx..<dataIdx + chunk.size).forEach { defrag[it] = -1 }
                    (emptyIdx..<emptyIdx + chunk.size).forEach { defrag[it] = chunk[0] }
                }
            }
        }
        defrag.checksum()
    }

    /**
     * expands the disk into a list of chunks where each chunk has repeated id (or -1 for empty) for the size
     * ex: 12345 becomes [[0], [-1, -1], [1, 1, 1], [-1, -1, -1, -1], [2, 2, 2, 2, 2]]
     * thought this would matter but everything ended up flattened anyway. still kinda sorta helpful in part 2
     */
    private fun List<Int>.expand(): List<List<Int>> = mapIndexed { idx, size -> ((idx / 2).takeIf { idx % 2 == 0 } ?: -1).let { fill -> (1..size).map { fill } } }

    private fun List<Int>.checksum(): Long = mapIndexedNotNull { idx, id -> (idx * id.toLong()).takeUnless { id == -1 } }.sum()

    private fun List<Int>.findSpace(space: Int, maxIdx: Int): Int {
        (0..maxIdx).forEach { idx ->
            if (this[idx] == -1 && (idx..<idx + space).all { this[it] == -1 }) {
                return idx
            }
        }
        return -1
    }
}
