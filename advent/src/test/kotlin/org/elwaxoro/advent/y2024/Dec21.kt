package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.Coord
import org.elwaxoro.advent.Dir
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.cartesianProduct

/**
 * Day 21: Keypad Conundrum
 * Observations:
 * don't move to the blank space! it freaks the robots out
 * every path for every robot starts and ends at A (so they can poke the pad in front of them)
 * when it's time to press a button on the door keypad, all other robots must be at the A position (and at the final press for a code, ALL robots are at A)
 * there's probably only 2 optimal paths between any 2 buttons? (>>^ and ^>> probably always better than >^> except base robot)
 */
class Dec21 : PuzzleDayTester(21, 2024) {

    // original solution to part 1
//    override fun part1(): Any = load().sumOf { code ->
//        val first = findPaths(code, doorPaths)
//        val second = first.flatMap { path ->
//            findPaths(path, controlPaths)
//        }
//        val min = second.minOf { it.length }
//        val bestSecond = second.filter { it.length == min }
//        val third = bestSecond.flatMap { path ->
//            findPaths(path, controlPaths)
//        }
//        third.minOf { it.length } * code.dropLast(1).toInt()
//    }

    override fun part1(): Any = load().sumOf { recursivePathfinder(it, 2) }
    override fun part2(): Any = load().sumOf { recursivePathfinder(it, 25) }

    // door keypad
    private val door = mapOf(
        Coord(0, 3) to '7', Coord(1, 3) to '8', Coord(2, 3) to '9',
        Coord(0, 2) to '4', Coord(1, 2) to '5', Coord(2, 2) to '6',
        Coord(0, 1) to '1', Coord(1, 1) to '2', Coord(2, 1) to '3',
        Coord(1, 0) to '0', Coord(2, 0) to 'A'
    )

    // robot directional keypad
    private val controls = mapOf(
        Coord(1, 1) to '^', Coord(2, 1) to 'A',
        Coord(0, 0) to '<', Coord(1, 0) to 'v', Coord(2, 0) to '>'
    )

    // pre-compute the movement paths for each keypad
    private val doorPaths = door.mapKeypad()
    private val controlPaths = controls.mapKeypad()

    /**
     * This is how to move around on a keypad from each button to every other button
     */
    private fun Map<Coord, Char>.mapKeypad(): Map<String, List<String>> =
        map { (a, aa) ->
            map { (b, bb) ->
                if (a == b) {
                    // robot is already pointed at the correct button, just press "A"
                    "$aa$bb" to listOf("A")
                } else {
                    // another BFS! exhaustively find all paths between a and b, just keep the best ones
                    val explore = mutableListOf(a to "")
                    var bestCost = Int.MAX_VALUE
                    val bestPaths = mutableListOf<String>()
                    // since cost of each step is just 1 and we're using BFS: we will find the best path length first (although there may be multiples of that same length)
                    while (explore.isNotEmpty()) {
                        val (coord, path) = explore.removeFirst()
                        // only keep exploring until the path is longer than the best cost
                        if (path.length + 1 <= bestCost) {
                            Dir.entries.map { dir ->
                                // move ^v<>, keep if still on keypad
                                coord.move(dir) to dir.toCaret()
                            }.filter { contains(it.first) }.map { (n, c) ->
                                if (n == b) {
                                    // this will be one of the best paths, add it and store the cost
                                    bestPaths.add(path + c + 'A')
                                    bestCost = path.length + 1
                                } else {
                                    // keep going, add this coord to the explore pile w/ path + the move
                                    explore.add(n to path + c)
                                }
                            }
                        }
                    }
                    "$aa$bb" to bestPaths
                }
            }
        }.flatten().toMap()

    /**
     * Finds all the paths to solve the given code, attach A to the front because that's where the robot actually points at the start of input
     */
    private fun findPaths(code: String, keypad: Map<String, List<String>>) = ("A$code").zipWithNext().mapNotNull { (a, b) -> keypad["$a$b"] }.cartesianProduct().map { it.joinToString("") }

    /**
     * Solve robot 1 the old way in part 1
     * ex 029A becomes <A^A>^^AvvvA (and many others)
     * for each pair of robot 1 inputs (<, A) etc try to find just this part at the required robot count
     * only the number of button presses matters, so throw the rest away and add up the best path cost between each pair of buttons
     */
    private fun recursivePathfinder(code: String, robots: Int) =
        findPaths(code, doorPaths).map { path ->
            ("A$path").zipWithNext().sumOf { (a, b) -> pathAtRobot(a, b, robots) }
        }.min() * code.dropLast(1).toLong()

    private fun pathAtRobot(a: Char, b: Char, r: Int): Long = computeIfAbsent(a, b, r) {
        val paths = controlPaths["$a$b"]!!
        if (r == 1) {
            // base case robot 1: just grab any path between a and b
            paths.first().length.toLong()
        } else {
            // recursive step: get all the best paths between a and b, split THEM into individual pairs and solve at the next robot
            paths.map { path ->
                ("A$path").zipWithNext().sumOf { (x, y) ->
                    pathAtRobot(x, y, r - 1)
                }
            }.min()
        }
    }

    // aww yeaaa using the SHIT outta this bad boy
    private val memo = mutableMapOf<String, Long>()
    private fun computeIfAbsent(a: Char, b: Char, r: Int, compute: () -> Long): Long = memo.getOrPut("$a$b$r") { compute.invoke() }
}
