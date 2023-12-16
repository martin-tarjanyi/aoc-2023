package com.example.aoc.y2023

fun main() {
    part1() // 579439039
    part2() // 7873084
}

private fun part1() {
    inputLineSequence("day05.txt").toList()
        .let { parseAlmanac(it) { input -> mapSeedsForPart1(input) } }
        .findLowestSeedLocationRanges()
        .minOf { it.first }
        .let { println(it) }
}

private fun part2() {
    inputLineSequence("day05.txt").toList()
        .let { parseAlmanac(it) { input -> mapSeedsForPart2(input) } }
        .findLowestSeedLocationRanges()
        .minOf { it.first }
        .let { println(it) }
}

private fun parseAlmanac(input: List<String>, seedMapper: (List<String>) -> List<LongRange>): Almanac {
    val seeds = seedMapper(input)

    val seedToSoil = parseMap(input, "seed-to-soil")
    val soilToFertilizer = parseMap(input, "soil-to-fertilizer")
    val fertilizerToWater = parseMap(input, "fertilizer-to-water")
    val waterToLight = parseMap(input, "water-to-light")
    val lightToTemperature = parseMap(input, "light-to-temperature")
    val temperatureToHumidity = parseMap(input, "temperature-to-humidity")
    val humidityToLocation = parseMap(input, "humidity-to-location")

    return Almanac(
        seeds,
        seedToSoil = seedToSoil,
        soilToFertilizer = soilToFertilizer,
        fertilizerToWater = fertilizerToWater,
        waterToLight = waterToLight,
        lightToTemperature = lightToTemperature,
        temperatureToHumidity = temperatureToHumidity,
        humidityToLocation = humidityToLocation,
    )
}

private fun mapSeedsForPart1(input: List<String>): List<LongRange> =
    input.first().removePrefix("seeds:").trim().split(" ").map { it.toLong()..it.toLong() }

private fun mapSeedsForPart2(input: List<String>): List<LongRange> =
    input.first().removePrefix("seeds:").trim().split(" ").chunked(2)
        .map { (start, length) -> start.toLong()..<(start.toLong() + length.toLong()) }

private fun parseMap(input: List<String>, mapName: String) =
    input.dropWhile { !it.contains(mapName) }.drop(1).takeWhile { !it.contains("map") }.associate {
        val (destinationStart, sourceStart, length) = it.split(" ").map { numString -> numString.toLong() }
        (sourceStart..<sourceStart + length) to (destinationStart..<destinationStart + length)
    }

private infix fun LongRange.overlaps(sourceRange: LongRange): Boolean {
    return this.first in sourceRange || sourceRange.first in this
}

private data class Almanac(
    val seeds: List<LongRange>,
    val seedToSoil: Map<LongRange, LongRange>,
    val soilToFertilizer: Map<LongRange, LongRange>,
    val fertilizerToWater: Map<LongRange, LongRange>,
    val waterToLight: Map<LongRange, LongRange>,
    val lightToTemperature: Map<LongRange, LongRange>,
    val temperatureToHumidity: Map<LongRange, LongRange>,
    val humidityToLocation: Map<LongRange, LongRange>
) {
    fun findLowestSeedLocationRanges(): List<LongRange> = seeds
        .asSequence()
        .flatMap { source -> toDestination(source, seedToSoil) }
        .flatMap { source -> toDestination(source, soilToFertilizer) }
        .flatMap { source -> toDestination(source, fertilizerToWater) }
        .flatMap { source -> toDestination(source, waterToLight) }
        .flatMap { source -> toDestination(source, lightToTemperature) }
        .flatMap { source -> toDestination(source, temperatureToHumidity) }
        .flatMap { source -> toDestination(source, humidityToLocation) }
        .toList()

    private fun toDestination(sourceRange: LongRange, map: Map<LongRange, LongRange>): List<LongRange> {
        val overlapsToDestination = map
            .filterKeys { mapSourceRange -> mapSourceRange overlaps sourceRange }
            .map { (mapSourceRange, mapDestinationRange) ->
                val overlap = if (sourceRange.first in mapSourceRange) {
                    sourceRange.first..minOf(sourceRange.last, mapSourceRange.last)
                } else if (mapSourceRange.first in sourceRange) {
                    mapSourceRange.first..minOf(sourceRange.last, mapSourceRange.last)
                } else {
                    error("Should be overlapping")
                }

                overlap to mapDestinationRange.first + (overlap.first - mapSourceRange.first)..
                        mapDestinationRange.first + (overlap.last - mapSourceRange.first)
            }
            .toMap()

        val unmappedRanges = findUnmappedRanges(overlapsToDestination.keys, sourceRange)

        return overlapsToDestination.values + unmappedRanges
    }

    private fun findUnmappedRanges(
        mappedRanges: Set<LongRange>,
        sourceRange: LongRange
    ): List<LongRange> {
        val overlaps = mappedRanges.sortedBy { it.first }
        val unmappedRanges = mutableListOf<LongRange>()
        var currentSource = sourceRange.first
        for (overlap in overlaps) {
            if (currentSource !in overlap) {
                unmappedRanges += currentSource..<overlap.first
            }
            currentSource = overlap.last + 1
        }
        if (currentSource in sourceRange) {
            unmappedRanges += currentSource..sourceRange.last
        }
        return unmappedRanges
    }
}
