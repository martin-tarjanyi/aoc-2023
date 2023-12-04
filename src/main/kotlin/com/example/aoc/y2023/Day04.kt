package com.example.aoc.y2023

import kotlin.math.pow
import kotlin.math.roundToLong

fun main() {
    inputLineSequence("day04.txt")
        .map { parseCard(it) }
        .map { card -> card.playerWinningNumbers() }
        .map { score(it) }
        .sum()
        .let { println(it) }
}

private fun score(numbers: List<Long>): Long {
    if (numbers.isEmpty()) {
        return 0
    }

    return 2.toDouble().pow((numbers.size - 1).toDouble()).roundToLong()
}

private fun parseCard(line: String): Card {
    val (winning, player) = line.substringAfter(": ").split(" | ")
    val winningNumbers = winning.split(" ").filter { it.isNotBlank() }.map { it.toLong() }
    val playerNumbers = player.split(" ").filter { it.isNotBlank() }.map { it.toLong() }

    return Card(winningNumbers, playerNumbers)
}

private data class Card(val winningNumbers: List<Long>, val playerNumbers: List<Long>) {
    fun playerWinningNumbers(): List<Long> {
        val remainingWinningNumbers = winningNumbers.toMutableList()
        return playerNumbers.filter { remainingWinningNumbers.remove(it) }
    }
}
