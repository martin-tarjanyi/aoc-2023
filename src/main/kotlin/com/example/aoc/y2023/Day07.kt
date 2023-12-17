package com.example.aoc.y2023

fun main() {
    part1()
    part2()
}

private fun part1() {
    inputLineSequence("day07.txt")
        .map { line ->
            val (cardString, bidString) = line.split(" ")
            HandForPart1(cardString, bidString.toLong())
        }
        .sortedWith(
            compareBy(HandForPart1::type)
                .thenComparing(HandForPart1.cardStrengthComparator)
                .reversed()
        )
        .withIndex()
        .sumOf { (index, handWithBid) ->
            val rank = index + 1
            val winning = rank * handWithBid.bid
            winning
        }
        .let { println(it) }
}

private fun part2() {
    inputLineSequence("day07.txt")
        .map { line ->
            val (cardString, bidString) = line.split(" ")
            HandForPart2(cardString, bidString.toLong())
        }
        .sortedWith(
            compareBy(HandForPart2::type)
                .thenComparing(HandForPart2.cardStrengthComparator)
                .reversed()
        )
        .withIndex()
        .sumOf { (index, handWithBid) ->
            val rank = index + 1
            val winning = rank * handWithBid.bid
            winning
        }
        .let { println(it) }
}

private data class HandForPart1(val cards: String, val bid: Long) {
    companion object {
        // strongest to weakest
        val strengthByCard: Map<Char, Int> = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
            .withIndex()
            .associate { (index, value) -> value to (index + 1) }

        val cardStrengthComparator = Comparator<HandForPart1> { hand1, hand2 ->
            require(hand1.cards.length == hand2.cards.length)
            hand1.cards.zip(hand2.cards).forEach { (card1, card2) ->
                if (card1 != card2) {
                    return@Comparator strengthByCard[card1]!! - strengthByCard[card2]!!
                }
            }

            return@Comparator 0
        }
    }

    fun type(): HandType {
        val countByCard = cards.groupingBy { it }.eachCount()

        return if (countByCard.size == 1) {
            HandType.FIVE_OF_A_KIND
        } else if (countByCard.values.contains(4)) {
            HandType.FOUR_OF_A_KIND
        } else if (countByCard.values.containsAll(listOf(3, 2))) {
            HandType.FULL_HOUSE
        } else if (countByCard.values.contains(3)) {
            HandType.THREE_OF_A_KIND
        } else if (countByCard.filter { (_, count) -> count == 2 }.size == 2) {
            HandType.TWO_PAIR
        } else if (countByCard.values.contains(2)) {
            HandType.ONE_PAIR
        } else {
            HandType.HIGH_CARD
        }
    }
}

private data class HandForPart2(val cards: String, val bid: Long) {
    companion object {
        // strongest to weakest
        val strengthByCard: Map<Char, Int> = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
            .withIndex()
            .associate { (index, value) -> value to (index + 1) }

        val cardStrengthComparator = Comparator<HandForPart2> { hand1, hand2 ->
            require(hand1.cards.length == hand2.cards.length)
            hand1.cards.zip(hand2.cards).forEach { (card1, card2) ->
                if (card1 != card2) {
                    return@Comparator strengthByCard[card1]!! - strengthByCard[card2]!!
                }
            }

            return@Comparator 0
        }
    }

    fun type(): HandType {
        val countByCard = cards.groupingBy { it }.eachCount()

        return if (countByCard.size == 1) {
            HandType.FIVE_OF_A_KIND
        } else if (countByCard.values.contains(4)) {
            if (countByCard['J'] == 1) {
                HandType.FIVE_OF_A_KIND
            } else if (countByCard['J'] == 4) {
                HandType.FIVE_OF_A_KIND
            } else {
                HandType.FOUR_OF_A_KIND
            }
        } else if (countByCard.values.containsAll(listOf(3, 2))) {
            if (countByCard.containsKey('J')) {
                HandType.FIVE_OF_A_KIND
            } else {
                HandType.FULL_HOUSE
            }
        } else if (countByCard.values.contains(3)) {
            if (countByCard['J'] == 1) {
                HandType.FOUR_OF_A_KIND
            } else if (countByCard['J'] == 2) {
                HandType.FIVE_OF_A_KIND
            } else if (countByCard['J'] == 3) {
                HandType.FOUR_OF_A_KIND
            } else {
                HandType.THREE_OF_A_KIND
            }
        } else if (countByCard.filter { (_, count) -> count == 2 }.size == 2) {
            if (countByCard['J'] == 1) {
                HandType.FULL_HOUSE
            } else if (countByCard['J'] == 2) {
                HandType.FOUR_OF_A_KIND
            } else {
                HandType.TWO_PAIR
            }
        } else if (countByCard.values.contains(2)) {
            if (countByCard['J'] == 1) {
                HandType.THREE_OF_A_KIND
            } else if (countByCard['J'] == 2) {
                HandType.THREE_OF_A_KIND
            } else {
                HandType.ONE_PAIR
            }
        } else {
            if (countByCard['J'] == 1) {
                HandType.ONE_PAIR
            } else {
                HandType.HIGH_CARD
            }
        }
    }
}

private enum class HandType {
    // strongest to weakest
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    THREE_OF_A_KIND,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD
}
