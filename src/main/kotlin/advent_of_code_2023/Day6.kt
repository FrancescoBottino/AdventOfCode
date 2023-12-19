package advent_of_code_2023

import getFileContent
import printTimedResult
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.time.measureTimedValue

object Day6 {
    //2756160
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult {
                inputPart1()
                    .map(::numberOfWaysToWin)
                    .fold(1) { acc, it -> acc * it }
            }
        }
    }

    //34788142
    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult {
                numberOfWaysToWin(inputPart2())
            }
        }
    }

    private data class Race(
        val time: Long,
        val recordDistance: Long,
    )

    private fun inputPart1(): List<Race> {
        val (timeLine, distanceLine) = getFileContent("2023/day6.txt")
            .split("\n")

        val times = timeLine.removePrefix("Time:")
            .split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
        val distances = distanceLine.removePrefix("Distance:")
            .split(" ").filter { it.isNotEmpty() }.map { it.toLong() }

        assert(times.size == distances.size)

        val races = times.mapIndexed { index, time ->
            Race(time, distances[index])
        }

        return races
    }

    private fun inputPart2(): Race {
        val (timeLine, distanceLine) = getFileContent("2023/day6.txt")
            .split("\n")

        val time = timeLine.removePrefix("Time:")
            .split(" ").filter { it.isNotEmpty() }.joinToString("").toLong()
        val distance = distanceLine.removePrefix("Distance:")
            .split(" ").filter { it.isNotEmpty() }.joinToString("").toLong()

        return Race(time, distance)
    }

    /**
     * distance traveled is equal to total time - time pressed, times time pressed
     *
     * total time: t
     * time pressed: x
     * recordDistance = d
     *
     * (t - x) * x = d => -x^2 + xt - d = 0
     *
     * equation solutions are min and max time pressed to beat record
     */
    private fun timePressRangeToWin(race: Race): IntRange {
        val t = race.time.toDouble()
        val d = race.recordDistance.toDouble()
        /**
         * D = b^2 - 4ac
         * so, t^2 - 4d
         */
        val determinant = t*t - 4*d

        assert(determinant > 0)

        /**
         * x 1,2 = (-b +- sqrt of D)/2
         * so
         * x1 = (t - sqrt of D) / 2
         * x2 = (t + sqrt of D) / 2
         */
        val determinantRoot = sqrt(determinant)
        val a = (t - determinantRoot) / 2
        val b = (t + determinantRoot) / 2

        val minTime = ceil(a + 0.0001).toInt()
        val maxTime = floor(b - 0.0001).toInt()

        return minTime..maxTime
    }

    private fun numberOfWaysToWin(race: Race): Int {
        return timePressRangeToWin(race)
            .let { range ->
                range.last - range.first + 1
            }
    }
}