package advent_of_code_2023

import getFileContent

object Day3 {
    //512794
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            val lines = getFileContent("2023/day3.txt").split("\n")

            val schematic = parseSchematic(lines)

            val sum = schematic
                .numbers
                .filter { (number, positions) ->
                    val adjacentPositions = positions
                        .flatMap { it.adjacentPositions() }
                        .toSet()
                        .minus(positions.toSet())

                    val isNumberAdjacentToSymbol = schematic.symbols
                        .any { it.position in adjacentPositions }

                    isNumberAdjacentToSymbol
                }
                .sumOf { (number, positions) -> number }

            println(sum)
        }
    }

    //67779080
    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            val lines = getFileContent("2023/day3.txt").split("\n")

            val schematic = parseSchematic(lines)

            val positionToNumberMap = schematic.numbers
                .flatMap { number ->
                    number.positions.map { position ->
                        position to number
                    }
                }
                .toMap()

            val sum = schematic.symbols
                .filter { it.value == '*' }
                .map { symbol ->
                    symbol.position.adjacentPositions()
                        .mapNotNull { position ->
                            positionToNumberMap[position]
                        }
                        .distinct()
                }
                .filter { it.size == 2 }
                .sumOf { numbersInGear ->
                    val (first, second) = numbersInGear

                    first.value * second.value
                }

            println(sum)
        }
    }

    private data class Position2D(val row: Int, val col: Int) {
        operator fun plus(other: Position2D): Position2D {
            return Position2D(row = this.row + other.row, col = this.col + other.col)
        }
        fun adjacentPositions(): List<Position2D> {
            return listOf(
                this + Position2D(row = 0, col = +1),
                this + Position2D(row = +1, col = +1),
                this + Position2D(row = +1, col = 0),
                this + Position2D(row = +1, col = -1),
                this + Position2D(row = 0, col = -1),
                this + Position2D(row = -1, col = -1),
                this + Position2D(row = -1, col = 0),
                this + Position2D(row = -1, col = +1),
            )
        }
    }

    private data class Number(val value: Int, val positions: List<Position2D>)
    private data class Symbol(val value: Char, val position: Position2D)

    private data class Schematic(val numbers: List<Number>, val symbols: List<Symbol>)

    private fun parseSchematic(lines: List<String>): Schematic {
        val numbers = mutableListOf<Number>()
        val symbols = mutableListOf<Symbol>()

        lines.forEachIndexed { row, line ->
            val currentNumber = mutableListOf<Pair<Char, Position2D>>()

            fun onNumberComplete() {
                val newNumber = currentNumber
                    .fold("" to emptyList<Position2D>()) { acc, curr ->
                        (acc.first + curr.first) to (acc.second + curr.second)
                    }
                    .let {
                        Number(
                            value = it.first.toInt(),
                            positions = it.second,
                        )
                    }

                numbers += newNumber

                currentNumber.clear()
            }

            line.forEachIndexed { col, char ->
                if(char.isDigit()) {
                    currentNumber += char to Position2D(row, col)
                } else {
                    if(currentNumber.isNotEmpty()) {
                        onNumberComplete()
                    }

                    if(char != '.') {
                        symbols += Symbol(
                            value = char,
                            position = Position2D(row, col)
                        )
                    }
                }

                if(col == line.lastIndex && currentNumber.isNotEmpty()) {
                    onNumberComplete()
                }
            }
        }

        return Schematic(
            numbers = numbers.toList(),
            symbols = symbols.toList(),
        )
    }

    //private val symbols = listOf('*', '+', '=', '%', '-', '#', '@', '/', '&', '$')
}