package com.suryadigital.leo.kedwig

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.BufferedInputStream
import java.io.InputStream

private const val BUFFER_SIZE = 8 * 1024

internal class InputStreamRequestBody(private val inputStream: InputStream, private val mediaType: MediaType? = null) :
    RequestBody() {
    override fun contentType(): MediaType? {
        return mediaType
    }

    override fun writeTo(sink: BufferedSink) {
        val bufferedInputStream: BufferedInputStream
        if (inputStream is BufferedInputStream) {
            bufferedInputStream = inputStream
        } else {
            bufferedInputStream = BufferedInputStream(inputStream)
        }
        val buffer = ByteArray(BUFFER_SIZE)
        var n: Int
        while (-1 != bufferedInputStream.read(buffer).also { n = it }) {
            sink.write(buffer, 0, n)
        }
        sink.flush()
        sink.close()
        inputStream.close()
    }
}
