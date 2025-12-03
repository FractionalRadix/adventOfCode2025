import com.cormontia.utilities.maxWithIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class AggregateFunctionsTester {

    @Test
    fun testMaxWithIndex() {
        val (val1, idx1) = maxWithIndex(listOf(3, 5, 6, 3, 4))
        assertEquals(6, val1)
        assertEquals(2, idx1)
    }
}