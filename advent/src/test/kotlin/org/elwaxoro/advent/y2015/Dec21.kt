package org.elwaxoro.advent.y2015

import org.elwaxoro.advent.PuzzleDayTester
import org.elwaxoro.advent.combinations
import kotlin.math.max

/**
 * Item shop:
 * Weapons:    Cost  Damage  Armor
 * Dagger        8     4       0
 * Shortsword   10     5       0
 * Warhammer    25     6       0
 * Longsword    40     7       0
 * Greataxe     74     8       0
 *
 * Armor:      Cost  Damage  Armor
 * Leather      13     0       1
 * Chainmail    31     0       2
 * Splintmail   53     0       3
 * Bandedmail   75     0       4
 * Platemail   102     0       5
 *
 * Rings:      Cost  Damage  Armor
 * Damage +1    25     1       0
 * Damage +2    50     2       0
 * Damage +3   100     3       0
 * Defense +1   20     0       1
 * Defense +2   40     0       2
 * Defense +3   80     0       3
 *
 * Player HP:
 * 100
 *
 * Boss:
 * Hit Points: 100
 * Damage: 8
 * Armor: 2
 */
class Dec21 : PuzzleDayTester(21, 2015) {

    override fun part1(): Any = tryAllTheCombos { it }.min()

    override fun part2(): Any = tryAllTheCombos { !it }.max()

    private fun tryAllTheCombos(selector: (result: Boolean) -> Boolean) =
        weapons.flatMap { w ->
            armor.flatMap { a ->
                rings.combinations(2).mapNotNull { r ->
                    val gear = listOf(w, a) + r
                    val outcome = battle(
                        Fighter(100, gear.sumOf { it.atk }, gear.sumOf { it.def }),
                        Fighter(100, 8, 2)
                    )
                    gear.sumOf { it.cost }.takeIf { selector.invoke(outcome) }
                }
            }
        }

    private fun battle(player: Fighter, boss: Fighter): Boolean {
        while (player.hp > 0 && boss.hp > 0) {
            boss.hp -= max(player.atk - boss.def, 1)
            if (boss.hp > 0) {
                player.hp -= max(boss.atk - player.def, 1)
            }
        }
        return player.hp > 0
    }

    private val weapons = listOf(
        Item(8, 4), // dagger
        Item(10, 5), // shortsword
        Item(25, 6), // warhammer
        Item(40, 7), // longsword
        Item(74, 8), // greataxe
    )

    private val armor = listOf(
        Item(0, 0, 0), // nakers
        Item(13, 0, 1), // leather
        Item(31, 0, 2), // chainmail
        Item(53, 0, 3), // splintmail
        Item(75, 0, 4), // bandedmail
        Item(102, 0, 5), // platemail
    )

    private val rings = listOf(
        Item(0, 0, 0), // empty finger
        Item(0, 0, 0), // empty finger
        Item(25, 1), // +1 atk
        Item(50, 2), // +2 atk
        Item(100, 3), // +3 atk
        Item(20, 0, 1), // +1 def
        Item(40, 0, 2), // +2 def
        Item(80, 0, 3), // +3 def
    )

    private data class Item(
        val cost: Int,
        val atk: Int = 0,
        val def: Int = 0
    )

    private data class Fighter(
        var hp: Int,
        var atk: Int,
        var def: Int,
    )
}
