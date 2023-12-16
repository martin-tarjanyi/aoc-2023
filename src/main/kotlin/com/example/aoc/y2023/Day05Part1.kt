package com.example.aoc.y2023

fun main() {
    inputLineSequence("day05.txt").toList()
        .let { parseAlmanac(it) }
        .findSeedLocations()
        .min()
        .let { println(it) }
}

private fun parseAlmanac(input: List<String>): Almanac {
    val seeds = input.first().removePrefix("seeds:").trim().split(" ").map { it.toLong() }

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

private fun parseMap(input: List<String>, mapName: String) = input.dropWhile { !it.contains(mapName) }
    .drop(1)
    .takeWhile { !it.contains("map") }
    .associate {
        val (destinationStart, sourceStart, length) = it.split(" ").map { numString -> numString.toLong() }
        (sourceStart..<sourceStart + length) to (destinationStart..<destinationStart + length)
    }

private data class Almanac(
    val seeds: List<Long>,
    val seedToSoil: Map<LongRange, LongRange>,
    val soilToFertilizer: Map<LongRange, LongRange>,
    val fertilizerToWater: Map<LongRange, LongRange>,
    val waterToLight: Map<LongRange, LongRange>,
    val lightToTemperature: Map<LongRange, LongRange>,
    val temperatureToHumidity: Map<LongRange, LongRange>,
    val humidityToLocation: Map<LongRange, LongRange>
) {
    fun findSeedLocations(): List<Long> = seeds.map { seed ->
        toDestination(seed, seedToSoil)
            .let { source -> toDestination(source, soilToFertilizer) }
            .let { source -> toDestination(source, fertilizerToWater) }
            .let { source -> toDestination(source, waterToLight) }
            .let { source -> toDestination(source, lightToTemperature) }
            .let { source -> toDestination(source, temperatureToHumidity) }
            .let { source -> toDestination(source, humidityToLocation) }
    }

    private fun toDestination(source: Long, map: Map<LongRange, LongRange>) = (map.entries
        .firstOrNull { (sourceRange, _) -> source in sourceRange }
        ?.let { (sourceRange, destinationRange) -> destinationRange.first + (source - sourceRange.first) }
        ?: source)
}
