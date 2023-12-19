package advent_of_code_2023

import getFileContent

object Day2 {
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            val result = getFileContent("2023/day2.txt")
                .split("\n")
                .map(Day2::parseGame)
                .filter { gameIsPossible(it) }
                .sumOf { it.id }
                .also(::println)

            assert(result == 2369)
        }

        private val maxColors = mapOf(
            Color.red to 12,
            Color.green to 13,
            Color.blue to 14,
        )

        private fun gameIsPossible(game: Game): Boolean {
            return game.sets.all { setIsPossible(it) }
        }

        private fun setIsPossible(set: Map<Color, Int>): Boolean {
            return set.all { (extractedColor, extractedAmount) ->
                extractedAmount <= maxColors[extractedColor]!!
            }
        }
    }

    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            val result = getFileContent("2023/day2.txt")
                .split("\n")
                .map(Day2::parseGame)
                .sumOf { getPower(it) }
                .also(::println)

            assert(result == 66363)
        }

        private fun getPower(game: Game): Int {
            return Color.entries.fold(1) { acc, color ->
                acc * game.sets.maxOf { it[color] ?: 0 }
            }
        }
    }

    private enum class Color {
        blue, green, red;
    }

    private class Game(
        val id: Int,
        val sets: List<Map<Color, Int>>,
    )

    private fun parseGame(line: String): Game {
        val (first, second) = line.split(":", limit = 2)
        val id = first.drop("Game ".length).toInt()

        val sets = second.split(";")
            .map {
                it.split(",")
                    .associate { colorExtraction ->
                        colorExtraction.trim()
                            .split(" ", limit = 2)
                            .let { (first, second) ->
                                Color.entries.single { it.name == second } to first.toInt()
                            }
                    }
            }

        return Game(id, sets)
    }
}