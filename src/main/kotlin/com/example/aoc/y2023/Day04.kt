package com.example.aoc.y2023

import kotlin.math.pow
import kotlin.math.roundToLong

fun main() {
    part1()
    part2()
}

private fun part1() {
    inputLineSequence("day04.txt")
        .map { parseCard(it) }
        .map { card -> card.playerWinningNumbers() }
        .map { score(it) }
        .sum()
        .let { println(it) }
}

private fun part2() {
    val cards = inputLineSequence("day04.txt").map { parseCard(it) }
    val copiesWonById = cards.associate { it.id to it.playerWinningNumbers().size }
    val numberOfCardsById = cards.associate { it.id to 1 }.toMutableMap()

    for ((id, numberOfCards) in numberOfCardsById) {
        val copiesWon = copiesWonById[id]!!
        repeat(copiesWon) {
            val cardIdWon = id + it + 1
            numberOfCardsById.merge(cardIdWon, numberOfCards, Int::plus)
        }
    }

    println(numberOfCardsById.values.sum())
}

private fun score(numbers: List<Long>): Long {
    if (numbers.isEmpty()) {
        return 0
    }

    return 2.toDouble().pow((numbers.size - 1).toDouble()).roundToLong()
}

private fun parseCard(line: String): Card {
    val cardId = """Card\s+(\d+):""".toRegex().find(line)?.groups?.get(1)?.value?.toInt()!!
    val (winning, player) = line.substringAfter(": ").split(" | ")
    val winningNumbers = winning.split(" ").filter { it.isNotBlank() }.map { it.toLong() }
    val playerNumbers = player.split(" ").filter { it.isNotBlank() }.map { it.toLong() }

    return Card(cardId, winningNumbers, playerNumbers)
}

private data class Card(val id: Int, val winningNumbers: List<Long>, val playerNumbers: List<Long>) {
    fun playerWinningNumbers(): List<Long> {
        val remainingWinningNumbers = winningNumbers.toMutableList()
        return playerNumbers.filter { remainingWinningNumbers.remove(it) }
    }
}
