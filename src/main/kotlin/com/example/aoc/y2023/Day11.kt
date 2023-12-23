package com.example.aoc.y2023

import java.util.*
import kotlin.math.abs

fun main() {
    part1()
    part2()
}

private fun part1() {
    inputLineSequence("day11.txt")
        .map { line -> line.map { char -> char.toUniverseItem() } }
        .toList()
        .let { Universe(it).expand() }
        .also { it.draw() }
        .let { universe -> universe.galaxyPositions().createPairs() }
        .sumOf { (galaxy1, galaxy2) -> galaxy1.distanceFrom(galaxy2) }
        .let { println(it) }
}

private fun part2() {
    inputLineSequence("day11.txt")
        .map { line -> line.map { char -> char.toUniverseItem() } }
        .toList()
        .let { Universe(it) }
        .let { universe ->
            val rowIndicesToExpand = universe.rowIndicesToExpand()
            val columnIndicesToExpand = universe.columnIndicesToExpand()
            val galaxyPositions = universe.galaxyPositions()
            expand(galaxyPositions, rowIndicesToExpand, columnIndicesToExpand, expansionFactor = 1_000_000)
        }
        .also { it.forEach { println(it) } }
        .let { positions -> positions.createPairs() }
        .sumOf { (galaxy1, galaxy2) -> galaxy1.distanceFrom(galaxy2) }
        .let { println(it) }
}

private fun expand(
    galaxyPositions: List<Position>,
    rowIndicesToExpand: List<Long>,
    columnIndicesToExpand: List<Long>,
    expansionFactor: Long
): List<Position> {
    return galaxyPositions
        .map { galaxyPosition ->
            val rowsAboveGalaxyToExpand = rowIndicesToExpand.count { it < galaxyPosition.row }
            val columnsAboveGalaxyToExpand = columnIndicesToExpand.count { it < galaxyPosition.column }
            Position(
                row = galaxyPosition.row + rowsAboveGalaxyToExpand * (expansionFactor - 1),
                column = galaxyPosition.column + columnsAboveGalaxyToExpand * (expansionFactor - 1),
            )
        }
}

private fun Char.toUniverseItem() = when (this) {
    '#' -> UniverseItem.GALAXY
    '.' -> UniverseItem.EMPTY_SPACE
    else -> error("Unexpected item: $this")
}

private fun Position.distanceFrom(other: Position): Long {
    val rowDistance = abs(this.row - other.row)
    val columnDistance = abs(this.column - other.column)
    return minOf(rowDistance, columnDistance) * 2 +
            (maxOf(rowDistance, columnDistance) - minOf(rowDistance, columnDistance))
}

private enum class UniverseItem {
    GALAXY, EMPTY_SPACE
}

private data class Universe(val items: List<List<UniverseItem>>) {
    fun expand(): Universe {
        return expandRows().expandColumns()
    }

    fun rowIndicesToExpand(): List<Long> {
        return items.withIndex()
            .filter { (_, row) -> row.all { it == UniverseItem.EMPTY_SPACE } }
            .map { it.index.toLong() }
    }

    fun columnIndicesToExpand(): List<Long> {
        return items.first().indices
            .filter { index -> items.all { row -> row[index] == UniverseItem.EMPTY_SPACE } }
            .reversed()
            .map { it.toLong() }
    }

    private fun expandRows(): Universe {
        val newItems = mutableListOf<List<UniverseItem>>()

        for (row in items) {
            if (row.all { it == UniverseItem.EMPTY_SPACE }) {
                repeat(2) {
                    newItems.add(row)
                }
            } else {
                newItems.add(row)
            }
        }

        return Universe(newItems)
    }

    private fun expandColumns(): Universe {
        val columnsToExpand = columnIndicesToExpand()

        val newItems = mutableListOf<List<UniverseItem>>()

        for (row in items) {
            val newRow = LinkedList(row)
            columnsToExpand.forEach { columnIndex -> newRow.add(columnIndex.toInt(), UniverseItem.EMPTY_SPACE) }
            newItems.add(newRow)
        }

        return Universe(newItems)
    }

    fun draw() {
        for (row in items) {
            for (item in row) {
                print(
                    when (item) {
                        UniverseItem.GALAXY -> '#'
                        UniverseItem.EMPTY_SPACE -> '.'
                    }
                )
            }
            println()
        }
    }

    fun galaxyPositions(): List<Position> {
        val list = mutableListOf<Position>()

        for ((rowIndex, row) in items.withIndex()) {
            for ((columnIndex, item) in row.withIndex()) {
                if (item == UniverseItem.GALAXY) {
                    list.add(Position(rowIndex.toLong(), columnIndex.toLong()))
                }
            }
        }
        return list
    }
}
