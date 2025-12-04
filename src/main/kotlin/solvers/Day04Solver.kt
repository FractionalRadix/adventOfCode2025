package com.cormontia.solvers

import com.cormontia.utilities.Coor
import com.cormontia.utilities.Grid
import com.cormontia.utilities.buildGridMap
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day04Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay04.txt""").readLines()
        val gridMap  = buildGridMap(inputAsStrings)
        val grid = Grid(gridMap)

        val answerPart1 = solvePart1(grid)
        println("The number of accessible paper rolls is $answerPart1.") // 1449
        val answerPart2 = solvePart2(grid)
        println("The total number of paper rolls that can be removed is $answerPart2.") // 8746
    }

    fun solvePart1(grid: Grid): Long {
        var result = 0L
        for (row in grid.minRow .. grid.maxRow) {
            //println()
            for (col in grid.minCol .. grid.maxCol) {
                // Is this position a paper roll? If not, move on to the next.
                val coor = Coor(row, col)
                if (grid.elementAt(coor) != '@') {
                    //print(".")
                    continue
                }

                // Find the 8 neighbours of the paper roll.
                val neighbours = grid.neighbours8(Coor(row, col))
                val filledNeighbours = neighbours
                    .mapNotNull { coor -> grid.elementAt(coor) }
                    .count { ch -> ch == '@' }
                if (filledNeighbours < 4) {
                    result++
                    //print("x")
                } else {
                    //print("@")
                }

            }
        }
        return result
    }

    fun solvePart2(grid: Grid): Long {
        var totalRollsRemoved = 0L
        var grid2 = grid
        while (true) {
            val (removed, grid3) = removeRolls(grid2)
            totalRollsRemoved += removed
            grid2 = grid3
            if (removed == 0L) {
                break
            }
        }
        return totalRollsRemoved
    }

    /**
     * Given a grid, remove all the paper rolls ('@' symbols) that have less than 4 paper rolls near them.
     * Return the new grid AND the amount of paper rolls removed.
     */
    fun removeRolls(grid: Grid): Pair<Long, Grid> {

        val newGrid = mutableMapOf<Coor, Char>()
        var result = 0L
        for (row in grid.minRow .. grid.maxRow) {
            //println()
            for (col in grid.minCol .. grid.maxCol) {
                // Is this position a paper roll? If not, move on to the next.
                val coor = Coor(row, col)
                if (grid.elementAt(coor) != '@') {
                    //print(".")
                    newGrid[coor] = grid.elementAt(coor)!!
                    continue
                }

                // Find the 8 neighbours of the paper roll.
                val neighbours = grid.neighbours8(Coor(row, col))
                val filledNeighbours = neighbours
                    .mapNotNull { coor -> grid.elementAt(coor) }
                    .count { ch -> ch == '@' }
                if (filledNeighbours < 4) {
                    result++
                    newGrid[coor] = 'x'
                    //print("x")
                } else {
                    newGrid[coor] = '@'
                    //print("@")
                }

            }
        }

        return Pair(result, Grid(newGrid))
    }
}