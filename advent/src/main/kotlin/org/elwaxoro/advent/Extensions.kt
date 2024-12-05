package org.elwaxoro.advent

import java.math.BigInteger

/**
 * Nullable Long plusser, null defaults to 0
 */
fun Long.plusNull(that: Long?): Long = (that ?: 0L) + this

/**
 * Split a string into a list of integers
 * Ex: "01234" becomes [0, 1, 2, 3, 4]
 */
fun String.splitToInt(): List<Int> = map(Character::getNumericValue)

/**
 * Replace matching values
 */
fun List<Int>.replace(oldInt: Int, newInt: Int): List<Int> = map { it.takeUnless { it == oldInt } ?: newInt }

/**
 * Based on https://stackoverflow.com/questions/9562605/in-kotlin-can-i-create-a-range-that-counts-backwards
 * Kotlin ranges don't support positive or negative directions at the same time
 */
fun Int.toward(to: Int): IntProgression = IntProgression.fromClosedRange(this, to, 1.takeIf { this <= to } ?: -1)

/**
 * Pads a progression of Int to the desired size, using the final Int as the pad value
 */
fun IntProgression.padTo(newSize: Int): List<Int> = toList().padTo(newSize)

/**
 * Pads a list of anything to the desired size, using the final object as the pad object
 * ex
 * listOf(1,2,3).padTo(10)
 * becomes
 * [1, 2, 3, 3, 3, 3, 3, 3, 3, 3]
 */
fun <T> List<T>.padTo(newSize: Int): List<T> = takeIf { size >= newSize } ?: plus(List(newSize - size) { last() })

/**
 * Splits a list into two, one with the first N elements the other with the remainder of the original list
 * I wanted something like partition or windowed, except with the first part having a fixed size and the second part being the entire remainder
 */
fun <T> List<T>.takeSplit(n: Int): Pair<List<T>, List<T>> = take(n) to drop(n)

/**
 * Makes a copy of a list missing the element at the given index
 */
fun <T> List<T>.dropAt(idx: Int): List<T> = toMutableList().also { it.removeAt(idx) }

fun <T> List<T>.swapAt(from: Int, to: Int): List<T> = toMutableList().also { ret ->
    ret[to] = this[from]
    ret[from] = this[to]
}

/**
 * Get the median from a list of Int
 */
fun List<Int>.median(): Double = sorted().let {
    if (size % 2 == 0) {
        (it[size / 2] + it[size / 2 - 1]) / 2.0
    } else {
        it[size / 2].toDouble()
    }
}

/**
 * Merge a list of maps by adding up values for matching keys
 * Merge values of matching keys using the provided merge function
 */
fun <T, U> List<Map<T, U>>.merge(merger: (value: U, existing: U?) -> U): Map<T, U> = fold(mutableMapOf()) { acc, map ->
    acc.also {
        map.entries.map { (key, value) ->
            acc[key] = merger(value, acc[key])
        }
    }
}

/**
 * Generates all possible permutations of the provided list
 * Should be "in-order" depending on your definition of "in-order" when it comes to permutations
 * It's in some sort of order, anyway
 * Ex: [A, B, C] becomes [[A, B, C], [A, C, B], [B, A, C], [B, C, A], [C, A, B], [C, B, A]]
 *
 * NOTE: this will only work on lists up to size 8 or so without running the jvm out of memory, so I guess if you really need to go that hard, give it more RAMs
 * 8: 40,320 combos
 * 9: 362,880 combos
 * 10: 3,628,800 combos
 */
fun <T> List<T>.permutations(): List<List<T>> =
    (0..lastIndex).fold(listOf(listOf<T>() to this)) { acc, _ ->
        acc.flatMap { (permutation, candidates) ->
            candidates.map { permutation.plus(it) to candidates.minus(it) }
        }
    }.map { it.first }

/**
 * greatest common factor (GCF, HCF, GCD) for a list
 */
fun List<BigInteger>.gcd(): BigInteger = fold(BigInteger.ZERO) { acc, int -> acc.gcd(int) }

/**
 * least common multiple (LCM, LCD) for a list
 */
fun Collection<BigInteger>.lcm(): BigInteger = fold(BigInteger.ONE) { acc, int -> acc * (int / int.gcd(acc)) }

/**
 * Turns string into regex, finds all groups from input, extracts them and returns as a list
 * I'm kind of uncertain why normal findAll makes this difficult
 */
fun String.findAllNonStupid(input: String): List<String> = toRegex().findAll(input).toList().map { it.groupValues[0] }
