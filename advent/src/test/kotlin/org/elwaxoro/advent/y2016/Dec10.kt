package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 10: Balance Bots
 *
 * Not the greatest organization here but it worked!
 */
class Dec10 : PuzzleDayTester(10, 2016) {

    override fun part1(): Any = loader().let { bots ->
        connectBots(bots)
        bots.filter { (_, v) -> v.input.containsAll(listOf(61, 17)) }.keys.single()
    } == 86

    override fun part2(): Any = loader().let { bots ->
        val output = connectBots(bots)
        output[0] * output[1] * output[2]
    } == 22847

    private fun connectBots(bots: Map<Int, Bot>): List<Int> {
        val output = MutableList(25) { -1 }
        while (bots.any { (_, v) -> v.input.size < 2 }) {
            bots.forEach { (_, bot) ->
                if (bot.input.size == 2) {
                    val high = bot.input.max()
                    val low = bot.input.min()
                    if (bot.highIsBot) {
                        bots[bot.highTarget]!!.input.add(high)
                    } else {
                        output[bot.highTarget] = high
                    }
                    if (bot.lowIsBot) {
                        bots[bot.lowTarget]!!.input.add(low)
                    } else {
                        output[bot.lowTarget] = low
                    }
                }
            }
        }
        return output
    }

    private fun loader() = load().let { lines ->
        val bots = mutableMapOf<Int, Bot>()
        lines.forEach { line ->
            if (line.startsWith("value")) {
                val (value, key) = """value (\d+) goes to bot (\d+).*""".toRegex().find(line)!!.destructured
                val bot = bots.getOrPut(key.toInt(), ::Bot)
                bot.input.add(value.toInt())
            } else {
                val (key, lowType, low, highType, high) = """bot (\d+) gives low to (\w+) (\d+) and high to (\w+) (\d+)""".toRegex().find(line)!!.destructured
                val bot = bots.getOrPut(key.toInt(), ::Bot)
                bot.lowIsBot = lowType == "bot"
                bot.lowTarget = low.toInt()
                bot.highIsBot = highType == "bot"
                bot.highTarget = high.toInt()
            }
        }
        bots
    }

    data class Bot(
        val input: MutableSet<Int> = mutableSetOf(),
        var lowIsBot: Boolean = false,
        var lowTarget: Int = -1,
        var highIsBot: Boolean = false,
        var highTarget: Int = -1,
    )
}
