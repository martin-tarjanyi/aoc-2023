package com.example.aoc.y2023

fun main() {
    part1()
    part2()
}

private fun part1() {
    val engineSchematic = inputLineSequence("day03.txt").toList()
    findPartNumbers(engineSchematic)
        .sumOf { it.number }
        .let { println(it) }
}

private fun part2() {
    val engineSchematic = inputLineSequence("day03.txt").toList()
    val partNumbers = findPartNumbers(engineSchematic)
    val gearSymbols = engineSchematic.flatMapIndexed { lineIndex: Int, line: String ->
        line.mapIndexedNotNull { columnIndex, c -> if (c == '*') EngineGearSymbol(lineIndex, columnIndex) else null }
    }

    gearSymbols.sumOf { gearSymbol ->
        val adjacentPartNumbers = findAdjacentPartNumbers(gearSymbol, partNumbers)
        return@sumOf if (adjacentPartNumbers.size == 2) {
            adjacentPartNumbers[0].number * adjacentPartNumbers[1].number
        } else {
            0L
        }
    }.let { println(it) }
}

private fun findPartNumbers(engineSchematic: List<String>) =
    engineSchematic.flatMapIndexed { index, line ->
        generateSequence(seed = line.findEngineNumber(lineIndex = index)) { previous ->
            line.findEngineNumber(fromIndex = previous.endColumnIndexExclusive, lineIndex = index)
        }
    }.filter { isPartNumber(it, engineSchematic) }

private fun String.findEngineNumber(fromIndex: Int = 0, lineIndex: Int): EngineNumber? {
    if (fromIndex >= this.length) return null

    val digitsWithIndex =
        this.withIndex().drop(fromIndex).dropWhile { !it.value.isDigit() }.takeWhile { it.value.isDigit() }
    val number = digitsWithIndex.map { it.value }.joinToString(separator = "").toLongOrNull()

    return number?.let { EngineNumber(it, lineIndex, digitsWithIndex.first().index) }
}

private fun isPartNumber(engineNumber: EngineNumber, schematic: List<String>): Boolean {
    val minLineIndex = engineNumber.lineIndex - 1
    val maxLineIndex = engineNumber.lineIndex + 1

    val minColumnIndex = engineNumber.columnIndex - 1
    val maxColumnIndex = engineNumber.endColumnIndexExclusive

    for (i in minLineIndex..maxLineIndex) {
        for (j in minColumnIndex..maxColumnIndex) {
            if (schematic.getOrNull(i)?.getOrNull(j)?.isSymbol == true) {
                return true
            }
        }
    }

    return false
}

private fun findAdjacentPartNumbers(gearSymbol: EngineGearSymbol, partNumbers: List<EngineNumber>): List<EngineNumber> {
    val gearScopeMinLineIndex = gearSymbol.lineIndex - 1
    val gearScopeMaxLineIndex = gearSymbol.lineIndex + 1

    val gearScopeMinColumnIndex = gearSymbol.columnIndex - 1
    val gearScopeMaxColumnIndex = gearSymbol.columnIndex + 1

    return partNumbers.filter {
        it.hasIntersection(
            minLineIndex = gearScopeMinLineIndex,
            maxLineIndex = gearScopeMaxLineIndex,
            minColumnIndex = gearScopeMinColumnIndex,
            maxColumnIndex = gearScopeMaxColumnIndex
        )
    }
}

private data class EngineNumber(val number: Long, val lineIndex: Int, val columnIndex: Int) {
    fun hasIntersection(minLineIndex: Int, maxLineIndex: Int, minColumnIndex: Int, maxColumnIndex: Int): Boolean {
        return lineIndex in (minLineIndex..maxLineIndex)
                && (columnIndex..<endColumnIndexExclusive).intersect(minColumnIndex..maxColumnIndex).isNotEmpty()
    }

    val endColumnIndexExclusive: Int
        get() = columnIndex + number.toString().length
}

private data class EngineGearSymbol(val lineIndex: Int, val columnIndex: Int)

private val Char.isSymbol: Boolean
    get() = when {
        this.isDigit() -> false
        this == '.' -> false
        else -> true
    }
