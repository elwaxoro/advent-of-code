package org.elwaxoro.advent.y2017

import org.elwaxoro.advent.Coord3D
import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 20: Particle Swarm
 */
class Dec20 : PuzzleDayTester(20, 2017) {

    /**
     * acceleration adds to velocity every step
     * velocity adds to position every step
     * "closest to 0 in the long term" sounds like the particle with the lowest acceleration (if tie then lowest velocity, if tie then closest Manhattan distance to 0)?
     * as everything else will zip off into infinity?
     * is acceleration and velocity magnitude just the sum of the absolute values?
     * ^^^ totally worked!
     * 91
     */
    override fun part1(): Any = loader().let { particles ->
        val zero = Coord3D(0, 0, 0)
        val sorted = particles.sortedWith(
            compareBy<Particle> { it.acc.absMagnitude() }
                .thenBy { it.vel.absMagnitude() }
                .thenBy { it.pos.manhattan(zero) }
        )
        particles.indexOf(sorted.first())
    }

    /**
     * There are 1000 particles in the simulation
     * idea: after each step, put all particle positions into a groupBy P and throw out everyone where the group size > 1
     * this might take forever depending on how horribly the simulator is designed
     * idea: if a particle is moving away from every other particle, it will never collide and can be ignored:
     * compare manhattan distances to every other active particle
     * move everyone
     * delete collisions
     * compare manhattan distances to every other active particle (if ALL of these are bigger than the befores, this particle is moving away from everyone)
     */
    override fun part2(): Any = loader().let { particles ->
        var active = particles.removeCollisions()
        
        while (active.hasPotentialCollisions()) {
            active = active.map { it.move() }.removeCollisions()
        }
        
        active.size
    }

    private fun List<Particle>.hasPotentialCollisions(): Boolean {
        for (i in indices) {
            for (j in i + 1 until size) {
                if (!this[i].canNeverCollideWith(this[j])) return true
            }
        }
        return false
    }
    
    /**
     * Faster than comparing everything to everything, use groupBy and drop any groups with a size greater than 1
     */
    private fun List<Particle>.removeCollisions(): List<Particle> = groupBy { it.pos }.values.filter { it.size == 1 }.flatten()

    private data class Particle(
        val pos: Coord3D,
        val vel: Coord3D,
        val acc: Coord3D,
        val name: Int
    ) {
        fun move(): Particle {
            val newVel = vel + acc
            val newPos = pos + newVel
            return Particle(newPos, newVel, acc, name)
        }

        fun canNeverCollideWith(other: Particle): Boolean {
            val dp = pos - other.pos
            val dv = vel - other.vel
            val da = acc - other.acc

            return isDiverging(dp.x, dv.x, da.x) ||
                    isDiverging(dp.y, dv.y, da.y) ||
                    isDiverging(dp.z, dv.z, da.z)
        }
        
        private fun isDiverging(dp: Int, dv: Int, da: Int): Boolean =
            (dp > 0 && dv >= 0 && da >= 0) || (dp < 0 && dv <= 0 && da <= 0)
    }

    private fun loader() = load().mapIndexed { idx, line ->
        val (p, v, a) = line.split(", ").map { parse3dVec(it) }
        Particle(p, v, a, idx)
    }

    private fun parse3dVec(str: String) = Coord3D.parse(str.drop(3).dropLast(1))
}
