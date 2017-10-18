package demo

import org.junit.Assert.assertEquals
import org.junit.Test

fun f1(vararg bs: String): Int {
    for ((index, value) in bs.withIndex())
        println("bs[${index}]=${value}")
    return bs.size
}

fun f2(vararg bs: String, l: () -> Int): Int {
    for ((index, value) in bs.withIndex())
        println("bs[${index}]=${value}")
    return bs.size + l.invoke()
}

class _5_VarargsTests {
    @Test fun testF1() {
        assertEquals(3, f1("one", "two", "three"))
    }

    @Test fun testF1Empty() {
        assertEquals(0, f1())
    }

    @Test fun testF2() {
        val arr = arrayOf("four", "five")
        val ret = f2("one", "two", "three", *arr, "six") { 4 } // { 4 } === { -> 4 }
        assertEquals(10, ret)
    }
}