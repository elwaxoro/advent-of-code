package org.elwaxoro.advent.y2023

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.lcm

/**
 * Pulse Propagation
 */
class Dec20 : PuzzleDayTester(20, 2023) {

    override fun part1(): Any = loader().let { map ->
        val button = map["button"]!!
        repeat(1000) {
            button.queuePulse("", true)
            var children = button.pulse()
            while (children.isNotEmpty()) {
                children = children.flatMap { it.pulse() }
            }
        }
        val highs = map.map { it.value.highCounter }.sum()
        val lows = map.map { it.value.lowCounter }.sum()
        highs * lows
    } == 825167435L

    override fun part2(): Any = loader().let { map ->
        val button = map["button"]!!
        val targets = map["rx"]!!.inputs.values.single().inputs.values
        val found = mutableMapOf<String, Long>()

        var pressCount = 1L
        while (found.size != targets.size) {
            button.queuePulse("", true)
            var children = button.pulse()
            while (children.isNotEmpty()) {
                children = children.flatMap { it.pulse() }
            }
            targets.forEach {
                if (!found.containsKey(it.name) && it.lowCounter > 0) {
                    found[it.name] = pressCount
                }
            }
            pressCount++
        }
        found.map { it.value.toBigInteger() }.lcm().toLong()
    } == 225514321828633L

    private fun loader() = load().map {
        val (description, outputs) = it.split(" -> ")
        when {
            description == "broadcaster" -> Broadcaster(outputs)
            description.startsWith("%") -> FlipFlop(description.drop(1), outputs)
            description.startsWith("&") -> Conjunction(description.drop(1), outputs)
            else -> throw IllegalStateException("omg")
        }
    }.plus(Button()).plus(Output("output")).associateBy { it.name }.toMutableMap()
        .also { map ->
            map.values.toList().map { it.connectOutputs(map) }
        }

    private abstract class Module(val name: String, val rawOutputs: String) {
        val inputs = mutableMapOf<String, Module>()
        val outputs = mutableListOf<Module>()
        var highCounter = 0L
        var lowCounter = 0L

        val inputQueue = mutableListOf<Pair<String, Boolean>>()

        open fun queuePulse(inputName: String, isLowPower: Boolean) {
            if (isLowPower) {
                lowCounter++
            } else {
                highCounter++
            }
            inputQueue.add(inputName to isLowPower)
        }

        /**
         * Handle the incoming pulse, return true if something changed?
         */
        fun pulse(): List<Module> {
            val (inputName, isLowPower) = inputQueue.removeFirst()
//            println("$inputName -${"low".takeIf { isLowPower }?: "high"}-> $name (${inputQueue.size})")
            return doPulse(inputName, isLowPower)
        }

        abstract fun doPulse(inputName: String, isLowPower: Boolean): List<Module>

        open fun addInput(input: Module) {
            inputs[input.name] = input
        }

        open fun connectOutputs(map: MutableMap<String, Module>) {
            if (rawOutputs.isNotBlank()) {
                rawOutputs.split(",").map { it.trim() }.map { outputName ->
                    val output = map.getOrPut(outputName) { Output(outputName) }
                    outputs.add(output)
                    output.addInput(this)
                }
            }
        }
    }

    private class Broadcaster(rawOutputs: String) : Module("broadcaster", rawOutputs) {

        var isLow = false

        override fun doPulse(inputName: String, isLowPower: Boolean): List<Module> {
            isLow = isLowPower
            outputs.map { it.queuePulse(name, isLow) }
            return outputs
        }

        override fun toString(): String = "$name high: $highCounter low: $lowCounter"
    }

    private class FlipFlop(name: String, rawOutputs: String) : Module(name, rawOutputs) {

        // true = isLowPower
        var isOff = true

        override fun doPulse(inputName: String, isLowPower: Boolean): List<Module> =
            if (isLowPower) {
                isOff = !isOff
                outputs.map { it.queuePulse(name, isOff) }
                outputs
            } else {
                listOf()
            }

        override fun toString(): String = "$name high: $highCounter low: $lowCounter isOff: $isOff"
    }

    private class Conjunction(name: String, rawOutputs: String) : Module(name, rawOutputs) {

        // true = isLowPower
        val mostRecentInput = mutableMapOf<String, Boolean>()

        override fun doPulse(inputName: String, isLowPower: Boolean): List<Module> {
            mostRecentInput[inputName] = isLowPower
            // send a low power pulse if ALL mostRecentInput are false
            val sendLowPower = mostRecentInput.none { it.value }
            outputs.map { it.queuePulse(name, sendLowPower) }
            return outputs
        }

        override fun addInput(input: Module) {
            super.addInput(input)
            mostRecentInput[input.name] = true
        }

        override fun toString(): String = "$name high: $highCounter low: $lowCounter tracker: $mostRecentInput"
    }

    private class Button : Module("button", "broadcaster") {

        override fun queuePulse(inputName: String, isLowPower: Boolean) {
            inputQueue.add(inputName to isLowPower)
        }

        override fun doPulse(inputName: String, isLowPower: Boolean): List<Module> {
            outputs.map { it.queuePulse(name, true) }
            return outputs
        }

        override fun toString(): String = "$name high: $highCounter low: $lowCounter"
    }

    private class Output(name: String) : Module(name, "") {

        override fun doPulse(inputName: String, isLowPower: Boolean): List<Module> {
//            println("Output got a ${"low".takeIf { isLowPower }?:"high"} pulse from $inputName")
            return outputs
        }

        override fun toString(): String = "$name high: $highCounter low: $lowCounter"

        override fun connectOutputs(map: MutableMap<String, Module>) {
            // do nothing, no outputs
        }
    }
}
