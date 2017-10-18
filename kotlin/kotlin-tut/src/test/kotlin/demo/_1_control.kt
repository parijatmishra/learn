package demo

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by parijat on 15/6/17.
 */
class _01_ControlTest {
    @Test fun _01_ifExpressionMax() {
        assertEquals(2, ifExpressionMax(1,2))
        assertEquals(2, ifExpressionMax(2,1))
    }

    @Test fun _02_whenExpressionFib() {
        assertEquals(1, whenExpressionFib(1))
        assertEquals(1, whenExpressionFib(2))
        assertEquals(2, whenExpressionFib(3))
        assertEquals(3, whenExpressionFib(4))
        assertEquals(5, whenExpressionFib(5))
        assertEquals(8, whenExpressionFib(6))
    }

    @Test fun _03_isNthFib() {
        assertTrue(isNthFib(8, n = 6))
        assertTrue(isNthFib(13, n = 7))
        assertFalse(isNthFib(8, n = 5))
    }

    @Test fun _04_whenRange() {
        assertEquals("RANGE", whenRange(1))
        assertEquals("RANGE", whenRange(10))

        assertEquals("INDETERMINATE", whenRange(11))
        assertEquals("INDETERMINATE", whenRange(20))

        assertEquals("OUT-OF-RANGE", whenRange(21))
    }
}