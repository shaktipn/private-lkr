package com.suryadigital.leo.ktor

import io.ktor.http.CookieEncoding
import io.ktor.http.encodeURLQueryComponent
import io.ktor.http.toHttpDate
import io.ktor.server.response.ApplicationResponse
import io.ktor.util.date.GMTDate

/**
 * This function sets a cookie on a response.
 *
 * Although Ktor has this functionality built in, it requires that the secure property of a cookie can only be set on a TLS connection.
 *
 * However, since we typically run behind a load balancer at which TLS terminates, the connection coming to Ktor is not a TLS connection.
 *
 * Therefore, for now we've forked this function to remove that check.
 *
 * @param name name of the cookie that to be added.
 * @param value value of the cookie that to be added.
 * @param encoding cookie encoding type.
 * @param maxAge indicates the number of seconds until the cookie expires.
 * @param expires indicates the maximum lifetime of the cookie.
 * @param domain defines the host to which the cookie will be sent.
 * @param path indicates the path that must exist in the requested URL for the browser to send the Cookie header.
 * @param secure indicates that the cookie is sent to the server only when a request is made with secure connection.
 * @param httpOnly forbids JavaScript from accessing the cookie.
 * @param extensions additional cookie extensions.
 * @param includeEncoding add an encoding type in the cookie string.
 */
fun ApplicationResponse.setCookie(
    name: String,
    value: String,
    encoding: CookieEncoding = CookieEncoding.URI_ENCODING,
    maxAge: Int = 0,
    expires: GMTDate? = null,
    domain: String? = null,
    path: String? = null,
    secure: Boolean = false,
    httpOnly: Boolean = false,
    extensions: Map<String, String?> = emptyMap(),
    includeEncoding: Boolean = false,
) {
    headers.append(
        "Set-Cookie",
        renderSetCookieHeader(
            name = name,
            value = value,
            encoding = encoding,
            maxAge = maxAge,
            expires = expires,
            domain = domain,
            path = path,
            secure = secure,
            httpOnly = httpOnly,
            extensions = extensions,
            includeEncoding = includeEncoding,
        ),
    )
}

/**
 * This is a copied version of https://github.com/ktorio/ktor/blob/main/ktor-http/common/src/io/ktor/http/Cookie.kt#L139,
 * added to avoid using `@KtorExperimentalAPI` annotation.
 */
private fun renderSetCookieHeader(
    name: String,
    value: String,
    encoding: CookieEncoding = CookieEncoding.URI_ENCODING,
    maxAge: Int = 0,
    expires: GMTDate? = null,
    domain: String? = null,
    path: String? = null,
    secure: Boolean = false,
    httpOnly: Boolean = false,
    extensions: Map<String, String?> = emptyMap(),
    includeEncoding: Boolean = true,
): String =
    (
        listOf(
            cookiePart(name.assertCookieName(), value, encoding),
            cookiePartUnencoded("Max-Age", if (maxAge > 0) maxAge else null),
            cookiePart("Expires", expires?.toHttpDate(), CookieEncoding.RAW),
            cookiePart("Domain", domain, CookieEncoding.RAW),
            cookiePart("Path", path, CookieEncoding.RAW),
            cookiePartFlag("Secure", secure),
            cookiePartFlag("HttpOnly", httpOnly),
        ) + extensions.map { cookiePartExt(it.key.assertCookieName(), it.value, encoding) } +
            if (includeEncoding) cookiePartExt("\$x-enc", encoding.name, CookieEncoding.RAW) else ""
    ).filter(String::isNotEmpty)
        .joinToString("; ")

private fun cookiePart(
    name: String,
    value: Any?,
    encoding: CookieEncoding,
) = if (value != null) "$name=${encodeCookieValue("$value", encoding)}" else ""

private fun cookiePartUnencoded(
    name: String,
    value: Any?,
) = if (value != null) "$name=$value" else ""

private fun cookiePartFlag(
    name: String,
    value: Boolean,
) = if (value) name else ""

private fun cookiePartExt(
    name: String,
    value: String?,
    encoding: CookieEncoding,
) = if (value == null) cookiePartFlag(name, true) else cookiePart(name, value, encoding)

private val cookieCharsShouldBeEscaped = setOf(';', ',', '"')

private fun Char.shouldEscapeInCookies() = isWhitespace() || this < ' ' || this in cookieCharsShouldBeEscaped

private fun String.assertCookieName() =
    when {
        any(Char::shouldEscapeInCookies) -> throw IllegalArgumentException("Cookie name is not valid: $this")
        else -> this
    }

private fun encodeCookieValue(
    value: String,
    encoding: CookieEncoding,
): String =
    when (encoding) {
        CookieEncoding.RAW ->
            when {
                value.any(Char::shouldEscapeInCookies) ->
                    throw IllegalArgumentException(
                        "The cookie value contains characters that couldn't be encoded in RAW format. " +
                            " Consider URL_ENCODING mode",
                    )
                else -> value
            }
        CookieEncoding.DQUOTES ->
            when {
                value.contains('"') -> throw IllegalArgumentException(
                    "The cookie value contains characters that couldn't be encoded in DQUOTES format. " +
                        "Consider URL_ENCODING mode",
                )
                value.any(Char::shouldEscapeInCookies) -> "\"$value\""
                else -> value
            }
        CookieEncoding.BASE64_ENCODING -> value
        CookieEncoding.URI_ENCODING -> value.encodeURLQueryComponent(encodeFull = true, spaceToPlus = true)
    }
