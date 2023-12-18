package com.example.aoc.y2023

fun main() {
    part1()
}

private fun part1() {
    val maze = inputLineSequence("day10.txt")
        .map { line -> line.map { symbol -> Pipe.from(symbol) } }
        .toList()
        .let { Maze(it) }

    generateSequence(maze.findStartingPosition()) { journeyPosition ->
        maze.findNextConnection(journeyPosition).takeIf { it.pipe != Pipe.STARTING }
    }.count()
        .let { it / 2 }
        .also { println(it) }
}

private enum class Direction {
    NORTH, SOUTH, WEST, EAST
}

private enum class Pipe(val symbol: Char, val connections: Set<Direction>) {
    VERTICAL(symbol = '|', connections = setOf(Direction.NORTH, Direction.SOUTH)),
    HORIZONTAL(symbol = '-', connections = setOf(Direction.WEST, Direction.EAST)),
    L_BEND(symbol = 'L', connections = setOf(Direction.NORTH, Direction.EAST)),
    J_BEND(symbol = 'J', connections = setOf(Direction.NORTH, Direction.WEST)),
    SEVEN_BEND(symbol = '7', connections = setOf(Direction.SOUTH, Direction.WEST)),
    F_BEND(symbol = 'F', connections = setOf(Direction.SOUTH, Direction.EAST)),
    GROUND(symbol = '.', connections = setOf()),
    STARTING(symbol = 'S', connections = Direction.entries.toSet());

    fun hasConnection(direction: Direction): Boolean = connections.contains(direction)

    companion object {
        fun from(symbol: Char): Pipe {
            return entries.first { it.symbol == symbol }
        }
    }
}

private data class Maze(private val field: List<List<Pipe>>) {
    fun findStartingPosition(): JourneyPosition {
        for ((rowIndex, row) in field.withIndex()) {
            for ((columnIndex, pipe) in row.withIndex()) {
                if (pipe == Pipe.STARTING) {
                    return JourneyPosition(Position(rowIndex, columnIndex), pipe, cameFrom = null)
                }
            }
        }

        error("Where is the starting position?")
    }

    fun findNextConnection(journeyPosition: JourneyPosition): JourneyPosition {
        for (direction in (journeyPosition.pipe.connections - setOfNotNull(journeyPosition.cameFrom))) {
            val newPosition = journeyPosition.position.move(direction)
            val newPipe = this.atPosition(newPosition)
            if (newPipe?.hasConnection(direction.opposite) == true || newPipe == Pipe.STARTING) {
                return JourneyPosition(newPosition, newPipe, direction.opposite)
            }
        }

        error("Ooh, no connection found.")
    }

    fun atPosition(position: Position): Pipe? =
        field.getOrNull(position.row)?.getOrNull(position.column)
}

private data class Position(val row: Int, val column: Int)

private data class JourneyPosition(val position: Position, val pipe: Pipe, val cameFrom: Direction?)

private fun Position.move(direction: Direction): Position = when (direction) {
    Direction.NORTH -> this.copy(row = row - 1)
    Direction.SOUTH -> this.copy(row = row + 1)
    Direction.WEST -> this.copy(column = column - 1)
    Direction.EAST -> this.copy(column = column + 1)
}

private val Direction.opposite: Direction
    get() = when (this) {
        Direction.NORTH -> Direction.SOUTH
        Direction.SOUTH -> Direction.NORTH
        Direction.WEST -> Direction.EAST
        Direction.EAST -> Direction.WEST
    }
