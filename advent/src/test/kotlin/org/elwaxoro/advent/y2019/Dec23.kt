package org.elwaxoro.advent.y2019

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 23: Category Six
 */
class Dec23 : PuzzleDayTester(23, 2019) {

    override fun part1(): Any = runTheThing(NAT()) == 22829L

    override fun part2(): Any = runTheThing(NAT2()) == 15678L

    /**
     * This is part 1, refactored to have a NAT which owns the 255 channel
     */
    private fun runTheThing(nat: NAT) = runBlocking {
        val code = loadToLong(delimiter = ",")

        val network = (0..49).associateWith {
            Channel<Packet>(capacity = Channel.UNLIMITED).also { c -> c.send(Packet(-1, it, listOf(it.toLong()))) }
        }.toMutableMap()

        val computers = (0..49).map { Computer(code, it, network) }

        // setup the NAT
        nat.network = network
        nat.computers = computers
        network[nat.address] = Channel(capacity = Channel.UNLIMITED)

        // fire everything up!
        computers.plus(nat).map {
            async { it.run() }
        }.joinAll()

        nat.output
    }

    private data class Packet(
        val from: Int,
        val to: Int,
        val data: List<Long>
    )

    private interface SuspendRunner {
        suspend fun run()
    }

    private open class NAT(
        val address: Int = 255,
        var network: Map<Int, Channel<Packet>> = mapOf(),
        var computers: List<Computer> = listOf(),
        var output: Long = -1,
    ) : SuspendRunner {

        /**
         * Part 1:
         * Just wait for a packet to show up, done
         */
        override suspend fun run() {
            while (output < 0) {
                delay(1)
                network[address]!!.tryReceive().getOrNull()?.let {
                    output = it.data.last()
                }
                // safety cutoff in case things get stuck, don't run forever
                if (computers.all { it.isIdle() }) {
                    kill()
                }
            }
            kill()
        }

        suspend fun kill() {
            network.forEach { (k, v) ->
                if (k != address) {
                    v.send(Packet(address, k, listOf(Long.MAX_VALUE)))
                }
            }
        }
    }

    private class NAT2(
        var lastPacket: Packet? = null,
        var lastY: Long = -1,
    ) : NAT() {

        /**
         * Part 2:
         * store last packet, wait for network idle, send last packet
         * if last packet's Y is the same twice in a row: done
         */
        override suspend fun run() {
            while (output < 0) {
                delay(1)
                network[address]!!.tryReceive().getOrNull()?.let { lastPacket = it }
                if (computers.all { it.isIdle() }) {
                    if (lastY == lastPacket?.data?.last()) {
                        output = lastY
                    } else {
                        val send = lastPacket!!
                        lastY = send.data.last()
                        network[0]?.send(send)
                    }
                }
            }
            kill()
        }
    }

    private class Computer(
        val code: List<Long>,
        val address: Int,
        val network: Map<Int, Channel<Packet>>,
        val inputBuffer: MutableList<Long> = mutableListOf(),
        val outputBuffer: MutableList<Long> = mutableListOf(),
    ) : SuspendRunner {

        var idle = 0

        /**
         * seems to work at 2, but cpu timing is maybe a factor here?
         */
        fun isIdle(): Boolean = idle >= 2

        /**
         * Try to read a packet from the channel, dump it into the input buffer
         * If input buffer is empty, increase idle counter and return -1
         */
        suspend fun input(): Long {
            delay(1) // always give up the thread
            val res = network[address]!!.tryReceive()
            res.getOrNull()?.let {
                inputBuffer.addAll(it.data)
            }

            if (inputBuffer.isNotEmpty()) {
                idle = 0
                return inputBuffer.removeFirst()
            } else {
                idle++
                return -1L
            }
        }

        /**
         * Wait for output buffer to have a full packet, then send it
         */
        suspend fun output(out: Long) {
            delay(1)
            outputBuffer.add(out)
            if (outputBuffer.size == 3) {
                val packet = Packet(address, outputBuffer.removeFirst().toInt(), outputBuffer.take(2))
                outputBuffer.clear()
                network[packet.to]!!.send(packet)
            }
        }

        override suspend fun run() {
            ElfCode(code).runner(
                setup = ElfCode.memExpander(500),
                input = { input() },
                output = { output(it) }
            )
        }
    }
}
