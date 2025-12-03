package com.cormontia

import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.max

class Day03Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay03.txt""").readLines()

        val answerPart1 = solvePart1(inputAsStrings)
        println("The total output joltage is: $answerPart1.") // 17095
        val answerPart2 = solvePart2(inputAsStrings)
        println("Actually, the total output joltage is: $answerPart2.") // 168794698570517
    }

    fun solvePart1(ratings: List<String>): Long {
        var result = 0L
        for (rating in ratings) {
            result += joltage(rating)
        }
        return result
    }

    fun solvePart2(ratings: List<String>): Long {
        var result = 0L
        for (rating in ratings) {
            result += joltage12(rating)
        }
        return result
    }

    private fun joltage(input: String): Int {
        // First, turn the string into individual digits.
        val digits = input.map { ch -> ch.digitToInt() }
        // Find the highest element.
        var highest = digits.max()
        // Find the highest element after that.
        var skip = 1 + digits.indexOfFirst { digit -> digit == highest }
        // A special case is when this is the last element; when skip == digits.length .
        if (skip == digits.size) {
            highest = digits.dropLast(1).max()
            skip = 1 + digits.indexOfFirst { digit -> digit == highest }
            val remainingDigits = digits.drop(skip)
            val nextHighest = remainingDigits.max()
            return 10 * highest + nextHighest
        } else {
            val remainingDigits = digits.drop(skip)
            val nextHighest = remainingDigits.max()
            return 10 * highest + nextHighest
        }
    }

    private fun joltage12(input: String): Long {
        // First, turn the string into individual digits.
        var digits = input
            .map { ch -> ch.digitToInt() }
            .toList()

        // Then, keep taking the highest value that leaves enough "tail".
        var result = 0L

        for (tailLen in 11 downTo 0) {
            val (max1, idx1) = maxWithIndex(digits.dropLast(tailLen))
            digits = digits.drop(idx1 + 1)
            result = 10 * result + max1
        }

        return result
    }

    //TODO?~ Move to a utilities folder?
    /**
     * Given a non-empty list of ints, give the value and index of the maximum.
     * E.g. [3,5,6,3,4] will result in 6 (the maximum) and 2 (the index of that element).
     * @param list A non-empty list of integers.
     * @return A pair (value, index) showing the highest value in the list, and its index.
     */
    fun maxWithIndex(list: List<Int>): Pair<Int, Int> {
        var idx = 0
        var max = list[idx]

        for (i in 1 until list.size) {
            if (list[i] > max) {
                idx = i
                max = list[idx]
            }
        }

        return  Pair(max, idx)
    }

    //TODO!~ Add a unit test framework and add this test to it.
    private fun testMaxWithIndex() {
        val (val1, idx1) = maxWithIndex(listOf(3, 5, 6, 3, 4))
        if (val1 != 6 || idx1 != 2) {
            println("testMaxWithIndex: unit test failed!")
        } else {
            println("testMaxWithIndex: unit test succeeded!")
        }
    }

}