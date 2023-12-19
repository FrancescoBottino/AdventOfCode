package advent_of_code_2023

import getFileContent
import printTimedResult

object Day9 {
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult(expectedValue = 1647269739) {
                input().sumOf {
                    makePredictionOnNext(sequence = it)
                }
            }
        }
    }

    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult(expectedValue = 864) {
                input().sumOf {
                    makePredictionOnPrevious(sequence = it)
                }
            }
        }
    }

    private fun input(): List<List<Int>> {
        return getFileContent("2023/day9.txt")
            .split("\n")
            .map {
                it.split(" ")
                    .map { it.toInt() }
            }
    }

    private fun createLayers(sequence: List<Int>): List<List<Int>> {
        val layers = mutableListOf(sequence)

        var currentLayer = sequence

        while(currentLayer.any { it != 0 }) {
            currentLayer = currentLayer
                .dropLast(1)
                .mapIndexed { index, item ->
                    currentLayer[index + 1] - item
                }

            layers += currentLayer
        }

        return layers.toList()
    }

    private fun makePredictionOnNext(sequence: List<Int>): Int {
        fun reverseTraversal(layers: List<List<Int>>): Int {
            if(layers.size == 1) return 0

            return layers.first().last() + reverseTraversal(layers.drop(1))
        }

        return reverseTraversal(createLayers(sequence))
    }

    private fun makePredictionOnPrevious(sequence: List<Int>): Int {
        fun reverseTraversal(layers: List<List<Int>>): Int {
            if(layers.size == 1) return 0

            return layers.first().first() - reverseTraversal(layers.drop(1))
        }

        return reverseTraversal(createLayers(sequence))
    }
}