package advent_of_code_2023

import getFileContent

object Day5 {
    //600279879
    object Part1 {
        @JvmStatic
        fun main(args: Array<String>) {
            val almanac = input()

            val minLocation = almanac
                .seeds
                .minOf { seed ->
                    applyRangeMapSequence(seed, almanac.rangeTranslationSequence)
                }

            println(minLocation)
        }
    }

    object Part2 {
        @JvmStatic
        fun main(args: Array<String>) {
            val almanac = input()

            /* TODO

            var minLocation: Long = Long.MAX_VALUE

            almanac
                .seeds
                .also { assert(it.size % 2 == 0) }
                .withIndex()
                .groupBy(
                    keySelector = { it.index / 2 },
                    valueTransform = { it.value }
                )
                .values
                .map { (start, size) ->
                    start ..< (start + size)
                }
                .forEach { seedsRange ->
                    seedsRange.forEach { seed ->
                        val location = applyRangeMapSequence(seed, almanac.rangeMapSequence)

                        minLocation = minOf(minLocation, minLocation)
                    }
                }
            println(minLocation)
             */

        }
    }

    private data class RangeTranslation(
        val sourceRangeStart: Long,
        val destRangeStart: Long,
        val rangeSize: Long,
    ) {
        val sourceRange
            get() = sourceRangeStart ..< (sourceRangeStart + rangeSize)

        val destRange
            get() = destRangeStart ..< (destRangeStart + rangeSize)

        val offset
            get() = destRangeStart - sourceRangeStart

        fun mapValue(value: Long): Long? = if(value in sourceRange) value + offset else null
    }

    /* todo
    private data class RangeMap(
        val map: Map<LongRange, LongRange>
    ) {
        companion object {
            fun List<RangeTranslation>.compile(): RangeMap {
                return this
                    .associate {
                        it.sourceRange to it.destRange
                    }
                    .let(::RangeMap)
            }
        }

        operator fun plus(other: RangeMap): RangeMap {

        }
    }

     */

    private fun List<RangeTranslation>.mapValue(value: Long): Long? {
        return this
            .mapNotNull { it.mapValue(value) }
            .also { assert(it.size in 0..1) }
            .singleOrNull()
    }

    private fun applyRangeMapSequence(value: Long, sequence: List<List<RangeTranslation>>): Long {
        return sequence.fold(value) { acc, rangeList ->
            rangeList.mapValue(acc) ?: acc
        }
    }

    private data class Almanac(
        val seeds: List<Long>,
        val rangeTranslationSequence: List<List<RangeTranslation>>,
    )

    private fun parseAlmanac(lines: List<String>): Almanac {
        fun parseRangeMap(line: String): RangeTranslation {
            val (destRangeStart, sourceRangeStart, rangeSize) = line
                .split(" ")
                .map { it.trim().toLong() }
                .also { assert(it.size == 3) }

            return RangeTranslation(
                destRangeStart = destRangeStart,
                sourceRangeStart = sourceRangeStart,
                rangeSize = rangeSize,
            )
        }

        fun parseRangeMapList(lines: List<String>, rangeName: String): List<RangeTranslation> {
            return lines
                .indexOfFirst { it == rangeName }
                .plus(1)
                .let { startIndex ->
                    val linesCutoff = lines
                        .drop(startIndex)

                    val endIndex = linesCutoff
                        .indexOfFirst { it == "" }
                        .takeIf { it != -1 }
                        ?: (linesCutoff.lastIndex + 1)

                    (startIndex..<(startIndex + endIndex))
                        .map { parseRangeMap(lines[it]) }
                        .sortedBy { it.sourceRangeStart }
                }
        }

        fun parseSeedsList(lines: List<String>): List<Long> {
            return lines.first().removePrefix("seeds: ")
                .split(" ")
                .map { it.trim().toLong() }
        }

        return Almanac(
            seeds = parseSeedsList(lines),
            rangeTranslationSequence = listOf(
                parseRangeMapList(lines, "seed-to-soil map:"),
                parseRangeMapList(lines, "soil-to-fertilizer map:"),
                parseRangeMapList(lines, "fertilizer-to-water map:"),
                parseRangeMapList(lines, "water-to-light map:"),
                parseRangeMapList(lines, "light-to-temperature map:"),
                parseRangeMapList(lines, "temperature-to-humidity map:"),
                parseRangeMapList(lines, "humidity-to-location map:"),
            )
        )
    }

    private fun input(): Almanac {
        val lines = getFileContent("2023/day5.txt")
            .split("\n")

        return parseAlmanac(lines)
    }
}