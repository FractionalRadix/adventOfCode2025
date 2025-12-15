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
        var sizes = rectangles.map { r -> r.size() }.toSet()
        println("There are ${sizes.size} different sizes: $sizes.")

        // First filter: select all rectangles that do not have a red tile "properly inside" them.
        var candidates = rectanglesWithoutRedTilesInside(rectangles, redTiles)
        println("Selected ${candidates.size} candidates.")
        sizes = candidates.map { r -> r.size() }.toSet()
        println("There are ${sizes.size} different sizes: $sizes.")

        // Second filter: select all rectangles that don't have a horizontal line segment straight through them.
        candidates = candidates.filter { r -> !isRectangleCrossedByHorizontalLineSegment(r, outline) }
        println("Selected ${candidates.size} candidates.")
        sizes = candidates.map { r -> r.size() }.toSet()
        println("There are ${sizes.size} different sizes: $sizes.")

        // Third filter: select all rectangles that don't have a vertical line segment straight through them.
        candidates = candidates.filter { r -> !isRectangleCrossedByVerticalLineSegment(r, outline) }
        println("Selected ${candidates.size} candidates.")
        sizes = candidates.map { r -> r.size() }.toSet()
        println("There are ${sizes.size} different sizes: $sizes.")


        //TODO!~ FOR TESTING.
        val badrects = candidates.filter { r -> r.size() == 40L }
        println(badrects)

        /** Here is the edge case: Marked by the '!'
         * ..............
         * .......#XXX#..
         * .......XXXXX..
         * ..OOOOOOO!XX..
         * ..OOOOOOOOXX..
         * ..OOOOOOOOXX..
         * .........XXX..
         * ..!......#X#..
         * ..............
         */


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
     * Given a list rectangle and a list of line segments, determine if any of the <em>horizontal</em> line segments
     * intersects with the rectangle.
     * If the line segment is ON the top or bottom of the rectangle, we still keep the rectangle!
     * @param rectangle A rectangle, defined by row and column coordinates.
     * @param lines A list of line segments, defined by row and column coordinates.
     * @return <code>true</code> if and only if (WE HOPE...!!!) at least one horizontal line segment crosses the rectangle.
     */
    fun isRectangleCrossedByHorizontalLineSegment(rectangle: Rectangle, lines: List<Pair<Coor, Coor>>): Boolean {
        val lineSegments = lines
            .asSequence()
            // Select only the horizontal line segments.
            .filter { (p, q) -> p.row == q.row }
            // Filter out the ones above the rectangle.
            .filter { (p, _) -> p.row > rectangle.top() }
            // Filter out the ones below the rectangle.
            .filter { (p, _) -> p.row < rectangle.bottom() }
            // Set them all in the same direction: p to the left, q to the right.
            .map { (p, q) -> if (p.col < q.col) { Pair(p,q) } else { Pair(q,p) } }
            // Filter out the ones to the left of the rectangle.
            .filter { (_, q) -> q.col < rectangle.left() }
            // Filter out the ones to the right of the rectangle.
            .filter { (p, _) -> p.col > rectangle.right() }
            //TODO!+ More filters
        return lineSegments.any()
    }

    /**
     * Given a list rectangle and a list of line segments, determine if any of the <em>vertical</em> line segments
     * intersects with the rectangle.
     * If the line segment is ON the left or right side of the rectangle, we still keep the rectangle!
     * @param rectangle A rectangle, defined by row and column coordinates.
     * @param lines A list of line segments, defined by row and column coordinates.
     * @return <code>true</code> if and only if (WE HOPE...!!!) at least one vertical line segment crosses the rectangle.
     */
    fun isRectangleCrossedByVerticalLineSegment(rectangle: Rectangle, lines: List<Pair<Coor, Coor>>): Boolean {
        val lineSegments = lines
            .asSequence()
            // Select only the vertical line segments.
            .filter { (p, q) -> p.col == q.col }
            // Filter out the ones left of the rectangle.
            .filter { (p, _) -> p.col < rectangle.left() }
            // Filter out the ones right of the rectangle.
            .filter { (p, _) -> p.col > rectangle.right() }
            // Set them all in the same direction: p above (lower row index), q below (higher row index)
            .map { (p, q) -> if (p.row < q.row) { Pair(p,q) } else { Pair(q,p) } }
            // Filter out the ones to the left of the rectangle.
            .filter { (_, q) -> q.row > rectangle.bottom () }
            // Filter out the ones to the right of the rectangle.
            .filter { (p, _) -> p.row < rectangle.top() }
        //TODO!+ More filters
        return lineSegments.any()
    }


    /**
     * Filter out all line segments that are completely to the left of the given rectangle.
     * @param rectangle A rectangle, in a 2D space defined by rows and columns.
     * @param lines A list of horizontal and vertical line segments, in a 2D space defined by rows and columns.
     * @return The set of line segments, without those that are completely to the left of the rectangle.
     */
    fun filterLineSegmentsToTheLeftOf(rectangle: Rectangle, lines: List<Pair<Coor, Coor>>) =
        lines.filter { (p,q) -> p.col < rectangle.left() && q.col < rectangle.left() }

    /**
     * Filter out all line segments that are completely to the left of the given rectangle.
     * @param rectangle A rectangle in a 2D space defined by rows and columns.
     * @param lines A list of line segments in a 2D space defined by rows and columns.
     * @return The set of line segments, without those that are completely to the right of the rectangle.
     */
    fun filterLineSegmentsToTheRightOf(rectangle: Rectangle, lines: List<Pair<Coor, Coor>>) =
        lines.filter { (p,q) -> p.col > rectangle.right() && q.col > rectangle.right() }

    /**
     * Filter out all line segments that are completely above the given rectangle.
     * Since this is a space defined by rows and columns, this filters out the line segments whose row coordinate
     * is <em>lower</em> than that of the top of the rectangle!
     * @param rectangle A rectangle in a 2D space defined by rows and columns.
     * @param lines A list of line segments in a 2D space defined by rows and columns.
     * @return The set of line segments, without those that are completely above the rectangle.
     */
    fun filterLineSegmentsAbove(rectangle: Rectangle, lines: List<Pair<Coor, Coor>>) =
        lines.filter { (p, q) -> p.row < rectangle.top() && q.row < rectangle.top() }

    /**
     * Filter out all line segments that are completely below the given rectangle.
     * Since this is a space defined by rows and columns, this filters out the line segments whose row coordinate
     * is <em>higher</em> than that of the bottom of the rectangle!
     * @param rectangle A rectangle in a 2D space defined by rows and columns.
     * @param lines A list of line segments in a 2D space defined by rows and columns.
     * @return The set of line segments, without those that are completely below the rectangle.
     */
    fun filterLineSegmentsBelow(rectangle: Rectangle, lines: List<Pair<Coor, Coor>>) =
        lines.filter { (p, q) -> p.row > rectangle.bottom() && q.row > rectangle.bottom() }

    data class Rectangle(val p: Coor, val q: Coor) {
        fun size(): Long {
            val rowDist = abs(p.row - q.row) + 1
            val colDist = abs(p.col - q.col) + 1
            return rowDist * colDist
        }

        fun left() = min(p.col, q.col)
        fun right() = max(p.col, q.col)
        fun top() = min(p.row, q.row)
        fun bottom() = max(p.row, q.row)

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