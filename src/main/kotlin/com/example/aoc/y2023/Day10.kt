package com.example.aoc.y2023

fun main() {
    part1()
    part2()
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

private fun part2() {
    val maze = inputLineSequence("day10.txt")
        .map { line -> line.map { symbol -> Pipe.from(symbol) } }
        .toList()
        .let { Maze(it) }

    val journey = generateSequence(maze.findStartingPosition()) { journeyPosition ->
        maze.findNextConnection(journeyPosition).takeIf { it.pipe != Pipe.STARTING }
    }.toList()

    val count = maze.replaceStartingPositionWithPipe().countEnclosedFieldsBy(journey)
    println(count)
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

        fun byConnections(directions: Set<Direction>): Pipe =
            (entries - STARTING).single { it.connections.containsAll(directions) }
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

    fun countEnclosedFieldsBy(journey: List<JourneyPosition>): Long {
        val journeyPositions = journey.map { it.position }

        var enclosedGrounds = 0L
        for ((rowIndex, row) in field.withIndex()) {
            var inside = false
            var openLBend = false
            var openFBend = false
            for ((columnIndex, pipe) in row.withIndex()) {
                val position = Position(rowIndex, columnIndex)
                if (pipe == Pipe.GROUND && inside) {
                    enclosedGrounds++
                    print("I")
                } else if (!journeyPositions.contains(position)) {
                    if (inside) {
                        enclosedGrounds++
                        print("I")
                    } else {
                        print("/")
                    }
                } else if (pipe == Pipe.HORIZONTAL) {
                    // nothing to do
                    print("#")
                } else if (pipe == Pipe.VERTICAL) {
                    inside = !inside
                    print("#")
                } else if (pipe == Pipe.L_BEND) {
                    openLBend = true
                    print("#")
                } else if (pipe == Pipe.J_BEND && openLBend) {
                    openLBend = false
                    print("#")
                } else if (pipe == Pipe.SEVEN_BEND && openLBend) {
                    inside = !inside
                    openLBend = false
                    print("#")
                } else if (pipe == Pipe.F_BEND) {
                    openFBend = true
                    print("#")
                } else if (pipe == Pipe.SEVEN_BEND && openFBend) {
                    openFBend = false
                    print("#")
                } else if (pipe == Pipe.J_BEND && openFBend) {
                    inside = !inside
                    openFBend = false
                    print("#")
                } else {
                    print(pipe.symbol)
                }
            }
            println()
        }

        return enclosedGrounds
    }

    fun replaceStartingPositionWithPipe(): Maze {
        val rows = mutableListOf<List<Pipe>>()
        for ((rowIndex, row) in field.withIndex()) {
            val pipes = mutableListOf<Pipe>()
            for ((columnIndex, pipe) in row.withIndex()) {
                if (pipe == Pipe.STARTING) {
                    val position = Position(rowIndex, columnIndex)
                    val connections = Direction.entries
                        .filter { direction -> atPosition(position.move(direction))?.hasConnection(direction.opposite) == true }
                        .toSet()
                    val replacementPipe = Pipe.byConnections(connections)
                    pipes.add(replacementPipe)
                } else {
                    pipes.add(pipe)
                }
            }
            rows.add(pipes)
        }
        return Maze(rows)
    }
}

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
