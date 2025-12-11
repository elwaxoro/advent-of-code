package org.elwaxoro.advent.y2025

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.multiply

/**
 * Day 11: Reactor
 */
class Dec11 : PuzzleDayTester(11, 2025) {

    /**
     * Did this with BFS first, but switched to DFS for part 2. refactored this to use the same
     */
    override fun part1(): Any = dfs("you", "out", loader()) == 746L

    /**
     * idea: we don't care about intermediate steps in the path at all, just did we visit dac and fft before going out?
     * count up svr -> dac/fft -> fft/dac -> out as separate stages then combine at the end
     * NOTE: all routes must be svr -> dac -> fft -> out OR svr -> fft -> dac -> out for a given puzzle input otherwise there would be a loop and we can't solve that
     */
    override fun part2(): Any = loader().let { network ->
        listOf("svr", "dac", "fft", "out").stepDfs(network) + listOf("svr", "fft", "dac", "out").stepDfs(network)
    } == 370500293582760

    /**
     * what are you doing, step-dfs?
     */
    private fun List<String>.stepDfs(network: Map<String, Set<String>>) = zipWithNext { a, b -> dfs(a, b, network) }.multiply()

    /**
     * basic DFS, use a map to avoid recalculating already solved nodes
     */
    private fun dfs(node: String, target: String, network: Map<String, Set<String>>, counter: MutableMap<String, Long> = mutableMapOf()): Long {
        if (node == target) {
            // base case, found the target
            return 1L
        } else if (counter.containsKey(node)) {
            // base case, already solved this node
            return counter[node]!!
        } else {
            // recursive case, explore dfs for each node connected to this one
            val count = network[node]?.sumOf { next ->
                dfs(next, target, network, counter)
            } ?: 0L
            counter[node] = count
            return count
        }
    }

    private fun loader() = load().fold(mutableMapOf<String, Set<String>>()) { acc, line ->
        acc.apply {
            val (id, b) = line.split(": ")
            this[id] = b.split(" ").toSet()
        }
    }
}
