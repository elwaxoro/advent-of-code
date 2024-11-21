package org.elwaxoro.advent.y2016

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.toHexString
import java.security.MessageDigest

/**
 * Day 5: How About a Nice Game of Chess?
 */
class Dec05: PuzzleDayTester(5, 2016) {

    override fun part1(): Any {
        val md = MessageDigest.getInstance("MD5")
        val salt = "ffykfhsq"
        var i = 0L
        var pass = ""
        while (pass.length < 8) {
            val hash = md.digest("$salt$i".toByteArray()).toHexString()
            if (hash.startsWith("00000")) {
                pass += hash[5]
            }
            i++
        }
        return pass
    }

    override fun part2(): Any {
        val md = MessageDigest.getInstance("MD5")
        val salt = "ffykfhsq"
        var i = 0L
        val pass = "--------".toMutableList()
        while (pass.contains('-')) {
            val hash = md.digest("$salt$i".toByteArray()).toHexString()
            if (hash.startsWith("00000") && hash[5] < '8') {
                val decrypted = hash[5].digitToInt()
                if (pass[decrypted] == '-') {
                    pass[decrypted] = hash[6]
                }
            }
            i++
        }
        return pass.joinToString("")
    }
}
