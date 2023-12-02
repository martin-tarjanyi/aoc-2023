package com.example.aoc.y2023

fun main() {
    part1()
    part2()
}

private fun part1() {
    val bag = Bag(
        mapOf(
            Color.RED to 12,
            Color.GREEN to 13,
            Color.BLUE to 14,
        )
    )

    inputLineSequence("day02.txt")
        .map { line -> parseGame(line) }
        .filter { game -> game.isPossibleWith(bag) }
        .sumOf { game -> game.id }
        .let { println(it) }
}

private fun part2() {
    inputLineSequence("day02.txt")
        .map { line -> parseGame(line) }
        .map { game -> game.smallestPossibleBag() }
        .sumOf { bag -> bag.power() }
        .let { println(it) }
}

private fun parseGame(line: String): Game {
    val id = line.substringBefore(':')
        .removePrefix("Game ")
        .toLong()

    return line.substringAfter(": ")
        .split("; ")
        .map { reveal ->
            reveal.split(", ").associate {
                val (numString, colorString) = it.split(" ")
                colorString.toColor() to numString.toLong()
            }.let { RevealedSubsets(it) }
        }.let { Game(id, it) }
}

private data class Bag(val cubes: Map<Color, Long>) {
    fun power(): Long = cubes.values.reduce { a, b -> a * b }
}

private data class Game(val id: Long, val reveals: List<RevealedSubsets>) {
    fun smallestPossibleBag(): Bag = Color.entries
        .associateWith { color -> reveals.maxOf { reveal -> reveal.cubes[color] ?: 0 } }
        .let { Bag(it) }

    fun isPossibleWith(bag: Bag): Boolean = this.reveals.all { reveal ->
        reveal.cubes.all { (color, num) -> num <= (bag.cubes[color] ?: 0) }
    }
}

private data class RevealedSubsets(val cubes: Map<Color, Long>)

private enum class Color {
    RED, GREEN, BLUE;
}

private fun String.toColor(): Color = when (this) {
    "red" -> Color.RED
    "green" -> Color.GREEN
    "blue" -> Color.BLUE
    else -> throw IllegalArgumentException("Unexpected color: $this")
}
