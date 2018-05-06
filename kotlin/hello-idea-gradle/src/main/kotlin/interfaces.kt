interface ISomeInterface {
    fun foo() = "Default implementation"
    fun bar()
}

class SomeInterfaceImpl: ISomeInterface {
    override fun bar() {
        TODO("Not implemented")
    }
}

interface IWithProp {
    val prop: Int // abstract
    val propWithImpl: String
        get() = "foo"
    fun foo() {
        print(prop)
    }
}

class WithPropImpl(override val prop: Int) : IWithProp

fun test() {
    val withPropImpl = WithPropImpl(1)
    withPropImpl.foo()
    println()
}

fun main(args: Array<String>) {
    val c = SomeInterfaceImpl()
    println(c.foo()) // should print: "Default implementation"
    test() // Should print: "1"
}

