package org.elwaxoro.advent

import java.util.PriorityQueue

/**
 * ad-hoc wrapper class for doing stuff with graphs based purely on
 * what's needed for past challenges with no thought given to future need
 * Nodes connect to other Nodes via edges
 *
 * IMPORTANT!!! Node's name must be unique to distinguish it from other nodes
 */
data class Node(
    val name: String,
    val canVisit: Boolean = true,
    val canLeave: Boolean = true,
    val canRevisit: Boolean = false
) {
    val edges = mutableMapOf<Node, Int>()
    var parent: Node? = null

    var scratch: Int = 0
    var coord: Coord? = null

    var shortestPath: List<Node> = emptyList()
    var shortestDistance: Int = Int.MAX_VALUE

    override fun toString(): String = name

    fun cost(node: Node): Int = edges[node] ?: error("No edge from $name to ${node.name}")

    fun addEdge(node: Node, cost: Int = 0) {
        edges[node] = cost
    }

    /**
     * Resets Dijkstra state on this node and all reachable nodes so dijkstra() can be re-run safely
     */
    fun resetPaths(visited: MutableSet<Node> = mutableSetOf()) {
        if (!visited.add(this)) return
        shortestDistance = Int.MAX_VALUE
        shortestPath = emptyList()
        edges.keys.forEach { it.resetPaths(visited) }
    }

    /**
     * PriorityQueue-backed Dijkstra from this node to all reachable nodes.
     */
    fun dijkstra() {
        resetPaths()
        shortestDistance = 0

        val settled = mutableSetOf<Node>()
        val queue = PriorityQueue<Node>(compareBy { it.shortestDistance })
        queue.add(this)

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            if (!settled.add(current)) continue

            for ((neighbor, edgeCost) in current.edges) {
                if (neighbor in settled) continue
                val candidateDist = current.shortestDistance + edgeCost
                if (candidateDist < neighbor.shortestDistance) {
                    neighbor.shortestDistance = candidateDist
                    neighbor.shortestPath = current.shortestPath + current
                    queue.add(neighbor)
                }
            }
        }
    }

    fun printify(): String = "$name: $scratch: ${edges.mapKeys { it.key.name }}"

    /**
     * Cycle-safe depth calculation
     */
    fun calculateAllDepths(depth: Int = 0, visited: MutableSet<Node> = mutableSetOf()) {
        if (!visited.add(this)) return
        this.scratch = depth
        edges.keys.forEach { it.calculateAllDepths(depth + 1, visited) }
    }

    /**
     * Follow parent links to root (with cycle guard)
     */
    fun traceRoot(): List<Node> {
        val path = mutableListOf<Node>()
        var curr: Node? = this
        val seen = mutableSetOf<Node>()
        while (curr != null && seen.add(curr)) {
            path.add(curr)
            curr = curr.parent
        }
        return path
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
        bestR = emptySet()
        execute(neighbors.keys)
        return bestR
    }

    private fun execute(
        p: Set<T>,
        r: Set<T> = emptySet(),
        x: Set<T> = emptySet()
    ) {
        if (p.isEmpty() && x.isEmpty()) {
            if (r.size > bestR.size) bestR = r
            return
        }

        val pivot = (p + x).maxBy { neighbors[it]?.size ?: 0 }
        val candidates = p - neighbors[pivot].orEmpty()

        for (v in candidates) {
            val neighborsOfV = neighbors[v].orEmpty()
            execute(
                p = p.intersect(neighborsOfV),
                r = r + v,
                x = x.intersect(neighborsOfV)
            )
        }
    }
}

/**
 * A generic stateless functional dijkstra/bfs that works with more things than just Node
 * Example (weighted 2D grid where each cell's Int value is the cost to enter it):
 * ```
 * val cost = shortestPath(
 *     start = Coord(0, 0),
 *     isGoal = { it == Coord(maxX, maxY) },
 *     neighbors = { pos ->
 *         pos.neighbors()
 *             .filter { it in grid }
 *             .map { next -> next to grid[next] }
 *     }
 * )
 * ```
 */
fun <S> shortestPath(
    start: S,
    isGoal: (S) -> Boolean,
    neighbors: (S) -> Iterable<Pair<S, Int>>
): Int? {
    val dist = mutableMapOf(start to 0)
    val queue = PriorityQueue<Pair<S, Int>>(compareBy { it.second })
    queue.add(start to 0)

    while (queue.isNotEmpty()) {
        val (state, cost) = queue.poll()
        if (isGoal(state)) return cost
        if (cost > (dist[state] ?: Int.MAX_VALUE)) continue

        for ((next, stepCost) in neighbors(state)) {
            val newCost = cost + stepCost
            if (newCost < (dist[next] ?: Int.MAX_VALUE)) {
                dist[next] = newCost
                queue.add(next to newCost)
            }
        }
    }
    return null
}
