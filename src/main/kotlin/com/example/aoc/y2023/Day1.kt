package com.example.aoc.y2023

fun main() {
    part1()
    part2()
}

private fun part1() {
    inputLineSequence("day1.txt")
        .map { line ->
            val digits = line.filter { it.isDigit() }
            "${digits.first()}${digits.last()}".toLong()
        }
        .sum()
        .let { println(it) }
}

private fun part2() {
    val stringToNum = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
    )

    inputLineSequence("day1.txt")
        .map { line ->
            val numbersAsStringInLineWithIndex = stringToNum.keys.flatMap {
                listOf(
                    stringToNum[it] to line.indexOf(it),
                    stringToNum[it] to line.lastIndexOf(it)
                )
            }
            val numbersAsDigitsInLineWithIndex = stringToNum.values.flatMap {
                listOf(
                    it to line.indexOf(it.toString()),
                    it to line.lastIndexOf(it.toString())
                )
            }
            val numbersInOrder = (numbersAsStringInLineWithIndex + numbersAsDigitsInLineWithIndex)
                .filter { it.second != -1 }
                .sortedBy { it.second }
                .map { it.first }

            "${numbersInOrder.first()}${numbersInOrder.last()}".toLong()
        }
        .sum()
        .let { println(it) }
}
