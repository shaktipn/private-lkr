package com.suryadigital.leo.awskt.mock

import com.suryadigital.leo.awskt.s3.S3Client
import com.suryadigital.leo.awskt.s3.S3Object
import com.suryadigital.leo.testUtils.ResultGenerator
import java.awt.Dimension
import java.lang.IllegalStateException
import java.net.URL
import java.time.Duration

/**
 * Implementation for [S3Client] to mock its functionality.
 */
@Suppress("Unused")
class MockS3Client : S3Client {
    private var getPresignedPutURLResultGenerator: ResultGenerator<URL>? = null
    private var getPresignedDownloadURLResultGenerator: ResultGenerator<URL>? = null
    private var uploadObjectResultGenerator: ResultGenerator<Nothing>? = null
    private var getFileContentResultGenerator: ResultGenerator<ByteArray>? = null
    private var getObjectSizeResultGenerator: ResultGenerator<Long>? = null
    private var getImageDimensionsResultGenerator: ResultGenerator<Dimension>? = null

    /**
     * Set the mock functionality for [S3Client.getPresignedPutUrl].
     */
    fun setGetPresignedPutURLResultGenerator(resultGenerator: ResultGenerator<URL>) {
        synchronized(this) {
            getPresignedPutURLResultGenerator = resultGenerator
        }
    }

    /**
     * Set the mock functionality for [S3Client.getPresignedDownloadUrl].
     */
    fun setGetPresignedURLResultGenerator(resultGenerator: ResultGenerator<URL>) {
        synchronized(this) {
            getPresignedDownloadURLResultGenerator = resultGenerator
        }
    }

    /**
     * Set the mock functionality for [S3Client.uploadObject].
     */
    fun setUploadObjectResultGenerator(resultGenerator: ResultGenerator<Nothing>?) {
        synchronized(this) {
            uploadObjectResultGenerator = resultGenerator
        }
    }

    /**
     * Set the mock functionality for [S3Client.getObjectSize].
     */
    fun setGetObjectSizeResultGenerator(resultGenerator: ResultGenerator<Long>) {
        synchronized(this) {
            getObjectSizeResultGenerator = resultGenerator
        }
    }

    /**
     * Set the mock functionality for [S3Client.getFileContent].
     */
    fun setGetFileContentResultGenerator(resultGenerator: ResultGenerator<ByteArray>) {
        synchronized(this) {
            getFileContentResultGenerator = resultGenerator
        }
    }

    /**
     * Set the mock functionality for [S3Client.getImageDimensions].
     */
    fun setgetImageDimensionsResultGenerator(resultGenerator: ResultGenerator<Dimension>) {
        synchronized(this) {
            getImageDimensionsResultGenerator = resultGenerator
        }
    }

    override fun getPresignedPutUrl(
        s3Object: S3Object,
        expirationDuration: Duration,
    ): URL {
        return when (getPresignedPutURLResultGenerator) {
            is ResultGenerator.Response -> (getPresignedPutURLResultGenerator as ResultGenerator.Response<URL>).value
            is ResultGenerator.Exception -> throw (getPresignedPutURLResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("getPresignedPutURLResultGenerator is not set on MockS3Client")
        }
    }

    override suspend fun getPresignedDownloadUrl(
        s3Object: S3Object,
        expirationDuration: Duration,
    ): URL {
        return when (getPresignedDownloadURLResultGenerator) {
            is ResultGenerator.Response -> (getPresignedDownloadURLResultGenerator as ResultGenerator.Response<URL>).value
            is ResultGenerator.Exception -> throw (getPresignedDownloadURLResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("getPresignedURLResultGenerator is not set on MockS3Client")
        }
    }

    override suspend fun uploadObject(
        s3Object: S3Object,
        imageContent: ByteArray,
    ) {
        when (uploadObjectResultGenerator) {
            is ResultGenerator.Exception -> throw (uploadObjectResultGenerator as ResultGenerator.Exception<*>).value
            else -> {
                // No action needs to be performed
            }
        }
    }

    override suspend fun getObjectSize(s3Object: S3Object): Long {
        return when (getObjectSizeResultGenerator) {
            is ResultGenerator.Response -> (getObjectSizeResultGenerator as ResultGenerator.Response<Long>).value
            is ResultGenerator.Exception -> throw (getObjectSizeResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("getObjectSizeResultGenerator is not set on MockS3Client")
        }
    }

    override suspend fun getFileContent(s3Object: S3Object): ByteArray {
        return when (getFileContentResultGenerator) {
            is ResultGenerator.Response -> (getFileContentResultGenerator as ResultGenerator.Response<ByteArray>).value
            is ResultGenerator.Exception -> throw (getFileContentResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("getFileContentResultGenerator is not set on MockS3Client")
        }
    }

    override suspend fun getImageDimensions(s3Object: S3Object): Dimension {
        return when (getImageDimensionsResultGenerator) {
            is ResultGenerator.Response -> (getImageDimensionsResultGenerator as ResultGenerator.Response<Dimension>).value
            is ResultGenerator.Exception -> throw (getImageDimensionsResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("getImageDimensionsResultGenerator is not set on MockS3Client")
        }
    }
}
