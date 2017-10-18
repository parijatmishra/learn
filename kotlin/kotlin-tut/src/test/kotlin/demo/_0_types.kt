package demo

import org.junit.Test
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals

class _00_TypesTest() {
    @Test fun _00_testIntArrayOf() {
        val i123: IntArray = intArrayOf(1,2,3)
        assertEquals(i123[1], 2)

        val i123_a: Array<Int> = arrayOf(1,2,3)
        assertEquals(i123_a[1], 2)
    }

    @Test fun _01_testArrayLambdaConstructor() {
        val f = Array(5, { i -> (i*i).toString() }) // Array<String>
        assertEquals(f[0], "0")
        assertEquals(f[1], "1")
        assertEquals(f[2], "4")
    }

    @Test fun _02_stringTemplateComputation() {
        assertEquals("1+2=3", "1+2=${1+2}")
    }

    @Test fun _03_stringTemplateVarsAndMethods() {
        val a = 1
        val b = "foo"
        assertEquals("\$a=1 \$b='foo' (3)", "\$a=${a} \$b='${b}' (${b.length})")
    }

    @Test fun _04_stringsMargin() {
        val text1 = """
        |for (c in "foo")
        |    print(c)
        """.trimMargin()
        val text2 = """
        >for (c in "foo")
        >    print(c)
        """.trimMargin(">")

        assertEquals(text1, text2)
    }
}