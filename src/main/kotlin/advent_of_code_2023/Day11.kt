package advent_of_code_2023

import getFileContent
import printTimedResult
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Day11 {
    object Part1_Example {
        @JvmStatic fun main(args: Array<String>) =
            run(Input.EXAMPLE, expectedOutput = 374, expansionRate = 2)
    }

    object Part1_Main {
        @JvmStatic fun main(args: Array<String>) =
            run(Input.MAIN, expectedOutput = 9799681, expansionRate = 2)
    }

    object Part2_Example1 {
        @JvmStatic fun main(args: Array<String>) =
            run(Input.EXAMPLE, expectedOutput = 1030, expansionRate = 10)
    }

    object Part2_Example2 {
        @JvmStatic fun main(args: Array<String>) =
            run(Input.EXAMPLE, expectedOutput = 8410, expansionRate = 100)
    }

    object Part2_Main {
        @JvmStatic fun main(args: Array<String>) =
            run(Input.MAIN, expectedOutput = null, expansionRate = 1_000_000) //FIXME
    }

    fun run(input: Input, expectedOutput: Int? = null, expansionRate: Int) {
        printTimedResult(expectedValue = expectedOutput) {
            val starMap = parseInput(input.get())

            starMap.galaxyPairs
                .sumOf { (a, b) ->
                    val distance = manhattanDistance(a, b)
                    val rowExpansionOffset = expansionOffset(a.row, b.row, starMap.expandedRows) * (expansionRate - 1)
                    val colExpansionOffset = expansionOffset(a.col, b.col, starMap.expandedCols) * (expansionRate - 1)

                    val expandedDistance = distance + rowExpansionOffset + colExpansionOffset

                    expandedDistance
                }
        }
    }

    enum class Input(private val filename: String) {
        MAIN("2023/day11.txt"),
        EXAMPLE("2023/day11_example1.txt");

        fun get(): String = getFileContent(filename)
    }

    private data class Position2D(val row: Int, val col: Int) {
        override fun toString(): String {
            return "($row, $col)"
        }
    }

    private data class Size2D(val rowNum: Int, val colNum: Int) {
        override fun toString(): String {
            return "{$rowNum, $colNum}"
        }

        val rows: IntRange
            get() = 0..<rowNum

        val cols: IntRange
            get() = 0..<colNum
    }

    private fun manhattanDistance(pos1: Position2D, pos2: Position2D): Int {
        return abs(pos1.row - pos2.row) + abs(pos1.col - pos2.col)
    }

    private fun expansionOffset(x1: Int, x2: Int, expanded: Set<Int>): Int {
        return (min(x1, x2)..max(x1,x2)).count { it in expanded }
    }

    private fun <T> List<T>.combinations(): List<Pair<T, T>> {
        val combinations = mutableListOf<Pair<T, T>>()

        for(i in 0..<(size - 1)) {
            for(j in (i+1)..<(size)) {
                combinations += this[i] to this[j]
            }
        }

        return combinations.toList()
    }

    private data class StarMap(
        val galaxies: List<Position2D>,
        val size: Size2D,
    ) {
        val expandedRows by lazy { size.rows.toSet() - galaxies.map { it.row }.toSet() }
        val expandedCols by lazy { size.cols.toSet() - galaxies.map { it.col }.toSet() }

        val galaxyPairs by lazy { galaxies.combinations() }
    }

    private fun parseInput(input: String): StarMap {
        val grid = input
            .split("\n")
            .map { it.toList() }

        val size = Size2D(rowNum = grid.size, colNum = grid.first().size)
        val galaxies = mutableListOf<Position2D>()

        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                if(cell == '#') {
                    galaxies += Position2D(rowIndex, colIndex)
                }
            }
        }

        return StarMap(galaxies, size)
    }
}