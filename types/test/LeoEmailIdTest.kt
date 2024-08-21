package com.suryadigital.leo.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LeoEmailIdTest {
    @Test
    fun validEmailId1() {
        val leoEmailId = LeoEmailId("FOO@bar.com")
        assertEquals("foo@bar.com", leoEmailId.value)
    }

    @Test
    fun validEmailId2() {
        val leoEmailId = LeoEmailId("FOO@bar.co.in")
        assertEquals("foo@bar.co.in", leoEmailId.value)
    }

    @Test
    fun validEmailId3() {
        val leoEmailId = LeoEmailId("FOO.bar@surya-soft.com")
        assertEquals("foo.bar@surya-soft.com", leoEmailId.value)
    }

    @Test
    fun validEmailId4() {
        val leoEmailId = LeoEmailId("FOO%^&*@bar.com")
        assertEquals("foo%^&*@bar.com", leoEmailId.value)
    }

    @Test
    fun invalidEmailId1() {
        assertFailsWith(LeoInvalidLeoEmailIdException::class) {
            LeoEmailId("foo")
        }
    }

    @Test
    fun invalidEmailId2() {
        assertFailsWith(LeoInvalidLeoEmailIdException::class) {
            LeoEmailId(".FOO@bar.com")
        }
    }

    @Test
    fun invalidEmailId3() {
        assertFailsWith(LeoInvalidLeoEmailIdException::class) {
            LeoEmailId("FOO@bar.com.")
        }
    }

    @Test
    fun invalidEmailId4() {
        assertFailsWith(LeoInvalidLeoEmailIdException::class) {
            LeoEmailId("FOO#bar.com")
        }
    }

    @Test
    fun invalidEmailId5() {
        assertFailsWith(LeoInvalidLeoEmailIdException::class) {
            LeoEmailId("FOO@bar..com")
        }
    }

    @Test
    fun invalidEmailId6() {
        assertFailsWith(LeoInvalidLeoEmailIdException::class) {
            LeoEmailId("FOO@bar")
        }
    }

    @Test
    fun invalidEmailId7() {
        assertFailsWith(LeoInvalidLeoEmailIdException::class) {
            LeoEmailId("FOO@.com")
        }
    }
}
