package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester

/**
 * Day 24: Immune System Simulator 20XX
 *
 * germs vs immune system!
 *
 * 18 units each with 729 hit points (weak to fire; immune to cold, slashing)
 *  with an attack that does 8 radiation damage at initiative 10
 *
 *  effective power: unit count * attack damage
 *  fights: phase 1 target, phase 2 attack
 *  target select: decreasing order of power, group picks targets. in a tie, use initiative
 *  target select: pick target based on weakness to attack type (ignore unit balance in defender). in a tie, use effective power then initiative then nothing (if can't damage anything due to immunities)
 *  target select: each defender can only be chosen once?
 *  attack: each attacker attacks its defender, if any
 *  attack: decreasing initiative
 *  attack: atk power = effective power. immune def = 0, weak = 2x
 *  defend: only lose whole units
 *  don't target, attack or defend if unit count = 0
 *
 *  repeat until one army is empty
 */
class Dec24: PuzzleDayTester(24, 2018) {

    override fun part1(): Any = loader()

    private fun loader() = load(delimiter = "\n\n\n").map { lines ->
        println(lines)

    }
}
