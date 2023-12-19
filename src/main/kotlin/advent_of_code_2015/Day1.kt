package advent_of_code_2015

import getFileContent

object Day1 {
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            val input = getFileContent("2015/day1.txt")

            val floor = input
                .fold(0) { currFloor, direction ->
                    when(direction) {
                        ')' -> currFloor-1
                        '(' -> currFloor+1
                        else -> throw RuntimeException("Invalid char $direction")
                    }
                }

            println(floor)
        }
    }

    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            val input = getFileContent("2015/day1.txt")

            var floor = 0

            input.forEachIndexed { index: Int, direction: Char ->
                when(direction) {
                    ')' -> floor--
                    '(' -> floor++
                    else -> throw RuntimeException("Invalid char $direction")
                }

                if(floor < 0) {
                    println(index + 1)
                    return
                }
            }
        }
    }
}