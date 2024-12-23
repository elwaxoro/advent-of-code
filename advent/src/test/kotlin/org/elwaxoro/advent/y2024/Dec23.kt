package org.elwaxoro.advent.y2024

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: LAN Party
 */
class Dec23: PuzzleDayTester(23, 2024) {

    /**
     * 3-cliques with a "t" node in them
     */
    override fun part1(): Any = loadNetwork().let { network ->
        network.keys.filter { it.startsWith('t') }.flatMap { t ->
            val connections = network.getValue(t)
            connections.flatMap { c ->
                network.getValue(c).filter { connections.contains(it) }.map { setOf(t, c, it) }
            }
        }.distinct().size
    }

    /**
     * oh look it's maximum clique
     */
    override fun part2(): Any {
        val network = loadNetwork()
        var cliques = loadCliques()
        while (true) {
            val newCliques = growCliques(cliques, network)
            if (newCliques.isNotEmpty()) {
                cliques = newCliques
            } else {
                return cliques.map { it.sorted().joinToString(",") }
            }
        }
    }

    private fun growCliques(cliques: List<Set<String>>, network: Map<String, Set<String>>): List<Set<String>> =
        cliques.flatMap { clique ->
            // for each member of the clique, see if we can add something from their connections to the clique
            clique.flatMap { member ->
                network.getValue(member).filter { network.getValue(it).containsAll(clique) }.map { clique + it }
            }.distinct()
        }.distinct()

    private fun loader() = load().let { input ->
        val cliques = input.map { it.split("-").toSet() }.distinct()
        val network = input.flatMap {
            val (a, b) = it.split("-")
            listOf(a to b, b to a)
        }.groupBy { it.first }.mapValues { it.value.map { it.second }.toSet() }
        cliques to network
    }

    private fun loadCliques() = load().map { it.split("-").toSet() }.distinct()

    private fun loadNetwork() = load().flatMap {
        val (a, b) = it.split("-")
        listOf(a to b, b to a)
    }.groupBy { it.first }.mapValues { it.value.map { it.second }.toSet() }
}
