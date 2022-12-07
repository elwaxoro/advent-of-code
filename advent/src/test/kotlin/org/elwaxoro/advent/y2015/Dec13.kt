package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.Node
import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.addNode
import org.elwaxoro.advent.findBestPath
import org.elwaxoro.advent.maxCost
import org.elwaxoro.advent.maxPath

/**
 * Knights of the Dinner Table
 */
class Dec13: PuzzleDayTester(13, 2015) {
    override fun part1(testFileSuffix: Int?): Any = findBestPath(parse(), connectLoop = true, ::maxPath, ::maxCost)

    override fun part2(testFileSuffix: Int?): Any = parse().let { nodes ->
        val newNode = Node("Me")
        nodes.forEach {
            newNode.addEdge(it, 0)
        }
        nodes.addNode(newNode)
    }.let {
        findBestPath(it, connectLoop = true, ::maxPath, ::maxCost)
    }

    private fun parse(): List<Node> = mutableMapOf<String, Node>().also { map ->
        load().let { lines ->
            lines.map {
                it.replace("would ", "").replace(" happiness units by sitting next to", "").replace(".", "").replace("gain ", "").replace("lose ", "-")
            }.forEach { line ->
                line.split(" ").let { (from, cost, to) ->
                    val sourceNode = map.getOrDefault(from, Node(from))
                    val destNode = map.getOrDefault(to, Node(to))
                    sourceNode.addEdge(destNode, sourceNode.edges.getOrDefault(destNode, 0) + cost.toInt())
                    destNode.addEdge(sourceNode, destNode.edges.getOrDefault(sourceNode, 0) + cost.toInt())
                    map[from] = sourceNode
                    map[to] = destNode
                }
            }
        }
    }.values.toList()
}