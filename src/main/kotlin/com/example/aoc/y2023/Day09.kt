package com.example.aoc.y2023

fun main() {
    part1()
}

private fun part1() {
    inputLineSequence("day09.txt")
        .sumOf { line ->
            val nums = line.split(" ").map { it.toLong() }
            val sequenceHistory = mutableListOf(nums)
            var currentSequence = nums
            while (currentSequence.any { it != 0L }) {
                currentSequence = currentSequence.windowed(2)
                    .map { (a, b) -> b - a }
                sequenceHistory.add(currentSequence)
            }

            sequenceHistory
                .reversed()
                .drop(1)
                .reduce { acc, longs ->
                    val increment = acc.last()
                    longs + (longs.last() + increment)
                }
                .last()
        }
        .let { println(it) }
}

