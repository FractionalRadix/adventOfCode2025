package com.cormontia.solvers

import kotlin.io.path.Path
import kotlin.io.path.readLines

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

        /*
        val testSet1 = mutableMapOf<String, List<String>>()
        testSet1["svr"] = listOf("aaa", "bbb", "ccc")
        testSet1["aaa"] = listOf("out")
        testSet1["bbb"] = listOf("out")
        testSet1["ccc"] = listOf("out")
        println("3==${countPaths1(testSet1, "svr", "out")}")
        println("3==${countPaths2(testSet1, "svr", "out")}")
         */

        val testSet2 = mutableMapOf<String, List<String>>()
        testSet2["svr"] = listOf("aaa", "bbb")
        testSet2["aaa"] = listOf("ccc")
        testSet2["bbb"] = listOf("ccc")
        testSet2["ccc"] = listOf("eee", "fff")
        testSet2["eee"] = listOf("out")
        testSet2["fff"] = listOf("out")
        println("4==${countPaths1(testSet2, "svr", "out")}")
        println("4==${countPaths2(testSet2, "svr", "out")}")


        println(countIncomingPaths(edges, "you", "out"))

        val answerPart1 = solvePart1(edges) // 599
        println(answerPart1)
        val answerPart2 = solvePart2(edges) // 6165 is TOO LOW. // 55485 is TOO LOW. // 24660 is TOO LOW.
        println(answerPart2)
    }

    fun solvePart1(edges: Map<String, List<String>>): Long {
        return countPaths1(edges, "you", "out")
    }

    fun solvePart2(edges: Map<String, List<String>>): Long {

        //val bottlenecks = findBottlenecks(edges)
        //println("Bottleneck nodes  $bottlenecks.") // For sample input 2, should be "ccc" and "fff".

        val fftToDac = countPaths2(edges, "fft", "dac")
        println("There are $fftToDac paths from 'fft' to 'dac'.")
        val dacToFft = countPaths2(edges, "dac", "fft")
        println("There are $dacToFft paths from 'dac' to 'fft'.")

        // We assume this is a DAG.
        // Under that assumption, either "fftToDac" is 0 or "dacToFft" is 0.
        if (fftToDac == 0L) {
            // No path fromm FFT to DAC, so there are only paths from DAC to FFT.
            // And the only relevant paths are those from FFT to OUT.
            // The path is then SVR -*-> DAC -*-> FFT -*-> OUT
            val svrToDac = countPaths2(edges, "svr", "dac")
            println("There are $svrToDac paths from 'svr' to 'dac'.")
            val fftToOut = countPaths1(edges, "fft", "out")
            println("There are $fftToOut from 'fft' to 'out'.")

            return svrToDac * dacToFft * fftToOut
        } else if (dacToFft == 0L) {
            val svrToFft = countPaths2(edges, "svr", "fft")
            println("There are $svrToFft paths from 'svr' to 'fft'.")

            //BUGGED! countPaths1 yields 18, countPaths2 yields 6165 . At least one of them is wrong.
            val dacToOut = countPaths2(edges, "dac", "out")
            println("There are $dacToOut from 'dac' to 'out'.")
            return svrToFft * fftToDac * dacToOut
        } else {
            println("Error! Input should be a Directed Acyclic Graph.")
            return -1L
        }
    }

    fun countPaths2(edges: Map<String, List<String>>, start: String, finish: String): Long {
        val nodeList = mutableListOf<String>()
        val pathCount = mutableMapOf<String, Long>() // Maps node to the nr of paths going there.

        nodeList += start
        while (nodeList.isNotEmpty()) {
            val nextList = mutableListOf<String>()
            for (node in nodeList) {
                if (node == finish) {
                    //pathCount[finish] = pathCount[finish]!! + 1
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

        // This is not the path count... the path count is the PRODUCT of everything going in here.

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

    //TODO?- Not sure if it's correct, but it's a depth-first search that is slow.
    /**
     * Count the number of paths from `start` to `finish`.
     */
    fun countIncomingPaths(edges: Map<String, List<String>>, start: String, finish: String): Long {
        // This time, we try it recursively.
        // First, find all paths that go to `finish`.
        val incomingEdges = edges.filter { (_, targetNodeList) -> targetNodeList.contains(finish) }
        var counter = 0L
        for (prev in incomingEdges.keys) {
            //print("$prev ")
            if (prev == start) {
                return 1L
            } else {
                counter += countIncomingPaths(edges, start, prev)
            }
            print("$counter ")
        }
        return counter
    }
}