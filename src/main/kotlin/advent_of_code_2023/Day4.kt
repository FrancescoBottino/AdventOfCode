package advent_of_code_2023

import getFileContent
import kotlin.math.pow

object Day4 {
    //21105
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            val lines = getFileContent("2023/day4.txt").split("\n")

            val cards = lines.map(Day4::parseCard)

            val result = cards.sumOf { getCardScore(it) }

            println(result)
        }
    }

    //5329815
    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            val lines = getFileContent("2023/day4.txt").split("\n")

            val cards = lines.map(Day4::parseCard)
                .associateBy { it.id }

            val winningsMemo = mutableMapOf<Card, Int>()
            val scratchesMemo = mutableMapOf<Card, Int>()

            fun getTotalScratchedCards(card: Card): Int {
                scratchesMemo[card]?.let { return it }

                val winningNumbersCount = winningsMemo[card]
                    ?: getCardWinningNumbersCount(card)
                        .also { winningsMemo[card] = it }

                val nextCards = (1..winningNumbersCount)
                    .map { offset ->
                        val newCardId = card.id + offset
                        cards[newCardId] ?: throw RuntimeException("card $newCardId does not exist")
                    }

                return nextCards.sumOf { getTotalScratchedCards(it) }
                    .plus(1)
                    .also { scratchesMemo[card] = it }
            }

            val total = cards.values.sumOf { getTotalScratchedCards(it) }

            println(total)
        }
    }

    private data class Card(val id: Int, val winningNumbers: List<Int>, val scratchedNumbers: List<Int>)

    private fun getCardWinningNumbersCount(card: Card): Int {
        return card.scratchedNumbers.count { scratchedNumber ->
            scratchedNumber in card.winningNumbers
        }
    }

    private fun getCardScore(card: Card): Int {
        val winningNumbersCount = getCardWinningNumbersCount(card)

        if(winningNumbersCount == 0) return 0
        if(winningNumbersCount == 1) return 1

       return 2.0.pow(winningNumbersCount - 1).toInt()
    }

    private fun parseCard(cardString: String): Card {
        val (first, second) = cardString.split(":", limit = 2)
        val id = first.drop("Card ".length).trim().toInt()

        val (winningNumbersString, scratchedNumbersString) = second.split("|", limit = 2)

        fun parseNumbersList(string: String): List<Int> {
            return string.split(" ").filter { it.isNotEmpty() }.map { it.trim().toInt() }
        }

        val winningNumbers = parseNumbersList(winningNumbersString)
        val scratchedNumbers = parseNumbersList(scratchedNumbersString)

        return Card(id, winningNumbers, scratchedNumbers)
    }
}