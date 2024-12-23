package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: LAN Party
 */
class Dec23 : PuzzleDayTester(23, 2024) {

    /**
     * 3-cliques with a "t" node in them
     */
    override fun part1(): Any = loader().let { (cliques, network) ->
        growCliques(cliques.filter { it.any { it.startsWith('t') } }, network).size
    }

    /**
     * oh look it's maximum clique
     * BRUTE FOOOOORCE GO!
     */
    override fun part2(): Any = loader().let { (initialCliques, network) ->
        var cliques = initialCliques
        while (true) {
            val newCliques = growCliques(cliques, network)
            if (newCliques.isNotEmpty()) {
                cliques = newCliques
            } else {
                return cliques.map { it.sorted().joinToString(",") }
            }
        }
    }

    /**
     * for each node of a clique, see if we can add one connection to the clique
     */
    private fun growCliques(cliques: List<Set<String>>, network: Map<String, Set<String>>): List<Set<String>> =
        cliques.flatMap { clique ->
            clique.flatMap { node ->
                network.getValue(node).filter { network.getValue(it).containsAll(clique) }.map { clique + it }
            }.distinct()
        }.distinct()

    /**
     * load the input 2 ways:
     * each connection becomes a 2-clique set
     * each node gets a list of all connected nodes
     */
    private fun loader() = load().let { input ->
        val cliques = input.map { it.split("-").toSet() }.distinct()
        val network = input.flatMap {
            val (a, b) = it.split("-")
            listOf(a to b, b to a)
        }.groupBy { it.first }.mapValues { it.value.map { it.second }.toSet() }
        cliques to network
    }
}
