
fun main(args: Array<String>) {
    val lst = listOf("one", "two", "three", "four")

    println(lst.associateBy { it.length })
    // {3=two, 5=three, 4=four}

    println(lst.associateBy(keySelector = {it.length}, valueTransform = {it.toUpperCase()}))
    // {3=TWO, 5=THREE, 4=FOUR}
}
