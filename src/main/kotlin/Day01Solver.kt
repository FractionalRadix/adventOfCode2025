package com.cormontia

import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day01Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay01.txt""").readLines()
        val instructions = inputAsStrings
            .map { s -> Pair(s[0], Integer.parseInt(s.drop(1))) }

        val answerPart1 = solvePart1(instructions)
        println("The password is ${answerPart1}.") // 1011
        val answerPart2 = solvePart2(instructions)
        println("Actually, the password is ${answerPart2}.") // 5937
    }

    private fun solvePart1(instructions: List<Pair<Char, Int>>): Int {
        var cur = 50
        var count = 0
        for (instruction in instructions) {
            if (instruction.first == 'L') {
                cur -= instruction.second
            } else if (instruction.first == 'R') {
                cur += instruction.second
            }

            while (cur < 0) {
                cur += 100
            }
            if (cur >= 100) {
                cur %= 100
            }

            if (cur == 0) {
                count++
            }
        }

        return count

    }

    private fun solvePart2(instructions: List<Pair<Char, Int>>): Int {
        var cur = 50
        var count = 0
        for (instruction in instructions) {

            val remainder = instruction.second % 100
            val fullTurns = (instruction.second - remainder) / 100

            if (instruction.first == 'L') {
                count += fullTurns

                // We want to look at the range (cur, cur - remainder].
                // Kotlin does not support ranges going downwards so we start at "cur - remainder".
                val range = IntRange(cur - remainder, cur - 1)
                if (range.contains(0) || range.contains(-100))
                    count++

                cur = (cur - remainder + 200) % 100

            } else if (instruction.first == 'R') {
                count += fullTurns

                val range = IntRange(cur + 1, cur + remainder)
                if (range.contains(0) || range.contains(100))
                    count++

                cur = (cur + remainder) % 100
            }
        }

        return count
    }
}