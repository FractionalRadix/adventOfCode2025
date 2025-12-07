package com.cormontia.solvers

import com.cormontia.utilities.Coor
import com.cormontia.utilities.buildGridMap
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day07Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay07.txt""").readLines()
        val grid = buildGridMap(inputAsStrings)

        val answerPart1 = solvePart1(grid) // 1570
        println(answerPart1)
        val answerPart2 = solvePart2(grid) // 15118009521693
        println(answerPart2)
    }

    fun solvePart1(grid: Map<Coor, Char>): Long {
        val start = grid.keys.first { key -> grid[key] == 'S' }
        val maxRows = grid.keys.maxBy { key -> key.row }.row
        var splits = 0L
        val beams = mutableSetOf<Long>()

        beams.add(start.col)
        for (row in start.row .. maxRows) {
            val oldBeams = beams.toList()
            for (beam in oldBeams) {
                if (grid[Coor(row, beam)] == '^') {
                    beams.remove(beam)
                    beams.add(beam-1)
                    beams.add(beam+1)
                    splits++
                }
            }
        }

        return splits
    }

    fun solvePart2(grid: Map<Coor, Char>): Long {
        val start = grid.keys.first { key -> grid[key] == 'S' }
        val maxRows = grid.keys.maxBy { key -> key.row }.row

        val beams = mutableMapOf<Long, Long>() // Maps beam idx to counter
        beams[start.col] = 1
        for (row in start.row .. maxRows) {
            val oldBeams = mutableListOf<Long>()
            oldBeams.addAll(beams.keys)
            for (beam in oldBeams) {
                if (grid[Coor(row, beam)] == '^') {
                    val beamsAtThisPoint = beams[beam]!!
                    // This number is removed, and added to the beams left and right.
                    beams[beam] = 0
                    beams[beam - 1] = (beams[beam - 1] ?: 0) + beamsAtThisPoint
                    beams[beam + 1] = (beams[beam + 1] ?: 0) + beamsAtThisPoint
                }
            }
        }

        return beams.values.sum()
    }

}