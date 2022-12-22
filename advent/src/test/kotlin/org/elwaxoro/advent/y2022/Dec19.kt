package org.elwaxoro.advent.y2022

import org.elwaxoro.advent.PuzzleDayTester

class Dec19 : PuzzleDayTester(19, 2022) {

    override fun part1(): Any = loader().map { factory ->
        val sim = Simulation(factory, 24)
        val states = (0..24).fold(listOf(sim)) { sims, ctr ->
            sims.flatMap { it.cycle() }
                .also { println("after rep $ctr the raw size is ${it.size}") }
            .filter { sim ->
                when {
//                    sim.timeLeft < 2 -> (sim.resources[Resource.GEODE]!! > 0)//.also { println("time left is zero, no geodes") }
//                    sim.timeLeft < 5 -> (sim.robots[Robot.GEODE_ROBOT]!! > 0)//.also { println("5 mins to go, no geode robot") }
                    sim.timeLeft < 4 -> (sim.robots[Robot.OBSIDIAN_ROBOT]!! > 0)//.also {println("8 mins to go, no obsidian robot")}
                    sim.timeLeft < 15 -> (sim.robots[Robot.CLAY_ROBOT]!! > 0)//.also{println("9 mins to go, no clay")}
                    sim.timeLeft < 16 -> (sim.robots[Robot.ORE_ROBOT]!! > 1)//.also { println("10 mins to go, only 1 ore") }
                    else -> true
                }
            }.also { println("after rep $ctr the filtered size is ${it.size}") }
                .toSet().also { println("after going down to set size: ${it.size}") }
                .sortedByDescending { it.potential() }.take(100000)
        }
        val best = states.maxBy { it.resources[Resource.GEODE] ?: 0 }
        val mostGeodes = states.maxOf { it.resources[Resource.GEODE] ?: 0 }
        println("Max costs: ${factory.maxCostMap}")
        println("After all reps, states: ${states.size} best: $mostGeodes : $best")
        factory.name * mostGeodes
    }.sum()

    override fun part2(): Any {
        return super.part2()
    }

    private fun loader() = load().map { settings ->
        val (fullName, costs) = settings.split(":")
        val name = fullName.replace("Blueprint ", "").toInt()
        val costMap = costs.split(".").filter { it.isNotBlank() }.associate { cost ->
            Robot.fromString(cost) to cost.substringAfter("costs ").split(" and ").associate { it.resourceCost() }
        }
        RobotFactory(name, costMap)
    }

    data class Simulation(
        val factory: RobotFactory,
        val timeLeft: Int,
        val resources: Map<Resource, Int> = mapOf(
            Resource.ORE to 0,
            Resource.CLAY to 0,
            Resource.OBSIDIAN to 0,
            Resource.GEODE to 0
        ),
        val robots: Map<Robot, Int> = mapOf(
            Robot.ORE_ROBOT to 1,
            Robot.CLAY_ROBOT to 0,
            Robot.OBSIDIAN_ROBOT to 0,
            Robot.GEODE_ROBOT to 0
        ),
    ) {
        fun potential(): Int = (resources[Resource.GEODE] ?: 0) + ((robots[Robot.GEODE_ROBOT] ?: 0) * timeLeft)// + (robots[Robot.OBSIDIAN_ROBOT] ?: 0)

        fun isFinished() = timeLeft == 0

        fun cycle(): List<Simulation> =
            if (isFinished()) {
                listOf(this)
            } else {
                // get resources
                val newResources = robots.map { (t, u) ->
                    t.produces to (resources[t.produces] ?: 0) + u
                }.toMap()

                val hasClay = (robots[Robot.CLAY_ROBOT] ?: 0) > 0
                val hasObsidian = (robots[Robot.OBSIDIAN_ROBOT] ?: 0) > 0
                val hasGeode = (robots[Robot.GEODE_ROBOT] ?: 0) > 0

                val buildableRobots = factory.costMap.filter { (robot, cost) ->
                    // can we afford this robot?
                    cost.all { rc -> (resources[rc.key] ?: 0) >= rc.value }
                }

                val futurePossibleRobots = if((robots[Robot.OBSIDIAN_ROBOT] ?: 0) > 0) {

                } else if ((robots[Robot.CLAY_ROBOT] ?: 0) > 0) {

                    } else {

                }

                // mark possible robots to build (use resource amounts from before new resources appear)
                // fan out with new robots (can only build up to one a minute)

                val possibleRobots = factory.costMap.filter { (robot, cost) ->
                    // can we afford this robot?
                    cost.all { rc -> (resources[rc.key] ?: 0) >= rc.value }
                }
                // drop everything for a geode robot
                if (possibleRobots.containsKey(Robot.GEODE_ROBOT)) {
                    val newRobots = robots.map { (r, t) ->
                        r to ((t + 1).takeIf { r == Robot.GEODE_ROBOT } ?: t)
                    }.toMap()
                    val updatedResources = newResources.map { (r, c) ->
                        r to (c - (factory.costMap[Robot.GEODE_ROBOT]!![r] ?: 0))
                    }.toMap()
                    listOf(Simulation(factory, timeLeft - 1, updatedResources, newRobots))
                } else {
                    factory.costMap.filter { (robot, cost) ->
                        // can we afford this robot?
                        cost.all { rc -> (resources[rc.key] ?: 0) >= rc.value }
                    }.filter { (robot, cost) ->
                        (robots[robot] ?: 0) < 10 && (robots[robot] ?: 0) < (factory.maxCostMap[robot.produces] ?: 0)
//                    true
                    }.map { (robot, cost) ->
                        val newRobots = robots.map { (r, t) ->
                            r to ((t + 1).takeIf { r == robot } ?: t)
                        }.toMap()
                        val updatedResources = newResources.map { (r, c) ->
                            r to (c - (cost[r] ?: 0))
                        }.toMap()
                        Simulation(factory, timeLeft - 1, updatedResources, newRobots)
                    }.plus(Simulation(factory, timeLeft - 1, newResources, robots))
                }
            }
    }

    data class RobotFactory(
        val name: Int,
        val costMap: Map<Robot, Map<Resource, Int>>,
    ) {
        val maxCostMap: Map<Resource, Int>
        init {
            maxCostMap = maxCosts()
        }

        fun maxCosts() = costMap.values.flatMap { it.map { it.key to it.value } }.groupBy { it.first }.map { it.key to it.value.maxOf { it.second } }.toMap()
    }

    private fun String.resourceCost(): Pair<Resource, Int> =
        Resource.values().single { contains(it.text) } to replace("\\D".toRegex(), "").toInt()

    enum class Robot(val text: String, val produces: Resource) {
        ORE_ROBOT("ore robot", Resource.ORE),
        CLAY_ROBOT("clay robot", Resource.CLAY),
        OBSIDIAN_ROBOT("obsidian robot", Resource.OBSIDIAN),
        GEODE_ROBOT("geode robot", Resource.GEODE);

        companion object {
            fun fromString(str: String): Robot = values().single { robot ->
                str.contains(robot.text)
            }
        }
    }

    enum class Resource(val text: String) {
        ORE("ore"),
        CLAY("clay"),
        OBSIDIAN("obsidian"),
        GEODE("geode");
    }
}
