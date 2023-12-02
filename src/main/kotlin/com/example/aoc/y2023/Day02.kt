package com.example.aoc.y2023

fun main() {
    val bag = Bag(mapOf(
        Color.RED to 12,
        Color.GREEN to 13,
        Color.BLUE to 14,
    ))

    inputLineSequence("day02.txt")
        .map { line -> parseGame(line) }
        .filter { game -> bag.isPossible(game) }
        .sumOf { game -> game.id }
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
    fun isPossible(game: Game): Boolean = game.reveals.all { reveal ->
        reveal.cubes.all { (color, num) -> num <= (this.cubes[color] ?: 0) }
    }
}

private data class Game(val id: Long, val reveals: List<RevealedSubsets>)
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
