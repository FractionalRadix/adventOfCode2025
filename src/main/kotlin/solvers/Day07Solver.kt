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
        val answerPart2 = solvePart2()
        println(answerPart2)
    }

    fun solvePart1(grid: MutableMap<Coor, Char>): Long {
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

    fun solvePart2(): Long {
        return 0L
    }
}