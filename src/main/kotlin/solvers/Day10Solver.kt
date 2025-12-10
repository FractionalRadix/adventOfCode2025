package com.cormontia.solvers

import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.min

class Day10Solver {
    fun solve() {
        val inputAsStrings: List<String> = Path("""src/main/resources/inputFiles/AoCDay10_sample.txt""").readLines()

        val machines = mutableListOf<Machine>()
        for (input in inputAsStrings) {

            val parts = input.trim().split(" ")
            val indicatorPart = parts[0].drop(1).takeWhile { ch -> ch != ']' }
            val lights = MachineState(indicatorPart.map { ch -> ch == '#' })

            val buttons = mutableListOf<List<Int>>()
            for (part in parts.drop(1).dropLast(1)) {
                // Drop the parentheses, then split into individual numbers.
                val numbers = part.drop(1).dropLast(1).split(",").map { str -> str.toInt() }
                buttons.add(numbers)
            }

            // Optimization: prioritize the step that increases the most.
            buttons.sortByDescending { it.size }

            // The last part is the joltage requirements.
            val reqsAsString = parts.takeLast(1).first()
            val requiredJoltages = reqsAsString.trim().drop(1).dropLast(1).split(",").map { str -> str.toLong() }

            val machine = Machine(lights, buttons, requiredJoltages)
            machines.add(machine)

            //machine.print()
        }

        val answerPart1 = solvePart1(machines) // 542
        println("The lowest number of button presses to activate the machines is $answerPart1.")
        val answerPart2 = solvePart2(machines)
        println("The lowest number of button presses to reach the required joltage levels is $answerPart2.")
    }


    fun solvePart1(machines: List<Machine>): Long {

        var sum = 0L

        for (machine in machines) {

            var count = 0L

            // For this first part, a simple breadth-first search might suffice.
            // Note that pushing the same button twice in a row has NO effect!

            val stateLen = machine.desiredIndicatorState.indicators.size

            var states = mutableListOf<MachineState>()
            val q0 = MachineState(List(stateLen) { false })
            states += q0

            breadthFirstSearch@
            do {
                val newList = mutableListOf<MachineState>()
                count++
                for (state in states) {

                    // We have a machine in a given state.
                    // The machine has several buttons.
                    val button = machine.buttons
                    for (buttonIdx in 0 until button.size) {
                        val nextState = machine.pushButton(buttonIdx, state)
                        newList += nextState
                        if (machine.isDesiredState(nextState)) {
                            sum += count
                            break@breadthFirstSearch
                        }
                    }
                }

                states = newList
            } while (true) //TODO?~ We leave it using the labelled break, but `while (true)` is still ugly...
        }

        return sum
    }

    fun solvePart2(machines: List<Machine>): Long {
        var sum = 0L

        for (machine in machines) {
            //TODO!~ Also SOLVE the equations...
            machine.equations()
        }

        return sum
    }

    fun solvePart2_DFS(machines: List<Machine>): Long {

        // Let's try a depth-first search.
        // But it has cutoffs. If a value becomes too large, or if the search depth equals an earlier depth.

        var sum = 0L
        for (machine in machines) {

            machine.print()

            bestDepth = null
            val presses = minimumPresses(machine)
            println(" $presses button presses.")
            sum += presses
        }

        return sum
    }

    private fun minimumPresses(machine: Machine): Long {

        val initialJoltages = List(machine.joltageRequirements.size) { 0L }
        val q0 = Joltages( initialJoltages)

        pressRecursive(0L, machine, q0)
        return bestDepth!!
    }

    var bestDepth: Long? = null

    private fun pressRecursive(depth: Long, machine: Machine, joltages: Joltages) {

        if (depth == bestDepth) {
            return
        }

        //println("$depth {$joltages}")

        if (machine.hasDesiredJoltages(joltages)) {
            //println("Proper joltage achieved in $depth steps!")
            bestDepth = depth
        }

        if (machine.exceedsDesiredJoltages(joltages)) {
            return
        }

        for (buttonIdx in 0 until machine.buttons.size) {
            val nextJoltages = machine.pushButton(buttonIdx, joltages)
            //println("$nextJoltages (${machine.joltageRequirements})")
            pressRecursive(depth + 1, machine, nextJoltages)
        }
    }



    data class MachineState(
        val indicators: List<Boolean>
    )

    data class Joltages(
        val values: List<Long>
    )

    class Machine(
        val desiredIndicatorState: MachineState,
        val buttons: List<List<Int>>,
        val joltageRequirements: List<Long>,
    ) {
        fun pushButton(button: Int, state: MachineState): MachineState {
            val newState = mutableListOf<Boolean>()
            newState.addAll(state.indicators)

            val updates = buttons[button]
            for (update in updates) {
                newState[update] = !newState[update]
            }

            return MachineState(newState)
        }

        fun pushButton(button: Int, joltages: Joltages): Joltages {
            val newJoltageValues = mutableListOf<Long>()
            newJoltageValues.addAll(joltages.values)

            val updates = buttons[button]
            for (update in updates) {
                newJoltageValues[update] += 1
            }

            return Joltages(newJoltageValues)
        }

        fun equations() {
            val variables = "abcdefghijklmnopqrstuvwxyz"

            for (colIdx in 0 until joltageRequirements.size) {
                var equation = ""

                for (buttonIdx in 0 until buttons.size) {
                    val variable = variables[buttonIdx]
                    val button = buttons[buttonIdx]
                    if (button.contains(colIdx)) {
                        equation += "+ $variable"
                    }
                }
                equation += "= ${joltageRequirements[colIdx]}"
                println(equation)
            }


        }

        fun isDesiredState(state: MachineState) =
            state == desiredIndicatorState

        fun hasDesiredJoltages(joltages: Joltages): Boolean {
            // return joltages == joltageRequirements // Yields 'false' even if the lists are structurally the same.
            // return joltages.equals(joltageRequirements) // Yields 'false' even if the lists are structurally the same.
            for (i in 0 until joltages.values.size) {
                if (joltages.values[i] != joltageRequirements[i]) {
                    return false
                }
            }
            return true
        }

        fun exceedsDesiredJoltages(joltages: Joltages): Boolean {
            for (i in 0 until joltageRequirements.size) {
                if (joltages.values[i] > joltageRequirements[i])
                    return true
            }
            return false
        }

        fun print() {
            println()

            // Print the desired indicator state.
            print("[")
            for (indicator in desiredIndicatorState.indicators) {
                if (indicator) {
                    print('#')
                } else
                    print(".")
            }
            print("]")

            // Print the buttons.
            for (button in buttons) {
                print("(")
                for (number in button) {
                    print("$number ")
                }
                print(")")
            }

            // Print the joltage requirements.
            print(" {")
            for (req in joltageRequirements) {
                print("$req ")
            }
            print("}")
        }
    }

}