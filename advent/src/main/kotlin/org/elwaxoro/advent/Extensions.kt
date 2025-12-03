package org.elwaxoro.advent

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.pow

/**
 * Note: NOT sum of prime factors, just all factors
 */
fun Int.sumOfFactors(): Int = (1..this).fold(0) { acc, i -> (acc + i).takeIf { this % i == 0 } ?: acc }

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
 * Creates a hex string MD5 hash of the input string
 */
private val MD5 = MessageDigest.getInstance("MD5")
fun String.md5(): String = BigInteger(1, MD5.digest(toByteArray())).toString(16).padStart(32, '0')

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

fun String.takeSplit(n: Int): List<String> = listOf(take(n), drop(n))

/**
 * A lot of puzzle parsing seems to require a chain of .replace("remove dumb thing", "")
 * This does it all in one shot: "Puzzle input X=123, Y=456".remove("Puzzle input X=", " Y=") returns "123,456"
 */
fun String.remove(vararg str: String) = str.fold(this) { acc, rep -> acc.replace(rep, "") }

fun <K, V> Pair<K, V>.toEntry() = object : Map.Entry<K, V> {
    override val key: K = first
    override val value: V = second
}

infix fun Long.pow(exponent: Int): Long = toDouble().pow(exponent).toLong()
infix fun Long.pow(exponent: Long): Long = toDouble().pow(exponent.toDouble()).toLong()
infix fun Int.pow(exponent: Int): Long = toDouble().pow(exponent).toLong()
infix fun Int.pow(exponent: Long): Long = toDouble().pow(exponent.toDouble()).toLong()
infix fun Long.xor(other: Int): Long = this xor other.toLong()

fun <T> List<List<T>>.cartesianProduct(): List<List<T>> =
    drop(1).fold(first().map { listOf(it) }) { acc, set ->
        acc.flatMap { list ->
            set.map { element -> list + element }
        }
    }

// found online as a translation of python's itertools function
fun <T> Iterable<T>.combinations(length: Int): Sequence<List<T>> =
    sequence {
        val pool = this@combinations as? List<T> ?: toList()
        val n = pool.size
        if (length > n) return@sequence
        val indices = IntArray(length) { it }
        while (true) {
            yield(indices.map { pool[it] })
            var i = length
            do {
                i--
                if (i == -1) return@sequence
            } while (indices[i] == i + n - length)
            indices[i]++
            for (j in i + 1 until length) indices[j] = indices[j - 1] + 1
        }
    }

// found online as a translation of python's itertools function
fun <T> Iterable<T>.permutations(length: Int? = null): Sequence<List<T>> =
    sequence {
        val pool = this@permutations as? List<T> ?: toList()
        val n = pool.size
        val r = length ?: n
        if (r > n) return@sequence
        val indices = IntArray(n) { it }
        val cycles = IntArray(r) { n - it }
        yield(List(r) { pool[indices[it]] })
        if (n == 0) return@sequence
        cyc@ while (true) {
            for (i in r - 1 downTo 0) {
                cycles[i]--
                if (cycles[i] == 0) {
                    val temp = indices[i]
                    for (j in i until n - 1) indices[j] = indices[j + 1]
                    indices[n - 1] = temp
                    cycles[i] = n - i
                } else {
                    val j = n - cycles[i]
                    indices[i] = indices[j].also { indices[j] = indices[i] }
                    yield(List(r) { pool[indices[it]] })
                    continue@cyc
                }
            }
            return@sequence
        }
    }

// kotlin only destructures up to 5, these add a few more
operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]
