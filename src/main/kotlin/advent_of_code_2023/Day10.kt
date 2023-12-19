package advent_of_code_2023

import getFileContent
import printTimedResult

object Day10 {
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult(expectedValue = 7173) {
                getReachableTilesWithDistance(input()).values.max()
            }
        }
    }

    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult(expectedValue = 291) {
                val cellsMap = input()

                val loopPipesPositions = getReachableTilesWithDistance(cellsMap).keys

                val insideLoop = cellsMap.size.rows
                    .map { row ->
                        var inLoop = false

                        cellsMap.size.cols
                            .mapNotNull { col ->
                                val position = Position2D(row, col)

                                val isLoop = position in loopPipesPositions
                                val crossesLoop = cellsMap.cells[position]!!.pipeDirections.contains(Direction2D.NORTH)

                                if(isLoop && crossesLoop) {
                                    inLoop = !inLoop
                                }

                                position.takeIf { inLoop && !isLoop }
                            }
                    }
                    .flatten()

                insideLoop.size
            }
        }
    }

    private fun input(): CellMap {
        val lines = getFileContent("2023/day10.txt")
            .split("\n")

        val size = Size2D(lines.size, lines.first().length)
        var start: Position2D? = null

        val cells = lines
            .mapIndexed { rowIndex, row ->
                row.mapIndexed { colIndex, cellChar ->
                    val position = Position2D(rowIndex, colIndex)
                    val cell: Cell = when(cellChar) {
                        '|' -> Cell.Vertical
                        '-' -> Cell.Horizontal
                        'L' -> Cell.NorthToEast
                        'J' -> Cell.NorthToWest
                        '7' -> Cell.SouthToWest
                        'F' -> Cell.SouthToEast
                        '.' -> Cell.Empty
                        'S' -> {
                            start = position
                            Cell.StartPlaceholder
                        }
                        else -> throw RuntimeException("invalid char $cellChar")
                    }

                    position to cell
                }
            }
            .flatten()
            .toMap()

        return CellMap(
            size = size,
            start = start!!,
            cells = cells,
        ).let { withPlaceholder ->
            val startDirections = findLinkedPipes(withPlaceholder, start!!)
                .map { start!! - it }
            val newStart = Cell.Start(startDirections)
            withPlaceholder.copy(
                cells = withPlaceholder.cells.plus(start!! to newStart)
            )
        }
    }

    private data class Size2D(val rowNum: Int, val colNum: Int): Iterable<Position2D> {
        override fun toString(): String {
            return "{$rowNum, $colNum}"
        }

        fun isValid(position: Position2D): Boolean {
            return position.row in 0..<rowNum && position.col in 0..<colNum
        }

        val rows: IntRange
            get() = 0..<rowNum

        val cols: IntRange
            get() = 0..<colNum

        override fun iterator(): Iterator<Position2D> {
            return rows.map { row -> cols.map { col -> Position2D(row, col) } }
                .flatten()
                .iterator()
        }
    }
    private data class Direction2D(val rowOffset: Int, val colOffset: Int) {
        companion object {
            val NORTH = Direction2D(-1, 0)
            val SOUTH = Direction2D(+1, 0)
            val EAST = Direction2D(0, +1)
            val WEST = Direction2D(0, -1)

            val orthogonals = listOf(
                NORTH,
                SOUTH,
                EAST,
                WEST,
            )
        }

        operator fun plus(other: Direction2D): Direction2D {
            return Direction2D(this.rowOffset + other.rowOffset, this.colOffset + other.colOffset)
        }

        operator fun times(amount: Int): Direction2D {
            return Direction2D(this.rowOffset * amount, this.colOffset * amount)
        }

        override fun toString(): String {
            return "[$rowOffset, $colOffset]"
        }

        fun inverted(): Direction2D {
            return Direction2D(this.rowOffset * -1, this.colOffset * -1)
        }
    }
    private data class Position2D(val row: Int, val col: Int) {
        operator fun plus(other: Direction2D): Position2D {
            return Position2D(this.row + other.rowOffset, this.col + other.colOffset)
        }

        operator fun minus(other: Position2D): Direction2D {
            return Direction2D(other.row - this.row, other.col - this.col)
        }

        override fun toString(): String {
            return "($row, $col)"
        }
    }

    private sealed interface Cell {
        val pipeDirections: List<Direction2D>

        data object Empty: Cell {
            override fun toString() = " "

            override val pipeDirections: List<Direction2D>
                get() = emptyList()
        }

        data object StartPlaceholder: Cell {
            override fun toString() = "S"

            override val pipeDirections: List<Direction2D>
                get() = Direction2D.orthogonals
        }

        data class Start(
            override val pipeDirections: List<Direction2D>
        ): Cell {
            override fun toString() = "S"
        }

        data object Vertical: Cell {
            override fun toString() = "┃"

            override val pipeDirections: List<Direction2D>
                get() = listOf(Direction2D.NORTH, Direction2D.SOUTH)
        }

        data object Horizontal: Cell {
            override fun toString() = "━"

            override val pipeDirections: List<Direction2D>
                get() = listOf(Direction2D.EAST, Direction2D.WEST)
        }

        data object NorthToEast: Cell {
            override fun toString() = "┗"

            override val pipeDirections: List<Direction2D>
                get() = listOf(Direction2D.NORTH, Direction2D.EAST)
        }

        data object NorthToWest: Cell {
            override fun toString() = "┛"

            override val pipeDirections: List<Direction2D>
                get() = listOf(Direction2D.NORTH, Direction2D.WEST)
        }

        data object SouthToWest: Cell {
            override fun toString() = "┓"

            override val pipeDirections: List<Direction2D>
                get() = listOf(Direction2D.SOUTH, Direction2D.WEST)
        }

        data object SouthToEast: Cell {
            override fun toString() = "┏"

            override val pipeDirections: List<Direction2D>
                get() = listOf(Direction2D.SOUTH, Direction2D.EAST)
        }
    }

    private data class CellMap(
        val size: Size2D,
        val start: Position2D,
        val cells: Map<Position2D, Cell>,
    ) {
        override fun toString(): String {
            return size.toString() + "\n" + gridToString(size, cells)
        }
    }

    private fun <T> gridToString(size: Size2D, grid: Map<Position2D, T>): String {
        return (0..<size.rowNum).joinToString("\n") { row ->
            (0..<size.colNum).joinToString("") { col ->
                grid[Position2D(row, col)]!!.toString()
            }
        }
    }
    
    private fun findLinkedPipes(cellMap: CellMap, currentPipe: Position2D): List<Position2D> {
        return cellMap.cells[currentPipe]!!
            .pipeDirections
            .mapNotNull { direction ->
                (currentPipe + direction)
                    .takeIf { cellMap.size.isValid(it) }
                    .takeIf { neighborPosition ->
                        val neighbor = cellMap.cells[neighborPosition]

                        neighbor?.pipeDirections?.contains(direction.inverted()) ?: false
                    }
            }
    }

    private fun getReachableTilesWithDistance(cellMap: CellMap): Map<Position2D, Int> {
        val distances = mutableMapOf<Position2D, Int>()

        val queue = mutableListOf(cellMap.start to 0)

        do {
            val (position, distance) = queue.removeFirst()
            distances[position] = distance

            findLinkedPipes(cellMap, position)
                .filter { newPosition ->
                    !distances.contains(newPosition) || distances[newPosition]!! > distance+1
                }
                .forEach {
                    queue.add(it to distance+1)
                }
        } while(queue.isNotEmpty())

        return distances.toMap()
    }
}