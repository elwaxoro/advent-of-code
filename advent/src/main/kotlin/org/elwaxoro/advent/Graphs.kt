package org.elwaxoro.advent

/**
 * ad-hoc wrapper class for doing stuff with graphs based purely on
 * what's needed for past challenges with no thought given to future need
 * Nodes connect to other Nodes via edges
 *
 * IMPORTANT!!! Node's name must be unique to distinguish it from other nodes
 */
data class Node(
    val name: String,
    val canVisit: Boolean = true, // custom rules for a puzzle, indicates unexplored node
    val canLeave: Boolean = true, // custom rules for a puzzle, indicates exit node
    val canRevisit: Boolean = false // custom rules for a puzzle
) {

    val edges = mutableMapOf<Node, Int>() // directly connected nodes, along with cost to visit them if applicable

    var parent: Node? = null // omg what am I even doing. should really create a tree structure instead of hacking the node to do this :( this is separate so that edges can be children without circular trees

    // Misc for various puzzles. Put whatever you want here
    var scratch: Int = 0
    var coord: Coord? = null

    // helpers for dijkstra
    var shortestPath = listOf<Node>()
    var shortestDistance: Int = Int.MAX_VALUE

    override fun toString(): String = name

    /**
     * Edge cost to visit an adjacent node
     */
    fun cost(node: Node): Int = edges[node]!!

    /**
     * Add an adjacent node + cost to visit
     */
    fun addEdge(node: Node, cost: Int = 0) {
        edges[node] = cost
    }

    /**
     * Dijkstra it up!
     * This call populates this node and all connected nodes with the shortest path from all other connected nodes to this node
     * Output path and cost data are stored in each Node, this Node's path will be empty and cost will be zero
     * NOTE: if the graph is not sufficiently connected, watch for empty paths and shortest distance = Int.MAX_VALUE
     * DANGER: this is a mutating call so will only really work once for all nodes involved
     */
    fun dijkstra() {
        shortestDistance = 0
        val settled: MutableSet<Node> = mutableSetOf()
        val unsettled: MutableSet<Node> = mutableSetOf()
        unsettled.add(this)

        while (unsettled.isNotEmpty()) {
            // always work on the shortest path first
            unsettled.minByOrNull { it.shortestDistance }!!.let { node ->
                // node goes from unsettled to settled
                settled.add(unsettled.delete(node))
                // all neighbors not already settled update their distances and go in the unsettled pile
                unsettled.addAll(node.edges.keys.filterNot { settled.contains(it) }.map { it.calculateMinimumDistance(node) })
            }
        }
    }

    /**
     * dijkstra helper
     */
    private fun MutableSet<Node>.delete(node: Node): Node = node.also { remove(node) }

    /**
     * dijkstra helper
     * If (other's shortest path + cost there to here) is better than this node's shortest path,
     * replace this node's shortest path stuff
     */
    private fun calculateMinimumDistance(other: Node): Node = this.also {
        (other.shortestDistance + cost(other)).takeIf { it < shortestDistance }?.let { newShorterDistance ->
            shortestDistance = newShorterDistance
            shortestPath = other.shortestPath + other
        }
    }

    fun printify(): String = "$name: $scratch: $edges"

    /**
     * Starting with this node as depth 0, recursively calculate the depth of all connected nodes
     */
    fun calculateAllDepths(depth: Int = 0) {
        this.scratch = depth
        edges.keys.forEach { it.calculateAllDepths(depth + 1) }
    }

    /**
     * Starting with this Node, follow the parent links until one is null
     * Returns a list of Nodes from this one to the root of the tree / graph
     */
    fun traceRoot(path: List<Node> = listOf()): List<Node> =
        if (parent == null) {
            path.plus(this)
        } else {
            parent!!.traceRoot(path.plus(this))
        }
}

/**
 * Insert a new node into a list of existing nodes, updating existing edge/node lists
 */
fun List<Node>.addNode(node: Node): List<Node> = map {
    it.also {
        if (node.edges.contains(it)) {
            it.addEdge(node, node.cost(it))
        }
    }
}.plus(node)

/**
 * Minimum cost path from a list of paths (path: list of connected nodes)
 */
fun minCost(nodes: List<List<Node>>): Int = nodes.minOf { it.cost() }

/**
 * Maximum cost path from a list of paths (path: list of connected nodes)
 */
fun maxCost(nodes: List<List<Node>>): Int = nodes.maxOf { it.cost() }

/**
 * Minimum cost path, get the path
 */
fun minPath(nodes: List<List<Node>>): List<Node> = nodes.minByOrNull { it.cost() }!!

/**
 * Maximum cost path, get the path
 */
fun maxPath(nodes: List<List<Node>>): List<Node> = nodes.maxByOrNull { it.cost() }!!

/**
 * Total cost of path described by list of nodes. Assumes in-order traversal and that nodes are actually connected
 */
fun List<Node>.cost(): Int = zipWithNext { a, b -> a.cost(b) }.sum()

/**
 * Assumes a fully connected bidirectional graph, find the best path that includes ALL nodes
 * pathfinder and cost function must be provided
 * connectLoop will add an edge between start and end nodes
 */
fun findBestPath(
    nodes: List<Node>,
    connectLoop: Boolean,
    bestPathFinder: (nodes: List<List<Node>>) -> List<Node>,
    costFunction: (nodes: List<List<Node>>) -> Int
): Int =
    costFunction(nodes.map {
        visitAllNodes(listOf(it), nodes.minus(it), bestPathFinder)
    }.map {
        if (connectLoop) {
            it.plus(it.first())
        } else {
            it
        }
    })

/**
 * Visits all nodes in a list, using the provided pathfinder to decide what to visit next
 * Assumes a fully connected bidirectional graph
 */
fun visitAllNodes(
    visited: List<Node>,
    remaining: List<Node>,
    bestPathFinder: (nodes: List<List<Node>>) -> List<Node>
): List<Node> =
    if (remaining.isEmpty()) {
        visited
    } else {
        bestPathFinder(remaining.map {
            visitAllNodes(visited.plus(it), remaining.minus(it), bestPathFinder)
        })
    }

/**
 * Maximum / Maximal clique in an undirected graph
 *
 * https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
 *
 * From Todd Ginsberg's blog post: https://todd.ginsberg.com/post/advent-of-code/2018/day23/
 */
class BronKerbosch<T>(private val neighbors: Map<T, Set<T>>) {

    private var bestR: Set<T> = emptySet()

    fun largestClique(): Set<T> {
        execute(neighbors.keys)
        return bestR
    }

    private fun execute(
        p: Set<T>,
        r: Set<T> = emptySet(),
        x: Set<T> = emptySet()
    ) {
        if (p.isEmpty() && x.isEmpty()) {
            // We have found a potential best R value, compare it to the best so far.
            if (r.size > bestR.size) bestR = r
        } else {
            val mostNeighborsOfPandX: T = (p + x).maxBy { neighbors.getValue(it).size }!!
            val pWithoutNeighbors = p.minus(neighbors[mostNeighborsOfPandX]!!)
            pWithoutNeighbors.forEach { v ->
                val neighborsOfV = neighbors[v]!!
                execute(
                    p.intersect(neighborsOfV),
                    r + v,
                    x.intersect(neighborsOfV)
                )
            }
        }
    }
}
