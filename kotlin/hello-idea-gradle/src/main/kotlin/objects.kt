/*
 * Functions returning objects without explicit superclass
 */
class C {
    // private fun: return type is object itself
    private fun foo(x: Int, y: Int) = object {
        val x: Int = x
        val y: Int = y
    }

    // public fun: return type is Any
    fun bar(x: Int, y: Int) = object {
        val x: Int = x
        val y: Int = y
    }

    fun test() {
        val o1 = foo(5, 10) // o1: object {...}
        println("o1.x = ${o1.x}; o1.y = ${o1.y}")

        val o2 = bar(5, 10) // o2: Any
        println("o2 is ${o2.javaClass}") // o2: dynamic type C$bar$1
    }
}

/*
 * Objects can access variables from enclosing scope
 */
class D(val prop: Int) {
    fun foo(arg1: Int): Int {
        val local = 6

        val o = object {
            val local = 8

            fun doSomething(arg2: Int): Int {
                return this@D.prop + arg1 + local + this.local + arg2
            }
        }

        return o.doSomething(7)
    }
}


/*
 * Object declarations
 */
object Singleton {
    fun foo(): Int = 10
}

fun doSomething(): Int = Singleton.foo()

/*
 * Companions
 */
interface Factory<T> {
    fun create(): T
}

class MyClass {
    companion object : Factory<MyClass> {
        override fun create(): MyClass = MyClass()
    }
}

fun main(args: Array<String>) {
    C().test()
    println(D(4).foo(5)) // Should print: 4 + 5 + 6 + 8 + 7 == "30"
}