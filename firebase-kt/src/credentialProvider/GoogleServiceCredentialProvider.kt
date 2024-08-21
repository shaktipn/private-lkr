package com.suryadigital.leo.firebasekt.credentialProvider

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.suryadigital.leo.firebasekt.PushNotificationImpl
import com.suryadigital.leo.inlineLogger.getInlineLogger
import com.suryadigital.leo.kedwig.AsyncAPIClient
import com.suryadigital.leo.kedwig.Method
import com.suryadigital.leo.kedwig.Response
import com.suryadigital.leo.kedwig.request
import com.suryadigital.leo.kotlinxserializationjson.getLong
import com.suryadigital.leo.kotlinxserializationjson.getString
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.security.KeyFactory
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.Date
import kotlin.jvm.Throws

internal class GoogleServiceCredentialProvider(private val googleAPIClient: AsyncAPIClient) : KoinComponent {
    private val logger = getInlineLogger(PushNotificationImpl::class)
    private val grantType = "urn:ietf:params:oauth:grant-type:jwt-bearer".encodeToUTF8()
    private val keyFactory = KeyFactory.getInstance("RSA")
    private val certFactory = CertificateFactory.getInstance("X.509")
    private val jsonParser by inject<Json>()

    @Throws(CredentialProviderException::class)
    internal suspend fun getAuthToken(
        credentialsFilePath: String,
        scope: String,
    ): AuthToken {
        val credentials = readCredentialsFromFile(credentialsFilePath)
        val publicKey = getPublicKey(credentials)
        val jwt = getJwt(credentials, publicKey, scope)
        val authResponse =
            googleAPIClient.sendRequestAsync(
                request {
                    path = AUTH_TOKEN_REQUEST_PATH
                    method = Method.POST
                    omitDefaultHeaders = true
                    headers {
                        header("Content-Type", "application/x-www-form-urlencoded")
                    }
                    body("grant_type=$grantType&assertion=$jwt")
                },
            )
        if (authResponse.statusCode != 200) {
            logger.debug { "Unable acquire auth token from ${credentials.publicKeyUrl}. Status Code : ${authResponse.statusCode}, Body : ${authResponse.stringBody}" }
            throw CredentialProviderException(
                "Unable acquire auth token from ${credentials.publicKeyUrl}. Status Code : ${authResponse.statusCode}",
            )
        }
        return parseAuthToken(authResponse)
    }

    private fun getJwt(
        credentials: Credentials,
        publicKey: RSAPublicKey,
        scope: String,
    ): String {
        val issuedAt = Date()
        val expiresAt: Date = Date.from(issuedAt.toInstant().plus(55, ChronoUnit.MINUTES))
        return try {
            JWT.create()
                .withHeader(mapOf("alg" to "RS256", "typ" to "JWT"))
                .withClaim("iss", credentials.clientEmail)
                .withClaim("scope", scope)
                .withAudience("https://www.googleapis.com/oauth2/v4/token")
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.RSA256(publicKey, credentials.privateKey))
                .encodeToUTF8()
        } catch (e: JWTCreationException) {
            throw CredentialProviderException("Unable to create JWT from provided credentials", e)
        }
    }

    private suspend fun getPublicKey(credentials: Credentials): RSAPublicKey {
        val publicKeyResponse =
            googleAPIClient.sendRequestAsync(
                request {
                    path = URI(credentials.publicKeyUrl).path
                    method = Method.GET
                    omitDefaultHeaders = true
                },
            )
        if (publicKeyResponse.statusCode != 200) {
            logger.debug { "Cannot acquire public key from public key URL. Status Code : ${publicKeyResponse.statusCode}, Body : ${publicKeyResponse.stringBody}" }
            throw CredentialProviderException(
                "Cannot acquire public key from public key URL. Status Code : ${publicKeyResponse.statusCode}",
            )
        }
        return try {
            parsePublicKey(publicKeyResponse.stringBody, credentials.privateKeyId)
        } catch (e: SerializationException) {
            throw CredentialProviderException("Unable to parse response body from public key URL", e)
        } catch (e: CertificateException) {
            throw CredentialProviderException("Unable to parse public key", e)
        }
    }

    @Throws(SerializationException::class, CertificateException::class)
    private fun parsePublicKey(
        publicKeyResponse: String,
        privateKeyId: String,
    ): RSAPublicKey {
        val certificate: X509Certificate =
            certFactory.generateCertificate(
                ByteArrayInputStream(
                    Base64.getDecoder().decode(
                        parseJson(publicKeyResponse)
                            .getString(privateKeyId)
                            .replace("\n", "")
                            .replace("-----BEGIN CERTIFICATE-----", "")
                            .replace("-----END CERTIFICATE-----", ""),
                    ),
                ),
            ) as X509Certificate
        return certificate.publicKey as RSAPublicKey
    }

    private fun parseAuthToken(authTokenResponse: Response): AuthToken {
        return try {
            val authTokenResponseJson = parseJson(authTokenResponse.stringBody)
            val responseTokenType = authTokenResponseJson.getString("token_type")
            if (responseTokenType.lowercase() != TOKEN_TYPE) {
                throw CredentialProviderException(
                    "Invalid token type : $responseTokenType",
                )
            }
            AuthToken(
                token = authTokenResponseJson.getString("access_token"),
                expiresAt = Instant.now().plusSeconds(authTokenResponseJson.getLong("expires_in")),
            )
        } catch (e: SerializationException) {
            throw CredentialProviderException("Unable to parse auth token response", e)
        }
    }

    private fun readCredentialsFromFile(credentialsFilePath: String): Credentials {
        return try {
            val credentials = File(credentialsFilePath).readText()
            val credentialsJson = parseJson(credentials)
            Credentials(
                clientEmail = credentialsJson.getString("client_email"),
                privateKeyId = credentialsJson.getString("private_key_id"),
                privateKey = getPrivateKey(credentialsJson.getString("private_key")),
                publicKeyUrl = credentialsJson.getString("client_x509_cert_url"),
            )
        } catch (e: IOException) {
            throw CredentialProviderException("Unable to read Credential file $credentialsFilePath")
        } catch (e: SerializationException) {
            throw CredentialProviderException("Unable to parse credentials file", e)
        } catch (e: InvalidKeySpecException) {
            throw CredentialProviderException("Unable to parse private key", e)
        }
    }

    @Throws(InvalidKeySpecException::class)
    private fun getPrivateKey(privateKeyContent: String): RSAPrivateKey {
        val keySpecPKCS8 =
            PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(
                    privateKeyContent
                        .replace("\n", "")
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", ""),
                ),
            )
        return keyFactory.generatePrivate(keySpecPKCS8) as RSAPrivateKey
    }

    private fun parseJson(string: String): JsonObject = jsonParser.parseToJsonElement(string).jsonObject
}

internal data class AuthToken(
    val token: String,
    val expiresAt: Instant,
)

private data class Credentials(
    val clientEmail: String,
    val privateKeyId: String,
    val privateKey: RSAPrivateKey,
    val publicKeyUrl: String,
)

private fun String.encodeToUTF8() = URLEncoder.encode(this, "UTF-8")

private const val AUTH_TOKEN_REQUEST_PATH = "/oauth2/v4/token"
private const val TOKEN_TYPE = "bearer"
