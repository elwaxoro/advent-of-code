package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 4: Ceres Search
 */
class Dec04 : PuzzleDayTester(4, 2024) {

    override fun part1(): Any = load().let { input ->
        input.mapIndexed { y, s ->
            s.mapIndexed { x, c ->
                val xmas = mutableListOf<String>()
                if (c == 'X') {
                    if (y > 2) {
                        // look up
                        xmas.add("$c${input[y - 1][x]}${input[y - 2][x]}${input[y - 3][x]}")
                        if (x > 2) {
                            // look diag up + left
                            xmas.add("$c${input[y - 1][x - 1]}${input[y - 2][x - 2]}${input[y - 3][x - 3]}")
                        }
                        if (x < s.length - 3) {
                            // look diag up + right
                            xmas.add("$c${input[y - 1][x + 1]}${input[y - 2][x + 2]}${input[y - 3][x + 3]}")
                        }
                    }
                    if (y < input.size - 3) {
                        // look down
                        xmas.add("$c${input[y + 1][x]}${input[y + 2][x]}${input[y + 3][x]}")
                        if (x > 2) {
                            // look diag down + left
                            xmas.add("$c${input[y + 1][x - 1]}${input[y + 2][x - 2]}${input[y + 3][x - 3]}")
                        }
                        if (x < s.length - 3) {
                            // look diag down + right
                            xmas.add("$c${input[y + 1][x + 1]}${input[y + 2][x + 2]}${input[y + 3][x + 3]}")
                        }
                    }
                    if (x > 2) {
                        // look left
                        xmas.add("$c${input[y][x - 1]}${input[y][x - 2]}${input[y][x - 3]}")
                    }
                    if (x < s.length - 3) {
                        // look right
                        xmas.add("$c${input[y][x + 1]}${input[y][x + 2]}${input[y][x + 3]}")
                    }
                }
                xmas.count { it == "XMAS" }
            }.sum()
        }.sum()
    }

    override fun part2(): Any = load().let { input ->
        input.mapIndexed { y, s ->
            s.mapIndexed { x, c ->
                val xmas = mutableListOf<String>()
                if (c == 'A' && y > 0 && y < input.size - 1 && x > 0 && x < input.size - 1) {
                    xmas.add("${input[y - 1][x - 1]}$c${input[y + 1][x + 1]}")
                    xmas.add("${input[y - 1][x + 1]}$c${input[y + 1][x - 1]}")
                    xmas.add("${input[y + 1][x - 1]}$c${input[y - 1][x + 1]}")
                    xmas.add("${input[y + 1][x + 1]}$c${input[y - 1][x - 1]}")
                }
                if (xmas.count { it == "MAS" } > 1) {
                    1
                } else {
                    0
                }
            }.sum()
        }.sum()
    }
}
