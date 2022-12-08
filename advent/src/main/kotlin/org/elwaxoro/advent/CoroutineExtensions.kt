package org.elwaxoro.advent

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

/**
 * For runs with static input size, convert a list to a channel and close it
 */
suspend fun <T> List<T>.toChannel(close: Boolean = true) =
    Channel<T>(capacity = Channel.UNLIMITED).also { channel ->
        forEach {
            channel.send(it)
        }
        if (close) {
            channel.close()
        }
    }

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Channel<T>.drainToList(): List<T> =
    mutableListOf<T>().also {
        while (!isClosedForReceive) {
            it.add(receive())
        }
    }
