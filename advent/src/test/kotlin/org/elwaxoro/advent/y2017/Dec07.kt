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

    /**
     * TODO make program instead of manual inspection. Output is:
     * This node is imbalanced uduyfo 1710 9295 {amhaz=1894, cumah=1903, zqfvypo=1894, dribos=1894}
     * This node is imbalanced rsalq 47820 94259 {sdxbol=9286, irqwcab=9286, jiaiwto=9286, viydtj=9286, uduyfo=9295}
     * This node is imbalanced ykpsek 84 471343 {xgudb=94250, fucsb=94250, rsalq=94259, xjllex=94250, splbrdu=94250}
     * 2017-7 Puzzle 2: ((Node(name=cumah, weight=1069, children=[pqmyjtl, wczux, rfjilj]), 1903), {pqmyjtl=278, wczux=278, rfjilj=278}) [5ms]
     * the solution is to find the deepest imbalanced node (in this case cumah) get the totalWeight of any sibling of cumah (ex: amhaz at 1894) the correct cumah.weight would be cumah.weight - (cuma.childWeights - amhaz.totalWeight) 
     */
    override fun part2(): Any = loader().let { tree ->
        tree.map { (_, n) -> n.calcWeight(tree) }
        val badNode = tree["cumah"]!!
        badNode to badNode.totalWeight to badNode.childWeights
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
                balanceCheck()
            }
            return totalWeight
        }
        
        fun balanceCheck() {
            if (children.isNotEmpty()) {
                val test = childWeights[children.first()]
                if (childWeights.values.any { it != test }) {
                    println("This node is imbalanced $name $weight $totalWeight $childWeights")
                }
            }
        }
    }
}
