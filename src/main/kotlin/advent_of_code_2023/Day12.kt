package advent_of_code_2023

import GenericBacktracker
import getFileContent
import printTimedResult

object Day12 {
    object Part1Example {
        @JvmStatic fun main(args: Array<String>) =
            run(Input.EXAMPLE, expectedOutput = 21, unfold = false)
    }

    object Part1Main {
        @JvmStatic fun main(args: Array<String>) =
            run(Input.MAIN, expectedOutput = 7286, unfold = false)
    }

    object Part2Example {
        @JvmStatic fun main(args: Array<String>) =
            run(Input.EXAMPLE, expectedOutput = 525152, unfold = true)
    }

    object Part2Main { //TODO remove : too low = 1313645061
        @JvmStatic fun main(args: Array<String>) =
            run(Input.MAIN, expectedOutput = null, unfold = true)
    }

    fun run(input: Input, expectedOutput: Int? = null, unfold: Boolean = false) {
        printTimedResult(expectedValue = expectedOutput) {
            parseInput(input.get())
                .let {
                    if(unfold) {
                        it .map { arrangement ->
                            arrangement.copy(
                                springs = (1..5).joinToString(
                                    separator = "${SpringState.UNKNOWN.label}",
                                    prefix = "",
                                    postfix = ""
                                ) { arrangement.springs },
                                records = (1..5).flatMap {
                                    arrangement.records
                                }
                            )
                        }
                    } else {
                        it
                    }
                }
                .sumOf {
                    getNumArrangements(it)
                }
        }
    }

    enum class Input(private val filename: String) {
        MAIN("2023/day12.txt"),
        EXAMPLE("2023/day12_example1.txt");

        fun get(): String = getFileContent(filename)
    }

    private data class SpringArrangement(
        val springs: String,
        val records: List<Int>,
    )

    private enum class SpringState(val label: Char) {
        DAMAGED('#'), OPERATIONAL('.'), UNKNOWN('?');
    }

    private fun parseInput(input: String): List<SpringArrangement> {
        return input.split("\n").map { line ->
            line.split(" ").let { (positions, recordsString) ->
                SpringArrangement(
                    positions,//.map { springLabel -> SpringState.entries.single { it.label == springLabel } }
                    recordsString.split(",").map { it.toInt() }
                )
            }
        }
    }

    private val cache = mutableMapOf<SpringArrangement, Int>()

    private fun getNumArrangements(springArrangement: SpringArrangement): Int {
        return cache.getOrPut(springArrangement) computation@ {
            if (springArrangement.springs.isEmpty()) {
                return@computation if (springArrangement.records.isEmpty())
                    1
                else
                    0
            }

            if (springArrangement.records.isEmpty()) {
                return@computation if (springArrangement.springs.none { it == SpringState.DAMAGED.label }) {
                    1
                } else {
                    0
                }
            }

            if (springArrangement.springs.startsWith(SpringState.OPERATIONAL.label)) {
                return@computation getNumArrangements(
                    springArrangement = springArrangement.copy(
                        springs = springArrangement.springs.let {
                            var springs = it
                            while (springs.startsWith(SpringState.OPERATIONAL.label)) {
                                springs = springs.drop(1)
                            }
                            springs
                        }
                    )
                )
            }

            val currentSpring = springArrangement.springs.first()
            val restOfSprings = springArrangement.springs.drop(1)
            assert(currentSpring != SpringState.OPERATIONAL.label)

            return@computation if (currentSpring == SpringState.DAMAGED.label) {
                val record = springArrangement.records.first()

                val springsCanFitGroup by lazy {
                    springArrangement.springs.length >= record
                }
                val groupIsFulfilled by lazy {
                    springArrangement.springs.take(record).none { it == SpringState.OPERATIONAL.label }
                }
                val springsIsOverOrCanFitMoreGroups by lazy {
                    springArrangement.springs.length == record || springArrangement.springs.drop(record)
                        .first() != SpringState.DAMAGED.label
                }

                if (springsCanFitGroup && groupIsFulfilled && springsIsOverOrCanFitMoreGroups) {
                    getNumArrangements(
                        SpringArrangement(
                            springs = springArrangement.springs.drop(record + 1),
                            records = springArrangement.records.drop(1),
                        )
                    )
                } else {
                    0
                }
            } else {
                getNumArrangements(
                    SpringArrangement(
                        springs = "#$restOfSprings",
                        records = springArrangement.records,
                    )
                ) + getNumArrangements(
                    SpringArrangement(
                        springs = "$restOfSprings",
                        records = springArrangement.records,
                    )
                )
            }
        }
    }
}