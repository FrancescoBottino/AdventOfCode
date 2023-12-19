package advent_of_code_2023

import getFileContent

object Day1 {
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            val result = getFileContent("2023/day1.txt")
                .split("\n")
                .sumOf {
                    val firstDigit = getFirstDigit(it)
                    val secondDigit = getFirstDigit(it.reversed())

                    "$firstDigit$secondDigit".toInt()
                }
                .also(::println)

            assert(result == 56042)
        }

        private fun getFirstDigit(string: String): Int {
            return string.first { it.isDigit() }.digitToInt()
        }
    }

    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            val result = getFileContent("2023/day1.txt")
                .split("\n")
                .sumOf {
                    extractNumber(it)
                }
                .also(::println)

            assert(result == 55358)
        }

        private val numbers: Map<String, Int> = listOf(
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine",
        ).mapIndexed { index, name -> name to index + 1 }
            .plus((1..9).map { it.toString() to it })
            .toMap()

        private fun extractNumber(string: String): Int {
            val onlyValidNumbers = numbers
                .entries
                .filter { string.contains(it.key) }

            val firstDigit = onlyValidNumbers
                .minBy { (numberString, _) ->
                    string.indexOf(numberString, 0, true)
                }
                .value

            val lastDigit = onlyValidNumbers
                .minBy { (numberString, _) ->
                    string.reversed().indexOf(numberString.reversed(), 0, true)
                }
                .value

            return "$firstDigit$lastDigit".toInt()
        }
    }
}