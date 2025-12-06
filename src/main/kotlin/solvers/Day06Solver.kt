package com.cormontia.solvers

import com.cormontia.utilities.Coor
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day06Solver {

    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay06.txt""").readLines()

        val (numberGrid, operations) = parseInputForPart1(inputAsStrings)
        val answerPart1 = solvePart1(numberGrid, operations)
        println("The grand total is $answerPart1.") // 5171061464548
        val answerPart2 = solvePart2(inputAsStrings)
        println("The corrected grand total is $answerPart2.") // 10189959087258
    }

    fun solvePart1(numberGrid: Map<Coor, Long>, operations: List<String>): Long {
        var result = 0L
        var col = 0L
        for (operation in operations) {
            val numbers = numberGrid.filter { elt -> elt.key.col == col  }.map { elt -> elt.value }
            if (operation =="*") {
                val product = numbers.fold( 1) { acc: Long, number: Long -> acc * number }
                result += product
            } else {
                val sum = numbers.fold(0) { acc: Long, number: Long -> acc + number }
                result += sum
            }
            col++
        }
        return result
    }

    fun parseInputForPart1(inputAsStrings: List<String>): Pair<Map<Coor, Long>, List<String>> {
        val numberGrid = mutableMapOf<Coor, Long>()
        val operations = mutableListOf<String>()
        var row = 0L
        for (line in inputAsStrings) {
            val components = line.split(" ").filter { str -> str.isNotBlank() }
            val firstString = components.first()
            val firstChar = firstString.elementAt(0)
            if (firstChar.isDigit()) {
                // Parse the numbers
                var col = 0L
                for (numberString in components) {
                    numberGrid[Coor(row, col)] = java.lang.Long.parseLong(numberString)
                    col++
                }
                row++
            } else {
                // Parse the operations
                operations.addAll(components)
            }
        }
        return Pair(numberGrid, operations)
    }

    fun solvePart2(lines: List<String>): Long {
        val numCols = lines.maxBy { l -> l.length }.length.toLong()
        val numRows = lines.size.toLong()

        val grid = parseInputForPart2(lines)

        var result = 0L
        val numbers = mutableListOf<Long>()
        for (col in numCols downTo 0) {

            var currentNumber = 0L
            for (row in 0 .. numRows) {
                val char = grid[Coor(row, col)]
                if (char != null && char.isDigit()) {
                    currentNumber = 10 * currentNumber + (char - '0')
                } else if (char == '+') {
                    numbers.add(currentNumber)
                    currentNumber = 0L

                    val sum = numbers.fold(0) { acc: Long, n: Long -> acc + n }
                    numbers.clear()
                    result += sum

                } else if (char == '*') {
                    numbers.add(currentNumber)
                    currentNumber = 0L

                    val product = numbers.filter { n -> n != 0L }.fold(1) { acc: Long, n: Long -> acc * n }
                    numbers.clear()
                    result += product

                } else {
                    // NOTE! This introduces zeroes into the sequence!!
                    // However - we can safely filter them out later.
                    // It WILL go wrong if, anywhere in a multiplication, there is a deliberate 0.
                    numbers.add(currentNumber)
                    currentNumber = 0L
                }
            }
        }


        return result
    }

    private fun parseInputForPart2(lines: List<String>): MutableMap<Coor, Char> {
        val grid = mutableMapOf<Coor, Char>()
        var row = 0L
        for (line in lines) {
            var col = 0L
            for (ch in line) {
                grid[Coor(row, col)] = ch
                col++
            }
            row++
        }
        return grid
    }
}