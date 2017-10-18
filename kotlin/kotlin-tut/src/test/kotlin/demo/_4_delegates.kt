package demo

import junit.framework.Assert.*
import org.junit.Test

/**
 * Created by parijat on 15/6/17.
 */
class User1(init: Map<String, Any?>) {
    val name: String by init
    val age:  Int by init
}

class User2(init: MutableMap<String, Any?>) {
    var name: String? by init
    var age: Int? by init
}

class _04_DelegateTests {
    @Test fun testGetNameImmutable() {
        val user = User1(mapOf("name" to "Parijat", "age" to 38))
        assertEquals("Parijat", user.name)
        assertEquals(38, user.age)
    }

    @Test fun testGetNameImmutableNull() {
        val user = User1(mapOf("name" to null, "age" to 38))
        assertNull(user.name)
    }

    @Test fun testThrowsExceptionOnUnsetProperty() {
        val user = User1(mapOf("age" to 38))
        try {
            val something = user.name
            fail("java.util.NoSuchElementException expected")
        } catch (e: java.util.NoSuchElementException) {
            //
        }
    }

    @Test fun testSetGetName() {
        val user = User2(mutableMapOf("name" to "Parijat"))
        assertEquals("Parijat", user.name)
        // assertNull(user.age) // Will throw java.util.NoSuchElementException

        user.name = "Sammy"
        user.age = 25
        assertEquals("Sammy", user.name)
        assertEquals(25, user.age ?: 0)
    }
}