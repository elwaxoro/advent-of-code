package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester
import kotlin.math.max

/**
 * Day 22: Wizard Simulator 20XX
 */
class Dec22 : PuzzleDayTester(22, 2015) {

    private var bestWin = Int.MAX_VALUE

    override fun part1(): Any = battle(50, 500, 55, 8) ?: -1

    override fun part2(): Any = reset().let { battle(50, 500, 55, 8, isHardMode = true) ?: -1 }

    private fun reset(): Int = Int.MAX_VALUE.also { bestWin = it }

    private fun battle(pHp: Int, pMan: Int, bHp: Int, atk: Int, spells: Map<Spell, Int> = mapOf(), isPlayerTurn: Boolean = true, manaSpent: Int = 0, isHardMode: Boolean = false): Int? {
        // player loses the instant player health hits 0 (or they fail to cast a spell)
        // player wins after player health check when boss health hits 0
        // player gets no non-magical armor
        // player starts with 50 health, 500 mana, no mana limit (unsure if health limit)
        // boss armor does nothing
        // boss damage always at least 1, up to atk
        // multi turn effects apply at the start of EACH turn (player and boss, decrease 1 each time)
        // can't cast an active spell
        // effect spell can end at start of turn and be re-cast end of player turn

        // update all the player stats, apply damage to boss (single round boss attacks like magic missile are applied here then removed from spell list)
        val newPhp = pHp + spells.keys.sumOf { it.heal } - (if (isHardMode && isPlayerTurn) 1 else 0)
        val newBhp = bHp - spells.keys.sumOf { it.atk }
        val newMan = pMan + spells.keys.sumOf { it.recharge }
        val newDef = spells.keys.sumOf { it.def }
        val newSpells = spells.mapValues { it.value - 1 }.filterValues { it > 0 }

        return if (newPhp <= 0 || manaSpent >= bestWin) {
            null
        } else if (newBhp <= 0) {
            bestWin = manaSpent
            manaSpent
        } else if (isPlayerTurn) {
            // for each inactive spell, cast it and recurse (instant spells are applied at the top of the next boss turn)
            // if no spells are cast, returns null
            Spell.entries.filterNot { newSpells.keys.contains(it) }.filter { it.mana <= newMan }.mapNotNull { newSpell ->
                battle(
                    pHp = newPhp,
                    pMan = newMan - newSpell.mana,
                    bHp = newBhp,
                    atk = atk,
                    spells = newSpells + (newSpell to newSpell.turns),
                    isPlayerTurn = false,
                    manaSpent = manaSpent + newSpell.mana,
                    isHardMode = isHardMode
                )
            }.minOrNull()
        } else {
            // attack the player, recurse
            battle(
                pHp = newPhp - max(1, atk - newDef),
                pMan = newMan,
                bHp = newBhp,
                atk = atk,
                spells = newSpells,
                isPlayerTurn = true,
                manaSpent = manaSpent,
                isHardMode = isHardMode
            )
        }
    }

    private enum class Spell(
        val mana: Int = 0,
        val atk: Int = 0,
        val def: Int = 0,
        val heal: Int = 0,
        val recharge: Int = 0,
        val turns: Int = 0,
    ) {
        MAGIC_MISSILE(mana = 53, atk = 4),
        DRAIN(mana = 73, atk = 2, heal = 2),
        SHIELD(mana = 113, def = 7, turns = 6),
        POISON(mana = 173, atk = 3, turns = 6),
        RECHARGE(mana = 229, recharge = 101, turns = 5)
    }
}
