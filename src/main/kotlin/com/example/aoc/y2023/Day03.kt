package com.example.aoc.y2023

fun main() {
    val engineSchematic = inputLineSequence("day03.txt").toList()
    engineSchematic.flatMapIndexed { index, line ->
        generateSequence(seed = line.findEngineNumber()) { previous ->
            line.findEngineNumber(fromIndex = previous.endIndexExclusive)
        }.filter { isPartNumber(it, engineSchematic, index) }
    }.sumOf { it.number }
        .let { println(it) }
}

private fun String.findEngineNumber(fromIndex: Int = 0): EngineNumber? {
    if (fromIndex >= this.length) return null

    val digitsWithIndex = this.withIndex()
        .drop(fromIndex)
        .dropWhile { !it.value.isDigit() }
        .takeWhile { it.value.isDigit() }
    val number = digitsWithIndex.map { it.value }.joinToString(separator = "").toLongOrNull()

    return number?.let { EngineNumber(it, digitsWithIndex.first().index) }
}

private fun isPartNumber(engineNumber: EngineNumber, schematic: List<String>, lineIndex: Int): Boolean {
    val minLineIndex = (lineIndex - 1).coerceAtLeast(0)
    val maxLineIndex = (lineIndex + 1).coerceAtMost(schematic.size - 1)

    val minColumnIndex = (engineNumber.index - 1).coerceAtLeast(0)
    val maxColumnIndex = (engineNumber.endIndexExclusive).coerceAtMost(schematic[lineIndex].length - 1)

    for (i in minLineIndex..maxLineIndex) {
        for (j in minColumnIndex..maxColumnIndex) {
            if (schematic.getOrNull(i)?.getOrNull(j)?.isSymbol == true) {
                return true
            }
        }
    }

    return false
}

private data class EngineNumber(val number: Long, val index: Int) {
    val endIndexExclusive: Int
        get() = index + number.toString().length
}

private val Char.isSymbol: Boolean
    get() = when {
        this.isDigit() -> false
        this == '.' -> false
        else -> true
    }
