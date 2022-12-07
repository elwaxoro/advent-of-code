package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 7: No Space Left On Device
 */
class Dec07 : PuzzleDayTester(7, 2022) {

    /**
     * Find directories with total size less than 100000 and add them up
     */
    override fun part1(): Any = loader().let { root ->
        root.findDirs().sumOf {
            val size = it.calcSize()
            if (size <= 100000) {
                size
            } else {
                0
            }
        }
    }// == 1582412L

    /**
     * Find directories >= the space required for the upgrade file
     * Pick the smallest one for deletion
     */
    override fun part2(): Any = loader().let { root ->
        val spaceRequired = maxOf(0, 30000000 - (70000000 - root.calcSize()))
        root.findDirs().filter { it.size >= spaceRequired }.minOf { it.size }
    }// == 3696336L

    /**
     * every command starts with $ and is followed by 1 or more additional newlines before the next $, so it's the perfect delimiter for this
     * start at the root, then run every command and consume any output
     * at the end, return a single FSNode marking the root of the filesystem
     */
    private fun loader(): FSNode = load(delimiter = "$").map { it.trim() }.filter { it.isNotEmpty() }.let { terminal ->
        FSNode(name = "/", isDir = true, parent = null).also { root ->
            terminal.fold(root) { dir, cmdAndOutput ->
                val split = cmdAndOutput.split("\n")
                val cmd = split.first()
                val output = split.drop(1)
                var workingDir = dir

                if (cmd.startsWith("cd")) {
                    val target = cmd.replace("cd ", "")
                    workingDir = when (target) {
                        "/" -> root
                        ".." -> workingDir.parent!!
                        else -> workingDir.children.first { it.name == target }
                    }
                } else if (cmd.startsWith("ls")) {
                    output.forEach {
                        if (it.startsWith("dir")) {
                            workingDir.children.add(FSNode(name = it.replace("dir ", ""), isDir = true, parent = workingDir))
                        } else {
                            val (size, name) = it.split(" ")
                            workingDir.children.add(FSNode(name = name, isDir = false, size = size.toLong(), parent = workingDir))
                        }
                    }
                }

                workingDir
            }
        }
    }

    private data class FSNode(
        val name: String,
        val isDir: Boolean,
        var size: Long = -1,
        val parent: FSNode?,
        val children: MutableList<FSNode> = mutableListOf(),
    ) {

        /**
         * Directory size is the sum of all files and subdirectories
         */
        fun calcSize(): Long = if (size >= 0) {
            size
        } else {
            children.sumOf { it.calcSize() }.also { size = it } // cache it
        }

        /**
         * Recursively builds a list of all directories or subdirectories from this node
         */
        fun findDirs(): List<FSNode> = if (isDir) {
            listOf(this).plus(children.filter { it.isDir }.flatMap { it.findDirs() })
        } else {
            listOf()
        }
    }
}
