package com.suryadigital.leo.ktor

import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

/**
 * This function adds the support for [cross-origin requests](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS).
 *
 * Example:
 * ```
 * installCors(allowedHosts, listOf(HttpMethod.Options, HttpMethod.Delete),listOf(HttpHeaders.ContentType),true, true)
 * ```
 *
 * @param hosts the hosts that can make cross-origin requests.
 * @param httpMethod the additional method that needs to be added. By default, the CORS plugin allows `GET`, `POST`, and `HEAD`.
 * @param httpHeader the additional header that needs to be added. By default, the CORS plugin allows `Accept`, `Accept-Language` and `Content-Language`.
 * @param allowCredentials this should be set to true to allow passing information like cookie or authentication information.
 * @param allowNonSimpleContentTypes allow sending requests with non-simple content-types. The types `text/plain`, `application/x-www-form-urlencoded` and `multipart/form-data` are considered simple.
 */
fun Application.installCORS(
    hosts: List<String>,
    httpMethod: List<HttpMethod> = listOf(HttpMethod.Options),
    httpHeader: List<String> = listOf(),
    allowCredentials: Boolean = true,
    allowNonSimpleContentTypes: Boolean = true,
) {
    install(CORS) {
        hosts.forEach {
            allowHost(it, schemes = listOf("https"))
        }
        httpMethod.forEach(::allowMethod)
        httpHeader.forEach(::allowHeader)
        this.allowCredentials = allowCredentials
        this.allowNonSimpleContentTypes = allowNonSimpleContentTypes
    }
}
