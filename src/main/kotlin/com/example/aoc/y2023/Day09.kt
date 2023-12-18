package com.example.aoc.y2023

fun main() {
    part1()
    part2()
}

private fun part1() {
    inputLineSequence("day09.txt")
        .sumOf { line ->
            val nums = line.split(" ").map { it.toLong() }
            val sequenceHistory = sequenceHistory(nums)

            sequenceHistory
                .reversed()
                .drop(1)
                .reduce { previousSequence, nextSequence ->
                    val increment = previousSequence.last()
                    nextSequence + (nextSequence.last() + increment)
                }
                .last()
        }
        .let { println(it) }
}

private fun part2() {
    inputLineSequence("day09.txt")
        .sumOf { line ->
            val nums = line.split(" ").map { it.toLong() }
            val sequenceHistory = sequenceHistory(nums)

            sequenceHistory
                .reversed()
                .drop(1)
                .reduce { previousSequence, nextSequence ->
                    val increment = previousSequence.first()
                    buildList {
                        add(nextSequence.first() - increment)
                        addAll(nextSequence)
                    }
                }
                .first()
        }
        .let { println(it) }
}

private fun sequenceHistory(originalSequence: List<Long>): MutableList<List<Long>> {
    val sequenceHistory = mutableListOf(originalSequence)
    var currentSequence = originalSequence
    while (currentSequence.any { it != 0L }) {
        currentSequence = currentSequence.windowed(2)
            .map { (a, b) -> b - a }
        sequenceHistory.add(currentSequence)
    }
    return sequenceHistory
}
