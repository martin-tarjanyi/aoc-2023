package com.example.aoc.y2023

fun main() {
    part1()
}

private fun part1() {
    val lines = inputLineSequence("day08.txt").toList()
    val instructions = lines.first().toList()
    val nodes = lines.drop(1)
        .associate { line ->
            val id = line.substringBefore(" =")
            val (left, right) = line.substringAfter("= ").removeSurrounding("(", ")").split(", ")
            id to Node(id, left, right)
        }

    var currentNode: Node = nodes["AAA"]!!

    instructions.asSequence()
        .repeat()
        .onEach { instruction ->
            currentNode = if (instruction == 'L') {
                nodes[currentNode.leftChild]
            } else {
                nodes[currentNode.rightChild]
            }!!
        }
        .takeWhile { currentNode.id != "ZZZ" }
        .count()
        .let { println(it + 1) }
}

private data class Node(val id: String, val leftChild: String, val rightChild: String)
