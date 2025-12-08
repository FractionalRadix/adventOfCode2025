package com.cormontia.solvers

import java.lang.Long.parseLong
import javax.swing.Box
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.sqrt

class Day08Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay08.txt""").readLines()

        val list = mutableListOf<Coor3D>()
        for (line in inputAsStrings) {
            val components = line.split(",")
            val x = parseLong(components[0].trim())
            val y = parseLong(components[1].trim())
            val z = parseLong(components[2].trim())
            list.add(Coor3D(x,y,z))
        }

        val answerPart1 = solvePart1(list) // 131150
        println(answerPart1)
        val answerPart2 = solvePart2()
        println(answerPart2)
    }

    fun solvePart1(list: List<Coor3D>): Long {

        val pairs = mutableListOf<Pair<Coor3D, Coor3D>>()
        //TODO?~ Use "forEachIndexed"
        for (box1Index in list.indices) {
            for (box2index in (box1Index + 1) until list.size) {
                val box1 = list[box1Index]
                val box2 = list[box2index]
                pairs.add(Pair(box1, box2))
            }
        }

        val cutoff = 1000 // 10 for the sample input, 1000 for the real input
        val distances = pairs
            .map { (a,b) -> Triple(a, b, a.distance(b))}
            .sortedBy { (_, _, dist) -> dist }
            .take(cutoff)

        // Make a set of circuits.
        // Initially ever set contains only one box.
        val circuits = list.map { box -> setOf(box) }.toMutableSet()
        // For every pair, find the circuits that these boxes are in.
        for ((box1, box2, _) in distances) {
            //println(pair)
            val circuit1 = circuits.first { circuit -> circuit.contains(box1) }
            val circuit2 = circuits.first { circuit -> circuit.contains(box2) }
            if (circuit1 != circuit2) {
                val newCircuit = mutableSetOf<Coor3D>()
                newCircuit.addAll(circuit1)
                newCircuit.addAll(circuit2)
                circuits.remove(circuit1)
                circuits.remove(circuit2)
                circuits.add(newCircuit)
            }
        }

        val circuitSizes = circuits.map { circuit -> circuit.size }.sortedDescending()
        val topThree = circuitSizes.take(3)

        return topThree[0].toLong() * topThree[1].toLong() * topThree[2].toLong()

    }


    fun solvePart2(): Long {
        return 0L
    }
}

data class Coor3D(val x: Long, val y: Long, val z: Long) {

    fun distance(other: Coor3D) = sqrt(
        (   (x - other.x) * (x - other.x) +
                (y - other.y) * (y - other.y) +
                (z - other.z) * (z - other.z)
        ).toDouble()
    )
}

