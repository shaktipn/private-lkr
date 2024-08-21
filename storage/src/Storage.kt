package com.suryadigital.leo.storage

import aws.smithy.kotlin.runtime.content.ByteStream
import org.koin.core.component.KoinComponent
import java.net.URL
import kotlin.time.Duration

/**
 * Defines the common storage API functionality provided by the runtime for different cloud storage providers.
 */
interface Storage : KoinComponent {
    /**
     * Get the presigned upload URL for the given [StorageObject].
     * A presigned upload URL is a temporary URL that can be used to upload an object to the storage provider.
     *
     * @param storageObject object metadata based on which the URL needs to be generated.
     * @param expirationDuration [Duration] in which the URL will expire, i.e., once the URL is expired, upload operation using that URL will not be permitted.
     */
    suspend fun getPresignedPutURL(
        storageObject: StorageObject,
        expirationDuration: Duration,
    ): URL

    /**
     * Get the presigned download URL for the given [StorageObject].
     * A presigned download URL is a temporary URL that can be used to download an object from the storage provider.
     *
     * @param storageObject object metadata based on which the URL needs to be generated.
     * @param expirationDuration [Duration] in which the URL will expire, i.e., once the URL is expired, download operation using that URL will not be permitted.
     */
    suspend fun getPresignedDownloadUrl(
        storageObject: StorageObject,
        expirationDuration: Duration,
    ): URL

    /**
     * Upload the [content] for the given [StorageObject].
     *
     * @param storageObject object metadata based on which upload request will be generated.
     * @param content content in [ByteArray] format which needs to be uploaded.
     */
    suspend fun uploadObject(
        storageObject: StorageObject,
        content: ByteArray,
    )

    /**
     * Get metadata for the object present in the storage provider.
     *
     * @param storageObject object attributes based on which metadata will be fetched from the storage provider.
     */
    suspend fun getObjectMetadata(storageObject: StorageObject): ObjectMetadata

    /**
     * Get metadata for the bucket present in the storage provider.
     *
     * @param bucket bucket metadata based on which metadata will be fetched from the storage provider.
     */
    suspend fun getBucketMetadata(bucket: Bucket): BucketMetadata

    /**
     * Fetches the content of a file stored in the storage provider.
     *
     * @param storageObject object metadata based on which the file content will be fetched from the storage provider in form of a [ByteStream].
     */
    suspend fun getFileContent(storageObject: StorageObject): ByteStream

    /**
     * Defines the bucket metadata for the bucket present in the storage provider.
     */
    data object BucketMetadata

    /**
     * Defines the object metadata for the object present in the storage provider.
     *
     * @property size size of the object.
     */
    data class ObjectMetadata(
        val size: Long,
    )
}
