package advent_of_code_2023

import getFileContent
import printTimedResult

object Day8 {
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult(expectedValue = 19667) {
                val (instructions, network) = input()

                getStepsNum(instructions, network, network.nodes["AAA"]!!) { it.name == "ZZZ" }
            }
        }
    }

    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult(expectedValue = 19185263738117L) {
                val (instructions, network) = input()

                network.nodes
                    .filter { it.key.endsWith("A") }
                    .values
                    .map {
                        getStepsNum(instructions, network, it) { it.name.endsWith("Z") }
                            .toLong()
                    }
                    .let(::lcm)
            }
        }
    }

    private fun input(): Input {
        return getFileContent("2023/day8.txt")
            .split("\n\n")
            .let { (instructions, nodes) ->
                Input(
                    instructions = instructions.toList(),
                    network = Network(
                        nodes = nodes
                            .split("\n")
                            .associate { line ->
                                val (name, directions) = line
                                    .split(" = ")

                                val (left, right) = directions
                                    .removePrefix("(")
                                    .removeSuffix(")")
                                    .split(", ")

                                name to Node(
                                    name = name,
                                    left = left,
                                    right = right,
                                )
                            }
                    )
                )
            }
    }

    private data class Node(
        val name: String,
        val left: String,
        val right: String,
    )

    private data class Network(
        val nodes: Map<String, Node>,
    )

    private enum class Direction {
        Left, Right;
    }

    private data class Input(
        val instructions: List<Char>,
        val network: Network,
    )

    private fun getStepsNum(instructions: List<Char>, network: Network, start: Node, isEnd: (Node) -> Boolean): Int {
        var counter = 0
        var instructionsIndex = 0
        var currentNode = start

        while(isEnd(currentNode).not()) {
            val currentInstruction = instructions[instructionsIndex]

            when(currentInstruction) {
                'L' -> currentNode = network.nodes[currentNode.left]!!
                'R' -> currentNode = network.nodes[currentNode.right]!!
            }

            instructionsIndex = (instructionsIndex + 1) % instructions.size
            counter++
        }

        return counter
    }

    fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
    fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b
    fun lcm(args: List<Long>): Long = args.reduce { acc, it -> lcm(acc, it) }
}