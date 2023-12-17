package com.example.aoc.y2023

import java.math.BigInteger

fun main() {
    part1()
    part2()
}

private fun part1() {
    val lines = inputLineSequence("day08.txt").toList()
    val instructions = lines.first().toList()
    val nodes = parseNodes(lines)

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

private fun part2() {
    val lines = inputLineSequence("day08.txt").toList()
    val instructions = lines.first().toList()
    val nodes = parseNodes(lines)
    val startingNodes = nodes.values.filter { it.id.endsWith("A") }.toMutableList()
    val stepsToEnd: MutableList<BigInteger> = MutableList(startingNodes.size) { BigInteger.ZERO }

    startingNodes.forEachIndexed { index, node ->
        var currentNode = node
        val steps = instructions.asSequence()
            .repeat()
            .onEach { instruction ->
                currentNode = if (instruction == 'L') {
                    nodes[currentNode.leftChild]
                } else {
                    nodes[currentNode.rightChild]
                }!!
            }
            .takeWhile { !currentNode.id.endsWith("Z") }
            .count() + 1
        stepsToEnd[index] = steps.toBigInteger()
    }

    val lcm = stepsToEnd.reduce(::lcm)
    println(lcm)
}

private fun parseNodes(lines: List<String>) = lines.drop(1).associate { line ->
    val id = line.substringBefore(" =")
    val (left, right) = line.substringAfter("= ").removeSurrounding("(", ")").split(", ")
    id to Node(id, left, right)
}

// least common multiple
private fun lcm(a: BigInteger, b: BigInteger): BigInteger {
    return (a * b) / gcd(a, b)
}

// greatest common divisor (Euclidean algorithm)
private fun gcd(a: BigInteger, b: BigInteger): BigInteger {
    if (b == BigInteger.ZERO) {
        return a
    }
    return gcd(b, a % b)
}

private data class Node(val id: String, val leftChild: String, val rightChild: String)

