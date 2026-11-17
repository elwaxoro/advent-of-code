package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 7: Recursive Circus
 */
class Dec07: PuzzleDayTester(7, 2017) {

    override fun part1(): Any = loader().let { tree -> 
        tree.entries.first { (name, _) -> 
            tree.none { (_,v) -> v.children.contains(name) }
        }
    }

    override fun part2(): Any = loader().let { tree ->
        tree.values.forEach { it.calcWeight(tree) }
        val unbalanced = tree.values.filter { it.isUnbalanced() }
        // the parent node we care about is the deepest/first/only unbalanced node where all of its children are themselves balanced
        val badParent = unbalanced.first { parent -> parent.children.none { child -> tree[child]!!.isUnbalanced() } }
        val byWeight = badParent.childWeights.entries.groupBy { it.value }
        // the incorrect child node is the one whose weight doesn't match the others
        val (badName, badTotal) = byWeight.minBy { it.value.size }.value.single()
        val targetTotal = byWeight.maxBy { it.value.size }.key
        tree[badName]!!.weight + (targetTotal - badTotal)
    }

    private fun loader() = load().map {
        it.replace("(", "").replace(")", "").replace("-> ", "").replace(",", "").split(" ")
    }.associate { tokens ->
        val name = tokens[0]
        val weight = tokens[1].toInt()
        val children = tokens.drop(2)
        name to Node(name, weight, children)
    }

    private data class Node(val name: String, val weight: Int, val children: List<String>) {
        var totalWeight = -1
        val childWeights = mutableMapOf<String, Int>()
        
        fun calcWeight(tree: Map<String, Node>): Int {
            if (totalWeight == -1) {
                children.forEach { c -> 
                    childWeights[c] = tree[c]!!.calcWeight(tree) 
                }
                totalWeight = weight + childWeights.values.sum()
            }
            return totalWeight
        }

        /**
         * Node is balanced if all children have the same weight
         */
        fun isUnbalanced() = childWeights.values.distinct().size > 1
    }
}
