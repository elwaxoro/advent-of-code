package org.elwaxoro.advent.y2022

import kotlinx.serialization.json.*
import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.sign

/**
 * Day 13: Distress Signal
 */
class Dec13 : PuzzleDayTester(13, 2022) {

    /**
     * add up indexes of pairs in the correct order
     */
    override fun part1(): Any = loader().mapIndexed { idx, (a, b) ->
        if (compare(a, b) < 1) {
            idx + 1
        } else {
            0
        }
    }.sum() == 5393

    /**
     * flatten everything into a big list. add the decoder keys. sort. multiply indexes of the decoder keys. done!
     */
    override fun part2(): Any = loader().flatten().plus(listOf(two, six)).sortedWith(comparator).let { sorted ->
        (sorted.indexOf(two) + 1) * (sorted.indexOf(six) + 1)
    } == 26712

    private fun loader(): List<List<JsonElement>> = load(delimiter = "\n\n").map { it.split("\n").map(Json::parseToJsonElement) }

    private val comparator = Comparator<JsonElement> { a, b -> compare(a, b) }
    private val two = Json.parseToJsonElement("[[2]]")
    private val six = Json.parseToJsonElement("[[6]]")

    /**
     * Compare rules:
     * If both values are integers, the lower integer should come first
     * If exactly one value is an integer, convert the integer to a list which contains that integer as its only value, then retry the comparison
     * If both values are lists, compare values till run out. If all match, compare left list size to right lift size
     */
    private fun compare(l: JsonElement, r: JsonElement): Int =
        if (l is JsonPrimitive && r is JsonPrimitive) {
            (l.jsonPrimitive.int - r.jsonPrimitive.int).sign
        } else if (l is JsonPrimitive || r is JsonPrimitive) {
            compare(l.ensureArray(), r.ensureArray())
        } else {
            l.jsonArray.zip(r.jsonArray).map { (a, b) -> compare(a, b) }.firstOrNull { it != 0 }
                ?: (l.jsonArray.size - r.jsonArray.size).sign
        }

    private fun JsonElement.ensureArray(): JsonElement =
        if (this is JsonPrimitive) {
            Json.parseToJsonElement("[${jsonPrimitive.int}]")
        } else {
            this
        }
}
