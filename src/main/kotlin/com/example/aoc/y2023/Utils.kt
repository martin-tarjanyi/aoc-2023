package com.example.aoc.y2023

import java.lang.IllegalArgumentException

fun inputLineSequence(fileName: String): Sequence<String> =
    Resource.javaClass.getResource("/inputs/$fileName")
        ?.readText()
        ?.lineSequence()
        ?.filter { it.isNotEmpty() }
        ?: throw IllegalArgumentException("Resource not found: $fileName")

fun <T> Sequence<T>.repeat(): Sequence<T> {
    return sequence {
        while (true) {
            yieldAll(this@repeat)
        }
    }
}

private object Resource

data class Position(val row: Long, val column: Long)

fun <T> List<T>.createPairs(): List<Pair<T, T>> {
    val result = mutableListOf<Pair<T, T>>()
    for ((i, elem) in this.withIndex()) {
        for (j in i + 1..<this.size) {
            result.add(Pair(elem, this[j]))
        }
    }
    return result
}
