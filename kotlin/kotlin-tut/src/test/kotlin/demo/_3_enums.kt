package demo

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by parijat on 15/6/17.
 */
enum class RGB { RED, GREEN, BLUE }

enum class ProtocolState {
    WAITING {
        override fun nextState(): ProtocolState = TALKING
    },
    TALKING {
        override fun nextState(): ProtocolState = WAITING
    };

    abstract fun nextState(): ProtocolState
}

inline fun <reified T : Enum<T>> enumValuesToString(): String = enumValues<T>().joinToString(",")


class _03_EnumsTest {
    @Test fun enumValueOf() {
        assertEquals(RGB.RED, RGB.valueOf("RED"))
    }

    @Test fun enumValueOfThrows() {
        try {
            RGB.valueOf("FOO")
            fail("Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            //
        }
    }

    @Test fun genericValues() {
        assertEquals("RED,GREEN,BLUE", enumValuesToString<RGB>())
    }

    @Test fun genericValueOf() {
        assertEquals(RGB.RED, enumValueOf<RGB>("RED"))
    }

    @Test fun enumMember() {
        val state = ProtocolState.WAITING;
        val next = state.nextState();
        assertEquals(ProtocolState.TALKING, next);
    }
}