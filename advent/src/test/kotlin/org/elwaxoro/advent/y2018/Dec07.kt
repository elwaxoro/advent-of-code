package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 7: The Sum of Its Parts
 */
class Dec07 : PuzzleDayTester(7, 2018) {

    override fun part1(): Any = loader().let { nodes ->
        val visited = mutableListOf<Char>()
        while (nodes.isNotEmpty()) {
            visited.add(nodes.takeNext()!!.c)
        }
        visited.joinToString("")
    }

    override fun part2(): Any = loader().let { nodes ->
        val workers = (1..5).map { Worker() }.toList()
        var minute = 0
        while (nodes.isNotEmpty()) {
            // finish up any workers who have nodes but are now at their available minute again
            // this also removes the worked nodes from the list
            workers.forEach {
                if (it.availableMinute <= minute && it.node != null) {
                    nodes.take(it.node!!.c)
                    it.node = null
                }
            }
            // assign candidates to any available workers, filter nodes already being worked but not yet complete
            val candidates = nodes.candidates().sortedBy { it.c }.filterNot { n -> workers.any { it.node == n } }
            val availableWorkers = workers.filter { it.node == null }
            availableWorkers.forEachIndexed { idx, worker ->
                if (idx < candidates.size) {
                    worker.assignNode(candidates[idx], minute)
                }
            }
            if (nodes.isNotEmpty()) {
                // advance time to the earliest completion time across all workers
                // exclude any workers that didn't manage to get work due to not enough candidates
                minute = workers.filter { it.node != null }.minOf { it.availableMinute }
            }
        }
        workers.maxOf { it.availableMinute }
    }

    private data class Worker(
        var availableMinute: Int = 0,
        var node: Node? = null
    ) {
        fun assignNode(n: Node, m: Int) {
            node = n
            availableMinute = m + n.cost()
        }
    }

    private fun loader() = mutableMapOf<Char, Node>().also { graph ->
        load().map { it[5] to it[36] }.map { edge ->
            graph.getOrPut(edge.second) { Node(edge.second) }.parents.add(graph.getOrPut(edge.first) { Node(edge.first) })
        }
    }

    private fun MutableMap<Char, Node>.takeNext(): Node? = take(candidates().minOf { it.c })

    private fun MutableMap<Char, Node>.candidates(): Collection<Node> = this.filter { it.value.parents.isEmpty() }.values

    private fun MutableMap<Char, Node>.take(c: Char): Node? = remove(c).also { forEach { (_, n) -> n.parents.remove(it) } }

    private data class Node(
        val c: Char,
        val parents: MutableList<Node> = mutableListOf()
    ) {
        fun cost() = 61 + ('A'..'Z').indexOf(c)
    }
}
