package demo

/**
 * Created by parijat on 13/6/17.
 */

fun ifExpressionMax(a: Int, b: Int): Int =
    if (a > b)
        a
    else
        b

fun whenExpressionFib(n: Int): Long =
        when (n) {
            1, 2 -> 1
            else ->
                whenExpressionFib(n-1) + whenExpressionFib(n-2)
        }

fun isNthFib(a: Long, n: Int): Boolean =
        when (a) {
            whenExpressionFib(n) -> true
            else -> false
        }

fun whenRange(x: Int): String =
    when (x) {
        in      1..10   -> "RANGE"
        !in     10..20  -> "OUT-OF-RANGE"
        else            -> "INDETERMINATE"
    }


fun whenTypes(x: Any): String =
    when (x) {
        is String -> "x is a string of length ${x.length}"
        is Int -> "x is an integer " + (if (x > 10) "larger than 10" else "")
        else -> "INDETERMINATE"
    }


fun whenIfElseChain(x: Int): String =
    when {
        x % 2 == 0 -> "even"
        x % 3 == 0 -> "divisible by three"
        else -> "some funny number"
    }


fun for1() {
    val s123 = Array(5, { i -> (i*i).toString() })

//    for (i in s123) {
//        println(i)
//    }

    for ((index, value) in s123.withIndex())
        println("Element ${index} is ${value}")
}

fun while1() {
    var x: Int = 10
    while (x > 0) {
        print(x); print(" ")
        x--
    }
}

fun while2() {
    var x: Int = 10
    do {
        print(x); print(" ")
        x--
    } while (x > 0)
}


fun breakWithLabel() {
    loop1@ for (i in 1..100) {
        for (j in i..100) {
            if (i * j > 1000) {
                break@loop1
            }
            println("$i * $j = ${i*j}")
        }
    }
}

fun retFromLambda() {
    val ints = intArrayOf(1, 2, 3, 0, 5)
    ints.forEach {
        if (it == 0)
            return // will return from forEach
        print(it); print(" ")
    }
}

fun retFromLambdaWithExplicitLabel() {
    val ints = intArrayOf(1, 2, 3, 0, 5)
    ints.forEach lit@ {
        if (it == 0)
            return@lit // will return from lambda (could also have used return@forEach)
        print(it); print(" ")
    }
}

fun retFromAnonymousFunction() {
    val ints = intArrayOf(1, 2, 3, 0, 5)
    ints.forEach(fun(v: Int) {
        if (v == 0)
            return // will return from anonymous function, not outer
        print(v); print(" ")
    })
}

fun retFromOuter() {
    val ints = intArrayOf(1, 2, 3, 0, 5)
    ints.forEach {
        if (it == 0)
            return
        print(it); print(" ")
    }
}
