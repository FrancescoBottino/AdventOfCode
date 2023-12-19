package advent_of_code_2023

import advent_of_code_2023.Day7.HandClassifier.NoJokersHandClassifier
import advent_of_code_2023.Day7.HandClassifier.WithJokersHandClassifier
import getFileContent
import printTimedResult

object Day7 {
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult(expectedValue = 253313241) {
                input()
                    .also { all ->
                        val allHands = all.map { it.first }
                        assert(allHands.toSet().size == allHands.size)
                    }
                    .asSequence()
                    .sortedWith { (hand1), (hand2) ->
                        noJokersHandComparator.compare(hand1, hand2)
                    }
                    .mapIndexed { index, (_, bid) ->
                        (index+1) * bid
                    }
                    .sum()
            }
        }
    }

    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            printTimedResult(expectedValue = 253362743) {
                input()
                    .also { all ->
                        val allHands = all.map { it.first }
                        assert(allHands.toSet().size == allHands.size)
                    }
                    .asSequence()
                    .sortedWith { (hand1), (hand2) ->
                        withJokersHandComparator.compare(hand1, hand2)
                    }
                    .mapIndexed { index, (_, bid) ->
                        (index+1) * bid
                    }
                    .sum()
            }
        }
    }

    private fun input(): List<Pair<Hand, Int>> {
        return getFileContent("2023/day7.txt")
            .split("\n")
            .map { line ->
                val (handString, bidValueString) = line.split(" ")

                Hand.fromString(handString) to bidValueString.toInt()
            }
    }

    private val allFaces = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
        .reversed()

    private val noJokersFaceValuesMap: Map<Char, Int> = allFaces
        .withIndex()
        .associate { it.value to it.index + 2 }

    private val withJokersFaceValuesMap = noJokersFaceValuesMap
        .plus('J' to 1)

    @JvmInline
    private value class Card(val face: Char) {
        override fun toString() = face.toString()
    }

    private class CardComparator(
        private val faceValues: Map<Char, Int>,
    ): Comparator<Card> {
        override fun compare(o1: Card, o2: Card): Int {
            return faceValues[o1.face]!!.compareTo(faceValues[o2.face]!!)
        }
    }

    private sealed interface HandClassifier {
        fun classify(hand: Hand): Hand.Type

        data object NoJokersHandClassifier: HandClassifier {
            override fun classify(hand: Hand): Hand.Type {
                val map = hand.cards.groupBy { it.face }

                return when {
                    map.keys.size == 5 ->
                        Hand.Type.HighCard

                    map.keys.size == 4 &&
                            map.values.filter { it.size == 2 }.size == 1 &&
                            map.values.filter { it.size == 1 }.size == 3 ->
                        Hand.Type.OnePair

                    map.keys.size == 3 &&
                            map.values.filter { it.size == 2 }.size == 2 &&
                            map.values.filter { it.size == 1 }.size == 1 ->
                        Hand.Type.TwoPair

                    map.keys.size == 3 &&
                            map.values.filter { it.size == 3 }.size == 1 &&
                            map.values.filter { it.size == 1 }.size == 2 ->
                        Hand.Type.ThreeOfAKind

                    map.keys.size == 2 &&
                            map.values.filter { it.size == 2 }.size == 1 &&
                            map.values.filter { it.size == 3 }.size == 1 ->
                        Hand.Type.FullHouse

                    map.keys.size == 2 &&
                            map.values.filter { it.size == 1 }.size == 1 &&
                            map.values.filter { it.size == 4 }.size == 1 ->
                        Hand.Type.FourOfAKind

                    map.keys.size == 1 ->
                        Hand.Type.FiveOfAKind

                    else -> throw RuntimeException("Classifier $this found no hand type applicable to $hand")
                }
            }
        }

        data object WithJokersHandClassifier: HandClassifier {
            override fun classify(hand: Hand): Hand.Type {
                if(hand.cards.none { it.face == 'J' }) {
                    return NoJokersHandClassifier.classify(hand)
                }

                return allFaces.minus('J')
                    .asSequence()
                    .map { face -> Card(face) }
                    .map { replacingCard ->
                        hand.cards
                            .map { handCard ->
                                if(handCard.face == 'J')
                                    replacingCard
                                else
                                    handCard
                            }
                            .let(::Hand)
                    }
                    .map { newHand ->
                        NoJokersHandClassifier.classify(newHand)
                    }
                    .maxBy { it.ordinal }
            }
        }
    }

    private data class Hand(val cards: List<Card>) {
        companion object {
            fun fromString(string: String): Hand {
                return Hand(string.map { Card(it) })
            }
        }

        init { assert(cards.size == 5) }

        enum class Type {
            HighCard,
            OnePair,
            TwoPair,
            ThreeOfAKind,
            FullHouse,
            FourOfAKind,
            FiveOfAKind;
        }

        var type: Type? = null

        override fun toString() = cards.joinToString("") + (type?.let { " ($type)" } ?: "")
    }

    private class HandComparator(
        private val handClassifier: HandClassifier,
        private val cardComparator: CardComparator,
    ): Comparator<Hand> {
        constructor(
            handClassifier: HandClassifier,
            faceValues: Map<Char, Int>,
        ): this(
            handClassifier,
            CardComparator(faceValues)
        )

        override fun compare(o1: Hand, o2: Hand): Int {
            val type1 = o1.type ?: handClassifier.classify(o1).also { o1.type = it }
            val type2 = o2.type ?: handClassifier.classify(o2).also { o2.type = it }

            return if(type1 == type2) {
                var i = 0
                while(o1.cards[i] == o2.cards[i]) { i++ }
                cardComparator.compare(o1.cards[i], o2.cards[i])
            } else {
                type1.ordinal - type2.ordinal
            }
        }
    }

    private val noJokersHandComparator = HandComparator(NoJokersHandClassifier, noJokersFaceValuesMap)
    private val withJokersHandComparator = HandComparator(WithJokersHandClassifier, withJokersFaceValuesMap)
}