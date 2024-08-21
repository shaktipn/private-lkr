package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.MalformedURLException
import java.net.URL

/**
 * Serializes a [URL] using [String] encoding and decoding.
 */
class URLSerializer : KSerializer<URL> {
    /**
     * Parses an [URL] using [String] decoding.
     */
    @Throws(LeoJSONException::class)
    override fun deserialize(decoder: Decoder): URL {
        try {
            return URL(decoder.decodeString())
        } catch (e: MalformedURLException) {
            throw LeoJSONException(e)
        } catch (e: ClassCastException) {
            throw LeoJSONException(e)
        } catch (e: IllegalArgumentException) {
            throw LeoJSONException(e)
        }
    }

    /**
     * Describes the structure of the serializable type.
     */
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.suryadigital.leo.URLSerializer", PrimitiveKind.STRING)

    /**
     * Formats an [URL] using [String] encoding.
     */
    override fun serialize(
        encoder: Encoder,
        value: URL,
    ) {
        encoder.encodeString("$value")
    }
}
