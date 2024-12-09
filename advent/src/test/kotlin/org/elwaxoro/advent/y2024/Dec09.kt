package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.splitToInt
import org.elwaxoro.advent.toward

class Dec09 : PuzzleDayTester(9, 2024) {

    override fun part1(): Any = loader().expand().flatten().toMutableList().let { disk ->
        while (disk.contains(-1)) {
            while (disk.last() == -1) {
                disk.removeLast()
            }
            val swap = disk.indexOf(-1)
            disk[swap] = disk.removeLast()
        }
        disk.checksum()
    }// == 6330095022244

    private fun List<Int>.checksum(): Long = mapIndexedNotNull { idx, id ->
        (idx * id.toLong()).takeUnless { id == -1 }
    }.sum()

    private fun List<Int>.expand(): List<List<Int>> = mapIndexed { idx, i ->
        val fill = if (idx % 2 == 0) {
            idx / 2
        } else {
            -1
        }
        (1..i).map { fill }
    }

    private fun List<Int>.findSpace(space: Int): Int {
        forEachIndexed { idx, i ->
            if (i == -1 && idx + space < size && (idx..<idx+space).all { this[it] == -1 }) {
                return idx
            }
        }
        return -1
    }

    override fun part2(): Any = loader().expand().let { disk ->
        val defrag = disk.flatten().toMutableList()
        (disk.size -1 downTo 1).forEach { idx ->
            val working = disk[idx]
            if (working.isNotEmpty() && working[0] != -1) {
                val a = defrag.indexOfFirst { it == working[0] }
                val b = defrag.indexOfLast { it == working[0] }
                val size = working.size
                val target = defrag.findSpace(size)
                if (target > -1 && target < a) {
//                    println("working with $idx: $working. found space at $target")
                    (a..b).forEach { defrag[it] = -1 }
                    (target..<target+size).forEach { defrag[it] = working[0] }
                } else {
//                    println("working with $idx: $working. can't find anywhere to put it")
                }
//                println(defrag.printify())
            }
        }
        defrag.checksum()
    }

    private fun List<Int>.printify(): String =
        map {
            if (it == -1) {
                "."
            } else {
                "$it"
            }
        }.joinToString("")

    private fun loader() = load().single().splitToInt()
}
