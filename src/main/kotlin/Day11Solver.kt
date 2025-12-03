package com.cormontia

import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day11Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay11_sample.txt""").readLines()

        val answerPart1 = solvePart1()
        println(answerPart1)
        val answerPart2 = solvePart2()
        println(answerPart2)
    }

    fun solvePart1(): Long {
        return 0L
    }

    fun solvePart2(): Long {
        return 0L
    }
}