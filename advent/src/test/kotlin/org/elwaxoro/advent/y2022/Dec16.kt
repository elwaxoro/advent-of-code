package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Node
import org.elwaxoro.advent.PuzzleDayTester

class Dec16 : PuzzleDayTester(16, 2022) {

    override fun part1(): Any = loader().let { valves ->
        (1.. 30).fold(listOf(Path(valves["AA"]!!, 30))) { paths, _ ->
            paths.flatMap { path ->
                path.openValveAndFanOut()
            }.sortedByDescending { it.maxPotential() }.take(10000)
        }.maxOf { it.pressure } == 1474L
    }

    //@Test
    fun testo() = loader().let { valves ->
        val start = valves["AA"]!!
        var minutes = 0

        val forcePath = listOf("DD", "open", "CC", "BB", "open", "AA", "II", "JJ", "open", "II", "AA", "DD", "EE", "FF", "GG", "HH", "open", "GG", "FF", "EE", "open", "DD", "CC", "open", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay")

        var path = Path(start, minutes)
        while (minutes < 30) {

            path = when (val action = forcePath[minutes]) {
                "open" -> path.openLast()
                "stay" -> path.addPressure()
                else -> path.move(valves[action]!!)
            }

            minutes++

            println("== Minute $minutes ==")
            println(path)
            println(path.maxPotential())
        }
    }

    private data class Path(
        val head: Node,
        val time: Int,
        val openValves: Map<Node, Int> = mapOf(),
        val pressure: Long = 0,
        val eleHead: Node? = null,
    ) {
        fun addPressure(): Path = Path(head, time - 1, openValves, pressure + openValves.map { it.key.scratch }.sum(), eleHead)

        fun openLast(): Path {
            val newPressure = pressure + openValves.map { it.key.scratch }.sum()
            val newMap = openValves.plus(head to time - 1)
            return Path(head, time - 1, newMap, newPressure, eleHead)
        }

        fun move(to: Node): Path {
            val newPressure = pressure + openValves.map { it.key.scratch }.sum()
            return Path(to, time - 1, openValves, newPressure, eleHead)
        }

        /**
         * turns out, just current pressure isn't enough for a reduction / fitness function
         */
        fun maxPotential(): Long = openValves.map { (v, t) -> t * v.scratch }.sum().toLong()

        fun fanOut(): List<Path> = head.edges.map { move(it.key) }

        fun openValveAndFanOut(): List<Path> {
            return if (head.scratch > 0 && !openValves.containsKey(head)) {
                listOf(openLast())
            } else {
                listOf()
            }.plus(fanOut())
        }
    }

    override fun part2(): Any {
        return super.part2()
    }

    private fun loader() = mutableMapOf<String, Node>().also { nodes ->
        load().map { line ->
            line.replace("Valve ", "").replace(" has flow rate=", "=").replace(" tunnels lead to valves ", "").replace(" tunnel leads to valve ", "").let {
                val (v, neighbors) = it.split(";")
                val (valve, pressure) = v.split("=")
                val valveNode = nodes.getOrPut(valve) { Node(valve) }
                valveNode.scratch = pressure.toInt()
                neighbors.split(", ").map { neighborName ->
                    val n = nodes.getOrPut(neighborName) { Node(neighborName) }
                    valveNode.addEdge(n, 1)
                    n.addEdge(valveNode, 1)
                }
            }
        }
    }
}
