package com.cormontia.solvers

import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.max

class Day11Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay11.txt""").readLines()

        val edges = mutableMapOf<String, List<String>>()
        for (line in inputAsStrings) {
            val parts = line.split(":")
            val key = parts[0].trim()
            val values = parts[1].trim().split(" ").map { it.trim() }
            edges[key] = values
        }

        //for (k in edges.keys) {
        //    println("Key: $k, targets: ${edges[k]}")
        //}

        val answerPart1 = solvePart1(edges) // 599
        println(answerPart1)
        val answerPart2 = solvePart2(edges) // 6165 is TOO LOW. // 55485 is TOO LOW. // 24660 is TOO LOW.
        println(answerPart2)
    }

    fun solvePart1(edges: Map<String, List<String>>): Long {
        return countPaths1(edges, "you", "out")
    }

    fun solvePart2(edges: Map<String, List<String>>): Long {

        val bottlenecks = findBottlenecks(edges)
        println("Bottleneck nodes (should be 'ccc' and 'fff'): $bottlenecks.")

        val fftToDac = countPaths2(edges, "fft", "dac", "out")
        println("There are $fftToDac paths from 'fft' to 'dac'.")
        val dacToFft = countPaths2(edges, "dac", "fft", "out")
        println("There are $dacToFft paths from 'dac' to 'fft'.")

        // We assume this is a DAG.
        // Under that assumption, either "fftToDac" is 0 or "dacToFft" is 0.
        if (fftToDac == 0L) {
            // No path fromm FFT to DAC, so there are only paths from DAC to FFT.
            // And the only relevant paths are those from FFT to OUT.
            // The path is then SVR -*-> DAC -*-> FFT -*-> OUT
            val svrToDac = countPaths2(edges, "svr", "dac", "out")
            println("There are $svrToDac paths from 'svr' to 'dac'.")
            val fftToOut = countPaths1(edges, "fft", "out")
            println("There are $fftToOut from 'fft' to 'out'.")

            return svrToDac * dacToFft * fftToOut
        } else if (dacToFft == 0L) {
            val svrToFft = countPaths2(edges, "svr", "fft", "out")
            println("There are $svrToFft paths from 'svr' to 'fft'.")
            val dacToOut = countPaths1(edges, "dac", "out")
            println("There are $dacToOut from 'dac' to 'out'.")
            return svrToFft * fftToDac * dacToOut
        } else {
            println("Error! Input should be a Directed Acyclic Graph.")
            return -1L
        }
    }

    fun countPaths2(edges: Map<String, List<String>>, start: String, finish: String, abort: String): Long {
        val nodeList = mutableListOf<String>()
        val pathCount = mutableMapOf<String, Long>() // Maps node to the nr of paths going there.

        nodeList += start
        while (nodeList.isNotEmpty()) {
            val nextList = mutableListOf<String>()
            for (node in nodeList) {
                if (node == finish) {
                    //pathCount[finish] = pathCount[finish]!! + 1
                } else if (node == abort) {
                    //TODO?+
                    println("ABORT!")
                } else {
                    val nextNodes = edges[node] ?: emptyList()
                    val toBeRemoved = mutableSetOf<String>()

                    nextNodes.forEach { nextNode ->
                        if (pathCount[nextNode] == null) {
                            pathCount[nextNode] = 1
                        } else {
                            pathCount[nextNode] = pathCount[nextNode]!! + 1
                            toBeRemoved.add(nextNode)
                        }
                    }

                    val updatedNextNodes = nextNodes.filter { ! toBeRemoved.contains(it) }

                    nextList.addAll(updatedNextNodes)
                }
            }

            nodeList.clear()
            nodeList.addAll(nextList)
        }

        pathCount.forEach { (nodeName, count) -> println("pathCount[$nodeName]==$count")  }

        return pathCount[finish] ?: 0
    }

    /**
     * Find the "bottleneck" nodes.
     * A "bottleneck node" is a node that has multiple incoming edges, but for each of these the source has this node
     * as its ONLY target.
     * In other words, there are at least two nodes whose edges go  to the bottleneck... but for EVERY incoming node,
     * it is the ONLY target.
     * This makes them "bottlenecks" in the graph, a place where all paths converge.
     */
    fun findBottlenecks(edges: Map<String, List<String>>): Set<String> {
        val candidates = mutableSetOf<String>()
        candidates += edges.keys

        // First, filter all the nodes that are not the single target in any rule.
        for (edge in edges) {
            //println("Candidates: $candidates")
            if (edge.value.size > 1) {
                //println("...targets: ${edge.value} ")
                candidates -= edge.value.toSet()
                //println("Updated candidates: $candidates")
            }
        }

        // Second, filter all the nodes that have only one incoming edge.
        val toBeRemoved = mutableSetOf<String>()
        for (node in candidates) {
            // Find all edges where the candidate is among the target values.
            val incoming = edges.filter { edge -> edge.value.contains(node) }
            if (incoming.size <= 1) {
                toBeRemoved += node
            }
        }
        candidates.removeAll(toBeRemoved)

        return candidates
    }


    fun countPaths1(edges: Map<String, List<String>>, start: String, finish: String): Long {
        var counter = 0L

        val nodeList = mutableListOf<String>()

        nodeList += start
        while (nodeList.isNotEmpty()) {
            val nextList = mutableListOf<String>()
            for (node in nodeList) {
                if (node == finish) {
                    counter++
                } else {
                    if (!edges.containsKey(node)) {
                        println("No list for $node.")
                    } else if (edges[node] == null) {
                        println("edges[$node] is null.")
                    } else {
                        nextList.addAll(edges[node]!!)
                    }
                }
            }

            nodeList.clear()
            nodeList.addAll(nextList)
        }

        return counter
    }
}