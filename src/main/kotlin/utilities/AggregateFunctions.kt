package com.cormontia.utilities

/**
 * Given a non-empty list of ints, give the value and index of the maximum.
 * E.g. [3,5,6,3,4] will result in 6 (the maximum) and 2 (the index of that element).
 * @param list A non-empty list of integers.
 * @return A pair (value, index) showing the highest value in the list, and its index.
 */
fun maxWithIndex(list: List<Int>): Pair<Int, Int> {
    var idx = 0
    var max = list[idx]

    for (i in 1 until list.size) {
        if (list[i] > max) {
            idx = i
            max = list[idx]
        }
    }

    return  Pair(max, idx)
}

class AggregateFunctions {
}