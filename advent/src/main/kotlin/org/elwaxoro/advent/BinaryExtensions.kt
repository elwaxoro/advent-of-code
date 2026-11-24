package org.elwaxoro.advent

/*
 * Doin stuff with 1s and 0s yo
 * Note: on these puzzles with java/kotlin it's easier to just use a list of integers where each item is a 1 or a 0, then collapse if/when needed
 * If starting from a string of 1s and 0s, use split and work with List<Int>
 * If starting from an integer, use toBinaryString
 * Can also use Long.toString(radix = 2) in a lot of places
 */


/**
 * String.encodeToByteArray() can also be helpful depending on what's needed
 * ByteArray to hexadecimal string
 * ex:
 * 6d9b3bf5bf5fc15c0e49081f9b160da6
 */
fun ByteArray.toHexString(): String = joinToString("") { (0xFF and it.toInt()).toString(16).padStart(2, '0') }

/**
 * Takes a list of 1s and 0s and turns it into a single integer
 */
fun Iterable<Int>.toBinaryInt(): Int = fold(0) { acc, bit ->
    require(bit == 0 || bit == 1) { "Invalid bit: $bit" }
    (acc shl 1) or bit
}

fun Iterable<Int>.toBinaryLong(): Long = fold(0L) { acc, bit ->
    require(bit == 0 || bit == 1) { "Invalid bit: $bit" }
    (acc shl 1) or bit.toLong()
}

/**
 * starting from a hex char (0-9a-f), produce 4 bit binary string
 * 0 = 0000
 * f = 1111
 */
fun hexCharTo4BitBinary(hexChar: Char): String {
    return hexChar.digitToInt(16)
        .toString(2)
        .padStart(4, '0')
}

/**
 * 0s become 1s, 1s become 0s
 */
fun List<Int>.bitFlip(): List<Int> = map {
    when (it) {
        1 -> 0
        0 -> 1
        else -> throw IllegalStateException("can't bit flip $it")
    }
}

fun String.hexToBinary(): String = map { hexCharTo4BitBinary(it) }.joinToString("")