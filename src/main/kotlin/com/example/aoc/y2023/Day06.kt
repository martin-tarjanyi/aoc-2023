package com.example.aoc.y2023

fun main() {
    // part 1
    solve { split("\\s+".toRegex()) }
    // part 2
    solve { listOf(replace("\\s+".toRegex(), "")) }
}

private fun solve(splitNumbers: String.() -> List<String>) {
    inputLineSequence("day06.txt")
        .take(2)
        .toList()
        .let { lines ->
            val times = lines.first().substringAfter(":").trim().splitNumbers().map { it.toLong() }
            val distances = lines[1].substringAfter(":").trim().splitNumbers().map { it.toLong() }
            require(times.size == distances.size) { "$times and $distances must have same size" }
            (0..<times.count())
                .map { i ->
                    val time = times[i]
                    val recordDistance = distances[i]
                    val bestBoostDuration = time / 2

                    generateSequence(bestBoostDuration) { boostDuration ->
                        if (boostDuration < 0) {
                            return@generateSequence null
                        }
                        boostDuration - 1
                    }
                        .takeWhile { boostDuration ->
                            val speed = boostDuration
                            val distance = speed * (time - boostDuration)
                            distance > recordDistance
                        }
                        .flatMap { boostDuration ->
                            setOf(boostDuration, time - boostDuration)
                        }
                        .count()
                }
                .reduce { a, b -> a * b }
        }
        .let { println(it) }
}
