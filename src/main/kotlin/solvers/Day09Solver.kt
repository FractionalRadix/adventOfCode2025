package com.cormontia.solvers

import com.cormontia.utilities.Coor
import java.lang.Long.parseLong
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day09Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay09_sample.txt""").readLines()

        val redTiles = mutableListOf<Coor>()
        for (line in inputAsStrings) {
            val parts = line.split(",")
            val col = parseLong(parts[0])
            val row = parseLong(parts[1])
            redTiles.add(Coor(row, col))
        }

        val answerPart1 = solvePart1(redTiles) // 4771508457
        println("The largest rectangle has $answerPart1 tiles.")

        val answerPart2 = solvePart2(redTiles)
        println("The largest rectangle that contains only red and green tiles has $answerPart2 tiles.")
    }

    fun solvePart1(redTiles: List<Coor>): Long {
        var maxSize = 0L

        //TODO?~ Could use flatMap over map to determine the rectangles.
        for (tile1 in redTiles) {
            val biggestInGroup = redTiles.maxOf { tile2 -> Rectangle(tile1, tile2).size() }
            if (biggestInGroup > maxSize) {
                maxSize = biggestInGroup
            }
        }

        return maxSize
    }

    fun solvePart2(redTiles: List<Coor>): Long {

        val outline = buildShapeOutline(redTiles)
        val rectangles = buildRectangles(redTiles)

        println("Checking ${rectangles.size} rectangles.")

        // First filter: select all rectangles that do not have a red tile "properly inside" them.

        //var candidates = rectanglesWithoutRedTilesInside(rectangles, redTiles)
        //TODO!~ This second one is too strict :-(
        var candidates = rectanglesWithoutRedTilesInside2(rectangles, redTiles)


        println("Selected ${candidates.size} candidates.")
        var sizes = candidates.map { r -> r.size() }.toSet()
        println("There are ${sizes.size} different sizes: $sizes.")

        // Second filter: select all rectangles that don't have a line segment straight through it.
        candidates = rectanglesWithoutLinesCrossingThem(candidates, outline)

        println("Selected ${candidates.size} candidates.")
        sizes = candidates.map { r -> r.size() }.toSet()
        println("There are ${sizes.size} different sizes: $sizes.")

        //TODO!~ FOR TESTING.
        val badrects = candidates.filter { r -> r.size() == 40L }
        println(badrects)


        return sizes.max()
    }

    /**
     * Given a list of rectangles, return only those rectangles that have NO red tiles inside of them.
     * Note that the corners of the rectangles don't count - it's only if the red tile is <em>properly</em>
     * inside the rectangle, not if it is on the border.
     */
    fun rectanglesWithoutRedTilesInside(rectangles: List<Rectangle>, redTiles: List<Coor>): List<Rectangle> {
        val candidates = mutableListOf<Rectangle>()
        for (rect in rectangles) {

            // The rectangle is a "candidate" if there is not a SINGLE red tile "properly inside" it.
            if (redTiles.any { tile -> rect.properInside(tile) }) {
                continue
            } else {
                candidates.add(rect)
            }
        }
        return candidates
    }

    /**
     * Given a list of rectangles, select those that don't have red tiles inside them.
     * The red tiles CAN be on the borders of the rectangle, as long as they're not the tiles that define the rectangle.
     */
    fun rectanglesWithoutRedTilesInside2(rectangles: List<Rectangle>, redTiles: List<Coor>): List<Rectangle> {
        val candidates = mutableListOf<Rectangle>()
        for (rect in rectangles) {

            val tilesInside = redTiles
                .filter { tile -> rect.contains(tile) }
                .toMutableList()
            // If the only "tiles inside" are p and q, we're good.
            //TODO!~ TOO STRICT! This removes the "24" that is the right answer in the sample.
            val condition1 = (tilesInside.size == 1 && tilesInside.contains(rect.p))
            val condition2 = (tilesInside.size == 1 && tilesInside.contains(rect.q))
            val condition3 = (tilesInside.size == 2 && tilesInside.contains(rect.p) && tilesInside.contains(rect.p))
            val condition4 = tilesInside.isEmpty()

            if (condition1 || condition2 || condition3 || condition4) {
                candidates.add(rect)
            }
        }
        return candidates
    }

    fun rectanglesWithoutLinesCrossingThem(rectangles: List<Rectangle>, outline: List<Pair<Coor, Coor>>): List<Rectangle> {
        val newCandidates = mutableListOf<Rectangle>()
        newCandidates.addAll(rectangles)
        for (rect in rectangles) {
            val left = min(rect.p.col, rect.q.col)
            val right = max(rect.p.col, rect.q.col)
            val top = min(rect.p.row, rect.q.row)
            val bottom = max(rect.p.row, rect.q.row)
            for (line in outline) {

                // First, horizontal line segments.
                if (line.first.row == line.second.row) {
                    // Is the "row" coordinate of the segment BETWEEN "top" and "bottom"?
                    val row = line.first.row
                    if (row in (top + 1)..< bottom) {
                        // And if yes, is the LOW column coordinate BEFORE "left", and the HIGH colum coordinate AFTER "right"?
                        val lowColumn = min(line.first.col, line.second.col)
                        val highColumn = max(line.first.col, line.second.col)
                        if (lowColumn < left && highColumn > right) {
                            // Do NOT add this rectangle.
                            newCandidates.remove(rect)
                        }
                    }
                } else if (line.first.col == line.second.col) {
                    // Second, vertical line segments
                    val col = line.first.col
                    if (col in (left + 1) ..< right) {
                        val lowRow = min(line.first.row, line.second.row)
                        val highRow = max(line.first.row, line.second.row)
                        if (lowRow < top && highRow > bottom) {
                            // Do NOt add this rectangle.
                            newCandidates.remove(rect)
                        }
                    }
                } else {
                    // Should never reach here, all lines should be either horizontal or vertical.
                    println("OOPS! Diagonal line detected! $line.")
                }
            }
        }
        return newCandidates
    }

    fun ALT_solvePart2(redTiles: List<Coor>): Long {

        val outline = buildShapeOutline(redTiles)

        val verticalLines = outline.filter { (p,q) -> p.col == q.col }
        println("$verticalLines")
        // lines0== [(Coor(row=1, col=11), Coor(row=7, col=11)), (Coor(row=7, col=9), Coor(row=5, col=9)),
        //           (Coor(row=5, col=2), Coor(row=3, col=2)), (Coor(row=3, col=7), Coor(row=1, col=7))]
        println(pointInsideVerticalLines(Coor(7,7), verticalLines)) // Should be: false
        // lines1 == [(Coor(row=5, col=2), Coor(row=3, col=2)), (Coor(row=3, col=7), Coor(row=1, col=7))]
        // lines2 == []

        println()
        return 0L //TODO!~
    }

    data class Rectangle(val p: Coor, val q: Coor) {
        fun size(): Long {
            val rowDist = abs(p.row - q.row) + 1
            val colDist = abs(p.col - q.col) + 1
            return rowDist * colDist
        }

        fun contains(point: Coor): Boolean {
            val startRow = min(p.row, q.row)
            val endRow = max(p.row, q.row)
            val rowInside = point.row in startRow..endRow
            val startCol = min(p.col, q.col)
            val endCol = max(p.col, q.col)
            val colInside = point.col in startCol..endCol
            return rowInside && colInside
        }

        /**
         * is the given point inside the rectangle, and NOT on the edges?
         */
        fun properInside(point: Coor): Boolean {
            val startRow = min(p.row, q.row)
            val endRow = max(p.row, q.row)
            val rowInside = point.row in (startRow + 1)..< endRow
            val startCol = min(p.col, q.col)
            val endCol = max(p.col, q.col)
            val colInside = point.col in (startCol+1)..< endCol
            return rowInside && colInside
        }


        fun contains(line: Pair<Coor, Coor>): Boolean {
            //TODO?~ Is this assumption correct?
            // A line segment is inside the rectangle if both its endpoints are.
            return contains(line.first) && contains(line.second)
        }
    }

    fun buildShapeOutline(redTiles: List<Coor>): List<Pair<Coor, Coor>> {
        // The shape is closed.
        // Add the first tile again at the end.
        val closedTiles = mutableListOf<Coor>()
        closedTiles.addAll(redTiles)
        closedTiles.add(redTiles[0])

        val outline = closedTiles
            .windowed(2, 1)
            .map { elt -> Pair(elt[0], elt[1]) }

        return outline
    }

    fun buildRectangles(redTiles: List<Coor>): List<Rectangle> {
        val result = mutableListOf<Rectangle>()

        for (tileIdx1 in 0 until redTiles.size) {
            val tile1 = redTiles[tileIdx1]
            for (tileIdx2 in (tileIdx1 + 1) until redTiles.size) {
                val tile2 = redTiles[tileIdx2]
                result.add(Rectangle(tile1, tile2))
            }
        }

        return result
    }



    fun ALT_solvePart2_2(redTiles: List<Coor>): Long {

        val outline = buildShapeOutline(redTiles)

        val (verticalLines, horizontalLines) = outline.partition { (start, end) -> start.col == end.col }

        println("Vertical lines: $verticalLines")
        println("Horizontal lines: $horizontalLines")


        for (tile1 in redTiles) {
            for (tile2 in redTiles) {
                val rect = Rectangle(tile1, tile2)
                if (rectangleInside(rect, outline)) {
                    println("Found one! size==${rect.size()}")
                }
            }
        }


        val test = false
        if (test) {
            //println(verticalLines)
            //TEST CODE!
            val maxRows = redTiles.maxOf { coor -> coor.row } + 2
            val maxCols = redTiles.maxOf { coor -> coor.col } + 2
            for (row in 0 until maxRows) {
                for (col in 0 until maxCols) {
                    val point = Coor(row, col)
                    val insideVertical = pointInsideVerticalLines(point, verticalLines)
                    val insideHorizontal = pointInsideHorizontalLines(point, horizontalLines)
                    if (insideVertical && insideHorizontal) {
                        print("#")
                    } else {
                        print(".")
                    }
                }
                println()
            }
        }

        return 0L //TODO!~
    }

    /**
     * Test if the given rectangle is fully inside the outline.
     * This one hinges on an assumption! That the number of line segments crossed is the same for points on the same
     * row/column.
     */
    fun rectangleInside(rect: Rectangle, outline: List<Pair<Coor, Coor>>): Boolean {
        // Suppose you have the rectangle that is spun up by (5,2)-(3,7)
        // Then the other corners of the rectangle are (5,7) and (3,2).
        val left = min(rect.p.col, rect.q.col)
        val right = max(rect.p.col, rect.q.col)
        val top = min(rect.p.row, rect.q.row)
        val bottom = max(rect.p.row, rect.q.row)

        val linesBeforeLeft =  outline
            .filter { line -> line.first.col == line.second.col } // Vertical line
            .filter { line -> line.first.col < left }
            //TODO?+ .filter { line -> pointBetween(point.row, line.first.row, line.second.row) }
        val linesBeforeRight = outline
            .filter { line -> line.first.col == line.second.col } // Vertical line
            .filter { line -> line.first.col < right }
            //TODO?+

        val linesAboveTop = outline
            .filter { line -> line.first.row == line.second.row } // Horizontal line
            .filter { line -> line.first.row < top }
            //TODO?+

        val linesBelowBottom = outline
            .filter { line -> line.first.row == line.second.row } // Horizontal line
            .filter { line -> line.first.row < bottom }
            //TODO?+

        // APPROXIMATION! NOT A SOLUTION YET.
        return linesBeforeLeft == linesBeforeRight && linesAboveTop == linesBelowBottom
    }

    fun pointBetween(point: Long, startRange: Long, endRange: Long) =
        if (startRange < endRange) {
            point in startRange..endRange
        } else {
            point in endRange..startRange
        }

    fun pointInsideVerticalLines(point: Coor, lines0: List<Pair<Coor, Coor>>): Boolean {
        // EDGE CASE: Point is ON the line segment!
        val pointOnSegment = lines0.any { line -> pointOnLineSegment(point, line.first, line.second) }
        if (pointOnSegment) { return true }

        val lines1 = lines0.filter { line -> line.first.col < point.col }
        val lines2 = lines1.filter { line -> pointBetween(point.row, line.first.row, line.second.row) }
        val count = lines2.size
        return (count % 2 == 1)
    }

    fun pointInsideHorizontalLines(point: Coor, lines0: List<Pair<Coor, Coor>>): Boolean {
        // EDGE CASE: Point is ON the line segment!
        val pointOnSegment = lines0.any { line -> pointOnLineSegment(point, line.first, line.second) }
        if (pointOnSegment) { return true }

        val lines1 = lines0.filter { line -> line.first.row < point.row }
        val lines2 = lines1.filter { line -> pointBetween(point.col, line.first.col, line.second.col) }
        val count = lines2.size
        return (count % 2 == 1)
    }
    /**
     * Return true if and only if the given point is on the line segment.
     * The line segment is either horizontal or vertical, never diagonal.
     * @param point The point.
     * @param p Start of the line segment.
     * @param q End of the line segment.
     * @return <code>true</code> if and only if the point is on the line segment p-q.
     */
    fun pointOnLineSegment(point: Coor, p: Coor, q: Coor): Boolean {
        if (point.row == p.row && point.row == q.row) {
            // Vertical line segment
            val startCol = min(p.col, q.col)
            val endCol = max(p.col, q.col)
            return point.col in startCol..endCol
        } else if (point.col == p.col && point.col == q.col) {
            // Horizontal line segment
            val startRow = min(p.row, q.row)
            val endRow = max(p.row, q.row)
            return point.row in startRow..endRow
        } else {
            //TODO?+ Error message
            return false
        }
    }

}