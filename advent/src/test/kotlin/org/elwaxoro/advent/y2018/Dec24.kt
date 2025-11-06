package org.elwaxoro.advent.y2018

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.component6
import org.elwaxoro.advent.component7
import kotlin.math.min

/**
 * Day 24: Immune System Simulator 20XX
 */
class Dec24 : PuzzleDayTester(24, 2018) {

    /**
     * Kinda lazy here but since we know only one team survives in part 1, just add up all the survivors. One side will be 0 so whatever
     */
    override fun part1(): Any = loader().let { armies -> armies.fightToTheDeath().let { (immune, infection) -> immune + infection } } == 21199

    /**
     * Simple iteration from boost = 0 until immune system wins
     * Was going to do this with binary search, but iteration solved it in 50 loops
     */
    override fun part2(): Any = loader().let { armies ->
        var survivors: Pair<Int, Int> = 1 to 1
        var boost = 0
        do {
            survivors = armies.fightToTheDeath(boost)
            boost++
        } while (survivors.second > 0)
        survivors.first
    } == 5761

    /**
     * Loop fights until no more units are being killed:
     * One side with units = that side won
     * Both sides with units = stalemate, neither side has sufficient atk to finish off the other
     * Returns pair of Immune System survivors to Infection survivors
     */
    private fun List<Pair<String, List<Group>>>.fightToTheDeath(boost: Int = 0): Pair<Int, Int> {
        val (immune, infection) = this
        var deaths: Int

        // only immune system gets buffs
        immune.second.forEach { it.reset(boost) }
        infection.second.forEach { it.reset() }

        do {
            // active groups have living units, sorted by effective power then initiative
            val activeGroups = immune.second.plus(infection.second).sortedWith(compareBy({ -it.effectivePower() }, { -it.initiative })).filter { it.units > 0 }
            val availableTargets = activeGroups.toMutableList()
            deaths = activeGroups.map { group ->
                group to group.selectTarget(availableTargets)?.also { availableTargets.remove(it) }
            }.filter { it.second != null }.sortedWith(compareBy { -it.first.initiative }).sumOf { (atk, def) ->
                def?.defend(atk) ?: 0
            }
        } while (deaths > 0)

        return immune.second.sumOf { it.units } to infection.second.sumOf { it.units }
    }

    /**
     * Returns a list of Pair<String, List<Group>>
     * Immune System first, then Infection
     * This is a gross pile of format hacking and a lil regex
     */
    private fun loader() = load(delimiter = "\n\n").map { section ->
        val lines = section.split("\n")
        val team = lines.first().replace(":", "")
        team to lines.drop(1).mapIndexed { idx, line ->
            """
                (\d+) units each with (\d+) hit points (\(.*\) )*with an attack that does (\d+) (\w+) damage at initiative (\d+)
            """.trimIndent().toRegex().matchEntire(line)!!.groupValues.let { (whole, units, hp, stats, atk, atkType, initiative) ->
                var weak = "none"
                var immune = "none"
                stats.replace("(", "").replace(")", "").split(";").filterNot { it.isBlank() }.map { stat ->
                    if (stat.contains("weak")) {
                        weak = stat.replace("weak to ", "").trim()
                    } else if (stat.contains("immune")) {
                        immune = stat.replace("immune to ", "").trim()
                    } else {
                        throw IllegalStateException("Parse error for $line")
                    }
                }
                Group(team, "$team ${idx + 1}", units.toInt(), hp.toInt(), atk.toInt(), atkType, initiative.toInt(), weak, immune)
            }
        }
    }

    private data class Group(
        val team: String,
        val name: String,
        var units: Int,
        val hp: Int,
        val atk: Int,
        val atkType: String,
        val initiative: Int,
        val weak: String,
        val immune: String,
        var boost: Int = 0,
    ) {

        val ogUnits: Int = units

        fun reset(newBoost: Int = 0) {
            units = ogUnits
            boost = newBoost
        }

        fun effectiveAtk() = atk + boost

        /**
         * Assumption: don't freeze power during target phase (ex: units killed before this group attacks)
         */
        fun effectivePower() = units * effectiveAtk()

        fun selectTarget(groups: List<Group>): Group? =
            groups.map { g -> g to calcDmg(g) }
                .filter { it.second > 0 }
                .sortedWith(compareBy({ -it.second }, { -it.first.effectivePower() }, { -it.first.initiative }))
                .firstOrNull()?.first

        fun calcDmg(target: Group): Int =
            when {
                team == target.team -> 0 // no friendly fire
                target.immune.contains(atkType) -> 0
                target.weak.contains(atkType) -> effectivePower() * 2
                else -> effectivePower()
            }

        fun defend(attacker: Group): Int =
            min(units, attacker.calcDmg(this) / hp).also { units -= it }
    }
}
