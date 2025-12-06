package com.cormontia.solvers

import java.lang.Long.parseLong
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day05Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay05_sample.txt""").readLines()

        val ranges = mutableListOf<LongRange>()
        val ingredients = mutableListOf<Long>()
        for (line in inputAsStrings) {
            if (line.contains("-")) {
                val parts = line.split("-")
                val start = parseLong(parts[0])
                val end = parseLong(parts[1])
                ranges.add(LongRange(start,end))
            } else {
                if (line.isNotEmpty()) {
                    ingredients.add(parseLong(line))
                }
            }
        }

        val answerPart1 = solvePart1(ranges, ingredients)
        println("There are $answerPart1 fresh ingredients.") // 613
        val answerPart2 = solvePart2(ranges)
        println(answerPart2)
    }

    fun solvePart1(ranges: List<LongRange>, ingedients: List<Long>): Long {
        var fresh = 0L
        for (ingredient in ingedients) {
            for (range in ranges) {
                if (range.contains(ingredient)) {
                    //println("$ingredient is in $range.")
                    fresh++
                    break
                }
            }
        }
        return fresh
    }

    fun solvePart2(ranges: List<LongRange>): Long {

        val finalSet = mutableListOf<LongRange>()

        val bucket = mutableListOf<LongRange>()
        bucket.addAll(ranges)

        while (bucket.isNotEmpty()) {

            val subject = bucket.first()
            bucket.remove(subject)

            val overlap = overlapsSingleRange(subject, bucket)
            bucket.removeAll(overlap.usedRanges)

            if (overlap.usedRanges.isEmpty()) {
                finalSet.add(overlap.combinedRange)
            } else {
                println("Adding: ${overlap.combinedRange}")
                bucket.add(overlap.combinedRange)
            }

            println("Bucket: $bucket")
            println("Final set: $finalSet")

        }


        return 0L
    }

    data class OverlapResult(
        val combinedRange: LongRange,
        val usedRanges: List<LongRange>,
        val unusedRanges: List<LongRange>,
    )

    /**
     * Given a range and a list of ranges, find the composite of all ranges that overlap with the subject range.
     * @param subject The range that might overlap with others.
     * @param allRanges The list of ranges that may or may not overlap with the subject range.
     * @return The composite of all ranges that overlap with the subject, the list of ranges used for this result,
     *  and the list of ranges that were NOT used.
     *  Note that the remaining ranges are those that do not overlap with the <em>original</em> subject, but may
     *  still overlap with the <em>resulting</em> range!
     */
    fun overlapsSingleRange(subject: LongRange, allRanges: List<LongRange>): OverlapResult {
        // First, select all ranges that overlap with our subject range.
        val (overlappingRanges, unusedRanges) = allRanges.partition { range -> overlap(range, subject) }
        // Now find the start of the earliest range, and the end of the latest range.
        // This is the combined new range.
        var newRange = subject
        if (overlappingRanges.isNotEmpty()) {
            val start = overlappingRanges.minBy { range -> range.first }.first
            val end = overlappingRanges.maxBy { range -> range.last }.last
            newRange = LongRange(start, end)
        }
        // Return the new range and the rest.
        // Note that while the "unusedRanges" don't overlap with the ORIGINAL subject, they may still overlap with the NEW range!
        return OverlapResult(combinedRange = newRange, usedRanges = overlappingRanges, unusedRanges = unusedRanges)
    }


    /**
     * Determine if two ranges overlap.
     * @param rangeA One of the two ranges.
     * @param rangeB The other range.
     * @return <code>true</code> if and only if the two ranges overlap.
     */
    fun overlap(rangeA: LongRange, rangeB: LongRange): Boolean {
        if (rangeA.first < rangeB.first) {
            // Three possibilities:
            // 1. The two ranges are completely separate. rangeA ends before rangeB begins.
            // 2. Some overlap. rangeA ends after rangeB begins, but before rangeB ends.
            // 3. Full overlap: rangeB is contained in rangeA. rangeA ends after rangeB ends.
            return rangeA.last >= rangeB.first
        } else {
            // rangeB.first <= rangeA.first
            return rangeB.last >= rangeA.first
        }
    }

    fun joinRanges(rangeA: LongRange, rangeB: LongRange): List<LongRange> {
        var firstRange: LongRange
        var secondRange: LongRange

        if (rangeA.first < rangeB.first) {
            firstRange = rangeA
            secondRange = rangeB
        } else {
            firstRange = rangeB
            secondRange = rangeA
        }

        if (firstRange.last < secondRange.first) {
            return listOf(firstRange, secondRange)
        } else {
            // WAIT! If secondRange.last < firstRange.last you'd miss out!
            val joinedRange = LongRange(firstRange.first,  Math.max(firstRange.last, secondRange.last))
            return listOf(joinedRange)
        }
    }
}