package com.cormontia.solvers

import com.cormontia.utilities.Coor

class Day06Solver {

    fun solvePart1(numberGrid: Map<Coor, Long>, operations: List<String>): Long {
        var result = 0L
        var col = 0L
        for (operation in operations) {
            val numbers = numberGrid.filter { elt -> elt.key.col == col  }.map { elt -> elt.value }
            if (operation =="*") {
                val product = numbers.fold( 1) { acc: Long, number: Long -> acc * number }
                println("Product: $product")
                result += product
            } else {
                val sum = numbers.fold(0) { acc: Long, number: Long -> acc + number }
                println("Sum: $sum")
                result += sum
            }
            col++
        }
        return result
    }

    fun solvePart2(): Long {
        return 0L
    }
}