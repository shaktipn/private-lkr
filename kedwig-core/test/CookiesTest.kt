package com.suryadigital.leo.kedwig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CookiesTest {
    companion object {
        val cookieList: Cookies =
            Cookies(
                listOf(
                    Cookie(
                        name = "Cookie1",
                        value = "Value1",
                        domain = null,
                        path = null,
                        expiresAt = null,
                        httpOnly = true,
                        secure = true,
                    ),
                    Cookie(
                        name = "Cookie2",
                        value = "Value2",
                        domain = null,
                        path = null,
                        expiresAt = null,
                        httpOnly = true,
                        secure = true,
                    ),
                    Cookie(
                        name = "Cookie1",
                        value = "Value3",
                        domain = null,
                        path = null,
                        expiresAt = null,
                        httpOnly = true,
                        secure = true,
                    ),
                ),
            )
    }

    @Test
    fun testGetFirstReturningValue() {
        val result = cookieList.getFirst("Cookie1")
        assertNotNull(result)
        assertEquals("Cookie1", result.name)
        assertEquals("Value1", result.value)
    }

    @Test
    fun testGetFirstReturningNull() {
        val result = cookieList.getFirst("Cookie")
        assertNull(result)
    }

    @Test
    fun testGetFirstValueReturningValue() {
        val result = cookieList.getFirstValue("Cookie1")
        assertNotNull(result)
        assertEquals("Value1", result)
    }

    @Test
    fun testGetFirstValueReturningNull() {
        val result = cookieList.getFirstValue("Cookie")
        assertNull(result)
    }

    @Test
    fun testGetWithSingleValue() {
        val result = cookieList["Cookie2"]
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Cookie2", result[0].name)
        assertEquals("Value2", result[0].value)
    }

    @Test
    fun testGetWithMultipleValues() {
        val result = cookieList["Cookie1"]
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("Cookie1", result[0].name)
        assertEquals("Value1", result[0].value)
        assertEquals("Cookie1", result[1].name)
        assertEquals("Value3", result[1].value)
    }

    @Test
    fun testGetWithNoMatch() {
        val result = cookieList["Cookie"]
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun testGetValuesWithSingleValue() {
        val result = cookieList.getValues("Cookie2")
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Value2", result[0])
    }

    @Test
    fun testGetValuesWithMultipleValues() {
        val result = cookieList.getValues("Cookie1")
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("Value1", result[0])
        assertEquals("Value3", result[1])
    }

    @Test
    fun testGetValuesWithNoMatch() {
        val result = cookieList.getValues("Cookie")
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun testGetAll() {
        assertEquals(cookieList.toList(), cookieList.getAll())
    }

    @Test
    fun testGetAllValues() {
        assertEquals(cookieList.map(Cookie::value), cookieList.getAllValues())
    }

    @Test
    fun testGetAllNames() {
        assertEquals(cookieList.map(Cookie::name), cookieList.getAllNames())
    }

    @Test
    fun requestCookieTest() {
        val request =
            request {
                method = Method.GET
                path = ""
                cookies { cookie(name = "Cookie1", value = "Value1") }
            }
        assertEquals("Cookie1=Value1", request.headers.getFirstValue("Cookie"))
    }

    @Test
    fun requestCookieTestUsingTo() {
        val request =
            request {
                method = Method.GET
                path = ""
                cookies { "Cookie1" to "Value1" }
            }
        assertEquals("Cookie1=Value1", request.headers.getFirstValue("Cookie"))
    }

    @Test
    fun testCookieBuilderEquality() {
        val cookiesBuilder1 = CookiesBuilder(arrayListOf(Header("Cookie1", "Value1")))
        val cookiesBuilder2 = CookiesBuilder(arrayListOf(Header("Cookie1", "Value1")))
        val cookiesBuilder3 = CookiesBuilder(arrayListOf(Header("Cookie1", "Value2")))
        val cookiesBuilder4 = CookiesBuilder(arrayListOf(Header("Cookie1", "Value2"), Header("Cookie2", "Value2")))
        assertEquals(cookiesBuilder1, cookiesBuilder2)
        assertEquals(cookiesBuilder1, cookiesBuilder1)
        assertNotEquals(cookiesBuilder1, cookiesBuilder3)
        assertNotEquals(cookiesBuilder1, cookiesBuilder4)
        assertEquals(cookiesBuilder1.hashCode(), cookiesBuilder2.hashCode())
    }
}
