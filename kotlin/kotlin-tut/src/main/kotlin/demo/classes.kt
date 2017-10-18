package demo

import java.util.logging.Logger

/**
 * Created by parijat on 14/6/17.
 */

class Empty

val logger: Logger = java.util.logging.Logger.getAnonymousLogger()!!

// primary constructor
// initializer block
// properties
class Person1(name: String) {
    val name: String = name
    val personKey: String = name.toUpperCase()

    init {
        logger.info("Person(key=${personKey}")
    }
}

// primary constructor with property
class Person2(val name: String) {
    // derived property
    val key: String = name.toUpperCase()
}

// explicit 'constructor' keyword for visibility modifiers and annotations
class Person3 private @Deprecated("blah") constructor(val name: String) {
    companion object {
        fun create(firstName: String, lastName: String = "") {
            Person3(firstName + " " + lastName)
        }
    }
}

// making the default constructor non-public
// secondary constructors
class Person4 private constructor(val name: String) {
    constructor(firstName: String, lastName: String) : this(firstName + " " + lastName)
}

fun consTest() {
    val person1 = Person1("Sarah")
    println(person1)
    val person2 = Person2("Jeremy")
    println(person2)
    // val person3 = Person3("Mike")
    val person3 = Person3.create("Mike")
    println(person3)
    val person4 = Person4("Ford", "Prefect")
    println(person4)
}


//
// Properties
//

class Customer1(var name: String)
class Customer2 {
    var name: String = ""
        set(value) {
            field = if (value.length > 20)
                value.substring(0, 20)
            else
                value
        }

    var someOtherProp: String = "default value"
        private set // change the visibility of the setter
}


fun propTest() {
    val customer1 = Customer1("Parijat")
    println("customer1.name = ${customer1.name}")
    customer1.name = "Parijat Mishra"
    println("customer1.name = ${customer1.name}")

    val customer2 = Customer2()
    customer2.name = "A very long sentence with more than 20 chars"
    println("customer2.name = ${customer2.name} (${customer2.name.length} chars)")
}


//
// Methods, inheritance and overriding
//

open class A {
    open val av = 1
    fun a() {
        println("A.a()")
    }
    open fun f() {
        println("A.f()")
    }
}

interface B {
    val bv: Int

    fun c() {
        println("B.c()")
    }
    fun f() {
        println("B.f()")
    }
}

class C : A(), B {
    override val av = 3
    override var bv = 5

    override fun f() {
        super<A>.f()
        super<B>.f()
    }
}

// `lateinit`
class PersonTest {
    lateinit var subject: Person1

    fun setup() {
        subject = Person1("some person")
    }

    fun test() {
        assert(subject.personKey == "SOME PERSON")
    }
}

fun lateinit() {
    val personTest = PersonTest()
    personTest.setup()
    personTest.test()
}