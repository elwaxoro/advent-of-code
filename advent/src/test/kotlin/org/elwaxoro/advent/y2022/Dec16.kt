package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.Node
import org.elwaxoro.advent.PuzzleDayTester

class Dec16 : PuzzleDayTester(16, 2022) {

    override fun part1(): Any = loader().let { valves ->
        (1..30).fold(listOf(Path(valves["AA"]!!, 30))) { paths, _ ->
            paths.flatMap { path ->
                path.openValveAndFanOut()
            }.sortedByDescending { it.maxPotential() }.take(10000) // culling function
        }.maxOf { it.pressure }// == 1474L
    }

    override fun part2(): Any = loader().let { valves ->
        (1..26).fold(listOf(Path(head = valves["AA"]!!, time = 26, eleHead = valves["AA"]!!))) { paths, _ ->
            paths.flatMap { path ->
                path.openValveAndFanOut()
            }.sortedByDescending { it.maxPotential() }.take(20000) // uhhh apparently culling function from ^^ is too small so double it idk it works now
        }.maxOf { it.pressure }// == 2100L
    }

    private data class Path(
        val head: Node,
        val time: Int,
        val openValves: Map<Node, Int> = mapOf(),
        val pressure: Long = 0,
        val eleHead: Node? = null,
    ) {
        fun addPressure(): Path = Path(head, time - 1, openValves, pressure + openValves.map { it.key.scratch }.sum(), eleHead)

        fun open(openMe: Node, isEleMove: Boolean): Path {
            val newPressure = pressure.takeIf { isEleMove } ?: (pressure + openValves.map { it.key.scratch }.sum())
            val newMap = openValves.plus(openMe to (time.takeIf { isEleMove } ?: (time - 1)))
            return Path(head, (time.takeIf { isEleMove } ?: (time - 1)), newMap, newPressure, eleHead)
        }

        fun move(to: Node, isEleMove: Boolean): Path {
            val newPressure = pressure.takeIf { isEleMove } ?: (pressure + openValves.map { it.key.scratch }.sum())
            return Path(to.takeUnless { isEleMove } ?: head, (time.takeIf { isEleMove } ?: (time - 1)), openValves, newPressure, to.takeIf { isEleMove } ?: eleHead)
        }

        /**
         * turns out, just current pressure isn't enough for a reduction / fitness function
         */
        fun maxPotential(): Long = openValves.map { (v, t) -> t * v.scratch }.sum().toLong()

        fun fanOut(fanMe: Node, isEleMove: Boolean): List<Path> = fanMe.edges.map { move(it.key, isEleMove) }

        fun openValveAndFanOut(): List<Path> {
            // first open / move the head and get all outcomes
            val headMoves = if (head.scratch > 0 && !openValves.containsKey(head)) {
                listOf(open(head, false))
            } else {
                listOf()
            }.plus(fanOut(head, false))

            return if (eleHead != null) {
                // now apply each of those new outcomes to some elephant bullshit
                headMoves.map { op ->
                    if (eleHead.scratch > 0 && !op.openValves.containsKey(eleHead)) {
                        listOf(op.open(eleHead, true))
                    } else {
                        listOf()
                    }.plus(op.fanOut(eleHead, true))
                }.flatten()
            } else {
                headMoves
            }
        }
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

    /**
     * nothing was working, so this is a simulator of the sample exactly
     */
    //@Test
    fun testo() = loader().let { valves ->
        val start = valves["AA"]!!
        var minutes = 0

        val forcePath = listOf("DD", "open", "CC", "BB", "open", "AA", "II", "JJ", "open", "II", "AA", "DD", "EE", "FF", "GG", "HH", "open", "GG", "FF", "EE", "open", "DD", "CC", "open", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay", "stay")

        var path = Path(start, minutes)
        while (minutes < 30) {

            path = when (val action = forcePath[minutes]) {
                "open" -> path.open(path.head, false)
                "stay" -> path.addPressure()
                else -> path.move(valves[action]!!, false)
            }

            minutes++

            println("== Minute $minutes ==")
            println(path)
            println(path.maxPotential())
        }
    }
}
