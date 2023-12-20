package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Aplenty
 */
class Dec19 : PuzzleDayTester(19, 2023) {

    /**
     * 449531
     */
    override fun part1(): Any = loader().let { (map, parts) ->
        parts.map { part ->
            var key = "in"
            val history = mutableListOf(key)
            while (key != "A" && key != "R") {
                key = map[key]!!.runWorkflow(part)
                history.add(key)
            }
            part to history
        }.filter { it.second.last() == "A" }.sumOf { it.first.score() }
    }

    private fun String.runWorkflow(part: Part): String {
        val checks = split(",")
        val applied = checks.first { check ->
            val split = check.split(":")
            if (split.size == 1) {
                true
            } else {
                val comparison = split.first()
                val amount = comparison.replace("[a-z<>]*".toRegex(), "").toInt()
                val category = part.category(comparison[0])
                if (comparison.contains("<")) {
                    category < amount
                } else {
                    category > amount
                }
            }
        }
        return applied.split(":").last()
    }

    /**
     * 122756210763577
     */
    override fun part2(): Any = loader().let { (map, _) ->
        val explore = mutableListOf("in" to PartRange())
        val accepted = mutableListOf<PartRange>()
        while (explore.isNotEmpty()) {
            val (exploreKey, exploreRange) = explore.removeFirst()

            when (exploreKey) {
                "A" -> accepted.add(exploreRange)
                "R" -> {
                    // do nothing
                }
                else -> {
                    val workflow = map[exploreKey]!!
                    workflow.split(",").fold(exploreRange) { activeRange, check ->
                        if (activeRange.xmas.isEmpty()) {
                            activeRange
                        } else {
                            if (check.contains("<")) {
                                val (xmasKey, r) = check.split("<")
                                val (l, workflowDest) = r.split(":")
                                val limit = l.toInt()
                                val xmas = activeRange.xmas[xmasKey]!!
                                if (xmas.first >= limit) {
                                    // activeRange does not apply to this check at all, skip any split and continue to next part of workflow
                                    activeRange
                                } else if (xmas.last < limit) {
                                    // activeRange fully applies to THIS part of the workflow, do not process additional workflow steps
                                    explore.add(workflowDest to activeRange)
                                    PartRange.blank()
                                } else {
                                    // split the activeRange into two sets: one to explore, one to process additional workflow steps
                                    val passing = (xmas.first until limit)
                                    val remainder = (limit..xmas.last)
                                    explore.add(workflowDest to activeRange.replace(xmasKey, passing))
                                    activeRange.replace(xmasKey, remainder)
                                }
                            } else if (check.contains(">")) {
                                val (xmasKey, r) = check.split(">")
                                val (l, workflowDest) = r.split(":")
                                val limit = l.toInt()
                                val xmas = activeRange.xmas[xmasKey]!!
                                if (xmas.last < limit) {
                                    // activeRange does not apply to this check at all, skip any split and continue to next part of workflow
                                    activeRange
                                } else if (xmas.first >= limit) {
                                    // activeRange fully applies to THIS part of the workflow, do not process additional workflow steps
                                    explore.add(workflowDest to activeRange)
                                    PartRange.blank()
                                } else {
                                    // split the activeRange into two sets: one to explore, one to process additional workflow steps
                                    val passing = (limit + 1..xmas.last)
                                    val remainder = (xmas.first..limit)
                                    explore.add(workflowDest to activeRange.replace(xmasKey, passing))
                                    activeRange.replace(xmasKey, remainder)
                                }
                            } else {
                                explore.add(check to activeRange)
                                activeRange
                            }
                        }
                    }
                }
            }
        }
        accepted.sumOf { it.score() }
    }

    private data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {

        fun category(check: Char): Int = when (check) {
            'x' -> x
            'm' -> m
            'a' -> a
            's' -> s
            else -> throw IllegalStateException("PART ASPLODE")
        }

        fun score(): Long = (x + m + a + s).toLong()
    }

    private data class PartRange(
        val xmas: Map<String, IntRange> = listOf("x", "m", "a", "s").associateWith { (1..4000) }
    ) {
        companion object {
            fun blank(): PartRange = PartRange(emptyMap())
        }

        fun replace(key: String, range: IntRange): PartRange = PartRange(xmas.plus(key to range))

        fun score(): Long = xmas.values.map { it.last - it.first + 1L }.reduce(Long::times)
    }

    private fun loader() = load(delimiter = "\n\n").let { (w, p) ->
        val workflows = w.split("\n").map {
            val (key, workflow) = it.replace("}", "").split("{")
            key to workflow
        }
        val parts = p.split("\n").map { it.toPart() }
        workflows.toMap() to parts
    }

    private fun String.toPart(): Part = replace("[{}=xmas]*".toRegex(), "").split(",").map { it.toInt() }.let { (x, m, a, s) ->
        Part(x, m, a, s)
    }
}
