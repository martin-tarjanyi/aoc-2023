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
