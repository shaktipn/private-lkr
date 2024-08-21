package com.suryadigital.leo.storage.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.HeadBucketRequest
import aws.sdk.kotlin.services.s3.model.HeadObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.http.request.HttpRequest
import com.suryadigital.leo.storage.Bucket
import com.suryadigital.leo.storage.Storage
import com.suryadigital.leo.storage.Storage.BucketMetadata
import com.suryadigital.leo.storage.Storage.ObjectMetadata
import com.suryadigital.leo.storage.StorageObject
import com.suryadigital.leo.storage.s3.exceptions.S3RuntimeException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URL
import kotlin.time.Duration

internal class S3StorageImpl : Storage, KoinComponent {
    private val s3Client by inject<S3Client>()

    private val StorageObject.headObjectRequest: HeadObjectRequest
        get() =
            HeadObjectRequest {
                bucket = storageBucket.name
                key = objectKey
            }

    private val StorageObject.getObjectRequest: GetObjectRequest
        get() =
            GetObjectRequest {
                bucket = storageBucket.name
                key = objectKey
                responseContentDisposition = """attachment; filename = "$filename""""
            }

    private fun StorageObject.putObjectRequest(fileSize: Long): PutObjectRequest =
        PutObjectRequest {
            bucket = storageBucket.name
            key = objectKey
            contentLength = fileSize
        }

    override suspend fun getPresignedPutURL(
        storageObject: StorageObject,
        expirationDuration: Duration,
    ): URL {
        return executeWithS3ExceptionHandling {
            s3Client.presignPutObject(
                input = storageObject.putObjectRequest(storageObject.fileSize),
                duration = expirationDuration,
            ).getURL()
        }
    }

    override suspend fun getPresignedDownloadUrl(
        storageObject: StorageObject,
        expirationDuration: Duration,
    ): URL {
        return executeWithS3ExceptionHandling {
            s3Client.presignGetObject(
                input = storageObject.getObjectRequest,
                duration = expirationDuration,
            ).getURL()
        }
    }

    override suspend fun uploadObject(
        storageObject: StorageObject,
        content: ByteArray,
    ) {
        executeWithS3ExceptionHandling {
            s3Client.putObject(
                PutObjectRequest {
                    key = storageObject.objectKey
                    bucket = storageObject.storageBucket.name
                    body = ByteStream.fromBytes(content)
                },
            )
        }
    }

    override suspend fun getObjectMetadata(storageObject: StorageObject): ObjectMetadata {
        val headResponse =
            executeWithS3ExceptionHandling {
                s3Client.headObject(storageObject.headObjectRequest)
            }
        if (headResponse.contentLength != storageObject.fileSize) {
            throw S3RuntimeException(
                message = "Object content-length ${headResponse.contentLength} does not match fileSize given by the user: ${storageObject.fileSize}.",
            )
        }
        return ObjectMetadata(
            size = headResponse.contentLength ?: throw S3RuntimeException("Content-length cannot be null for the object with objectKey ${storageObject.objectKey}."),
        )
    }

    override suspend fun getFileContent(storageObject: StorageObject): ByteStream {
        return executeWithS3ExceptionHandling {
            s3Client.getObject(storageObject.getObjectRequest) { response ->
                response.body ?: throw S3RuntimeException("Body cannot be null for the object with objectKey: ${storageObject.objectKey}.")
            }
        }
    }

    override suspend fun getBucketMetadata(bucket: Bucket): BucketMetadata {
        executeWithS3ExceptionHandling {
            s3Client.headBucket(
                input =
                    HeadBucketRequest {
                        this.bucket = bucket.name
                    },
            )
        }
        return BucketMetadata
    }

    private suspend fun <R> executeWithS3ExceptionHandling(block: suspend () -> R): R {
        return try {
            block()
        } catch (e: S3Exception) {
            throw S3RuntimeException(
                message = "Operation failed due to the following reason: ${e.message}.",
                cause = e.cause,
            )
        }
    }

    private fun HttpRequest.getURL(): URL {
        return URL("$url")
    }
}
