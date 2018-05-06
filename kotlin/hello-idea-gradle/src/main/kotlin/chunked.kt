import kotlin.system.measureNanoTime

fun main(args: Array<String>) {
    val N = 1_000_000
    val lst2 = (1..N).toList()
    // warm up
    lst2.chunked(3).forEach {
        it.sum()
    }

    val forEachTime = measureNanoTime {
        lst2.chunked(3).forEach {
            it.sum()
        }
    }
    println("forEach: ${forEachTime/N} ns/iter")
    // forEach: 90 ns/iter

    // warm-up
    for (el in lst2.chunked(3)) {
        el.sum()
    }

    val forTime = measureNanoTime {
        for (el in lst2.chunked(3)) {
            el.sum()
        }
    }
    println("for    : ${forTime/N} ns/iter")
    // for    : 80 ns/iter
}