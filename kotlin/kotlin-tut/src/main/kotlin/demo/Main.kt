package demo

/**
 * Created by parijat on 3/6/17.
 */
fun main(args: Array<String>) {
    val name = if (args.size > 1) args[1] else "World"
    println("Hello, $name!")
}