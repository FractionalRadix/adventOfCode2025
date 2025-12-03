package com.cormontia

import java.lang.Long.parseLong
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day02Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay02.txt""").readLines()
        val list = inputAsStrings[0]
            .split(",")
            .map { str -> str.trim() }
            .map { str -> str.split("-") }
            .map { elt -> Pair(elt[0], elt[1])}

        val answerPart1 = solvePart1(list)
        println("The sum of the invalid IDs is $answerPart1.") // 17077011375
        val answerPart2 = solvePart2(list)
        println("Actually, the sum of invalid IDs is $answerPart2.") // 36037497037
    }

    private fun solvePart1(ranges: List<Pair<String, String>>): Long {
        var result = 0L

        for (range in ranges) {
            result += sumOfInvalidIDsInRange(range.first, range.second)
        }

        return result
    }

    private fun sumOfInvalidIDsInRange(start: String, end: String): Long {
        val lenStart = start.length
        val lenEnd = end.length

        var result = 0L

        // Only numbers of even length can be doubles.
        // So if the start and end of the range have the same length and that length is odd... discard right away.
        if (lenStart == lenEnd && lenStart % 2 == 1) {
            return 0
        }

        // If the length of the start value is an odd number, find the lowest value above it that has an even length.
        // For example, if the range starts with 885, the length is 3.
        // The first value above that with length 4 is 1000. (And the first invalid ID would be 1010, if you want).
        var realStart = start
        if (lenStart % 2 == 1) {
            realStart = "1" + "0".repeat(lenStart)
        }
        
        // Now that the "realStart" is guaranteed to have an even length, take its first half.
        val startString = realStart.take(realStart.length / 2)

        var firstHalf = startString
        val startValueAsLong = parseLong(start)
        val endValueAsLong = parseLong(end)
        while (true) {
            val candidate = firstHalf + firstHalf
            val candidateAsLong = parseLong(candidate)
            if (candidateAsLong <= endValueAsLong) {
                if (candidateAsLong >= startValueAsLong) {
                    result += candidateAsLong
                }
            }
            else {
                break
            }
            val firstHalfAsLong = parseLong(firstHalf)
            firstHalf =  (firstHalfAsLong + 1).toString()
        }

        return result
    }

    fun solvePart2(ranges: List<Pair<String, String>>): Long {
        var result = 0L

        for (range in ranges) {
            result += sumOfNewInvalidIDsInRange(range.first, range.second)
        }

        return result
    }

    fun sumOfNewInvalidIDsInRange(start: String, end: String): Long {
        var result = 0L
        val startAsLong = parseLong(start)
        val endAsLong = parseLong(end)
        for (candidate in startAsLong .. endAsLong) {
            if (isRepeatingString(candidate)) {
                result += candidate
            }
        }
        return result
    }

    fun isRepeatingString(candidate: Long): Boolean {
        val candidateAsString = candidate.toString()
        val candidateLength = candidateAsString.length
        for (prefixLen in 1..1 + (candidateLength / 2)) {

            var multiplier = 2
            while (multiplier * prefixLen <= candidateLength) {
                if (multiplier * prefixLen == candidateLength) {
                    val pattern = candidateAsString.take(prefixLen).repeat(multiplier)
                    if (pattern == candidateAsString) {
                        return true
                    }
                }
                multiplier++
            }

        }
        return false
    }
}