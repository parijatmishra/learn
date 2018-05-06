import java.lang.ClassCastException

/*
 * as?
 */
open class Fruit
class Apple: Fruit()
class Pear: Fruit()

fun unsafeCast() {
    val fruit: Fruit = Apple()
    try {
        val pear: Pear = fruit as Pear
        println("ERROR!")
    } catch (e: ClassCastException) {
        println("correct: Apple is not Pear")
    }
}

fun safeCast() {
    val fruit: Fruit = Apple()
    val pear: Pear? = fruit as? Pear
    if (pear == null) {
        println("correct: Apple is not Pear")
    } else {
        println("ERROR!")
    }
}

fun testForWithIndex() {
    val ints = arrayOf(1, 2, 3)
    for ((idx, v) in ints.withIndex())
        print("${idx}:${v} ")
    println()
    // Should print: "0:1 1:2 2:3 "
}

fun testBreak() {
    for (i in 1..10) {
        if (i == 3)
            break
        print("$i ")
    }
    println()
    // Should print: "1 2 "
}

fun testBreakWithLabel() {
    outer@ for (i in 1..5) {
        for (j in i..10) {
            if (i + j > 8)
                break@outer
            print("${i+j} ")
        }
        println("Would never reach here!")
    }
    println()
    // Should print: "2 3 4 5 6 7 4 5 6 7 6 7 "
}

fun testContinue() {
    for (i in 1..10) {
        if (i % 2 == 0)
            continue
        print("${i} ")
    }
    println()
    // Should print "1 3 5 7 9 "
}

fun testContinueLabel() {
    outer@ for (i in 1..3) {
        for (j in i..5) {
            if (i % 2 == 0)
                continue@outer
            print("${i+j} ")
        }
    }
    println()
    // Should print "2 3 4 5 6 6 7 8 "
}

fun testDoWhile() {
    val ints = arrayOf(1, 2, 3)
    val it = ints.iterator()
    do {
        val y = it.next()
        print("${y} ")
    } while (it.hasNext())
    println()
    // Should print: "1 2 3 "
}

fun testIfExpression() {
    val y = if (10 > 2) 10 else 2
    println("${y}")
    // Should print: "10"
}

fun testInWhen() {
    val x = 10
    val y = when (x) {
        in 1..5 -> "<= 5"
        in 6..10 -> ">= 5"
        else -> "Unknown!"
    }
    println("${y}")
    // Should print: ">= 5
}

fun testIsWhen() {
    val x: Any = "Foo"
    when (x) {
        is String -> println("x is a String")
        else -> println("x is not a String")
    }
    // Should print/: "x is a String"
}

fun main(args: Array<String>) {
    println("Hello, World!")
    print("as:                          "); unsafeCast()
    print("as?:                         "); safeCast()
    print("testForWithIndex:            "); testForWithIndex()
    print("testBreak:                   "); testBreak()
    print("testBreakWithLabel:          "); testBreakWithLabel()
    print("testContinue:                "); testContinue()
    print("testContinueLabel:           "); testContinueLabel()
    print("testDoWhile:                 "); testDoWhile()
    print("testIfExpression:            "); testIfExpression()
    print("testInWhen:                  "); testInWhen()
    print("testIsWhen:                  "); testIsWhen()

}
