package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

class Dec08 : PuzzleDayTester(8, 2018) {

    override fun part1(): Any = loader().metadataSum()

    override fun part2(): Any = loader().metadataIndexSum()

    private fun loader() = load().single().split(" ").map { it.toInt() }.let { line ->
        val fakeRoot = Node()
        parseNode(line, fakeRoot)
        fakeRoot.children.single()
    }

    private fun parseNode(line: List<Int>, parent: Node): List<Int> {
        val childCount = line[0]
        val metaCount = line[1]
        val node = Node()
        val lineRemainder = (1..childCount).fold(line.drop(2)) { acc, _ ->
            parseNode(acc, node)
        }
        node.metaData.addAll(lineRemainder.take(metaCount))
        parent.children.add(node)
        return lineRemainder.drop(metaCount)
    }

    private data class Node(
        val metaData: MutableList<Int> = mutableListOf(),
        val children: MutableList<Node> = mutableListOf(),
    ) {
        fun metadataSum(): Int = metaData.sum() + children.sumOf { it.metadataSum() }

        fun metadataIndexSum(): Int = metaData.sumOf { idx ->
            if (children.isEmpty()) {
                idx
            } else if (idx > children.size) {
                0
            } else {
                children[idx - 1].metadataIndexSum()
            }
        }
    }
}
