package com.suryadigital.leo.awskt.s3

import com.suryadigital.leo.awskt.CONNECTION_ACQUISITION_TIMEOUT_SECONDS
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.awt.Dimension
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.URL
import java.time.Duration
import javax.imageio.ImageIO

/**
 * Implementatin for the [S3Client].
 */
class S3ClientImpl : S3Client {
    /**
     * One instance of [S3Presigner] object will be created per service when injecting [S3Client] using Koin since this is an expensive operation.
     *
     * Credentials and Region are picked up using [software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain] and [software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider] respectively.
     *
     * Credentials are configured as Task Role Policy for Fargate Tasks.
     */
    private val presigner: S3Presigner
    private val s3Client: S3AsyncClient

    init {
        s3Client = getS3AsyncClient()
        presigner = S3Presigner.create()
    }

    /**
     * Get pre-signed URL to upload a file to S3 using async AWS S3 Client.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     * @param expirationDuration duration for the generated URL to remain active.
     *
     * @return [URL] presigned PUT URL of the [s3Object].
     *
     * @throws GetPresignedUrlException if unable to retreive URL from [S3Presigner].
     */
    @Throws(GetPresignedUrlException::class)
    override fun getPresignedPutUrl(
        s3Object: S3Object,
        expirationDuration: Duration,
    ): URL {
        try {
            val putObjectPresignRequest =
                PutObjectPresignRequest
                    .builder()
                    .signatureDuration(expirationDuration)
                    .putObjectRequest(s3Object.putObjectRequest)
                    .build()
            val presignedPutObjectRequest = presigner.presignPutObject(putObjectPresignRequest)
            return presignedPutObjectRequest.url()
        } catch (e: S3Exception) {
            throw GetPresignedUrlException(e)
        }
    }

    /**
     * Get pre-signed URL of an object that is uploaded to private or public S3 bucket using async AWS S3 Client.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     * @param expirationDuration duration for the generated URL to remain active.
     *
     * @return [URL] presigned URL of the [s3Object] which can be used to download the object.
     *
     * @throws GetPresignedUrlException if unable to retreive URL from [S3Presigner].
     */
    @Throws(GetPresignedUrlException::class, ObjectNotFoundException::class)
    override suspend fun getPresignedDownloadUrl(
        s3Object: S3Object,
        expirationDuration: Duration,
    ): URL {
        try {
            if (!s3Object.doesKeyExist()) {
                throw ObjectNotFoundException()
            }
            val getObjectPresignRequest =
                GetObjectPresignRequest
                    .builder()
                    .signatureDuration(expirationDuration)
                    .getObjectRequest(s3Object.getObjectRequest)
                    .build()
            val presignedGetObjectRequest: PresignedGetObjectRequest =
                presigner.presignGetObject(getObjectPresignRequest)
            return presignedGetObjectRequest.url()
        } catch (e: S3Exception) {
            throw GetPresignedUrlException(e)
        }
    }

    private suspend fun S3Object.doesKeyExist(): Boolean {
        return try {
            s3Client.headObject(headObjectRequest).await()
            true
        } catch (e: NoSuchKeyException) {
            false
        }
    }

    /**
     * Uploads the object to the S3 bucket.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     * @param imageContent [ByteArray] of the object.
     *
     * @throws ObjectUploadFailedException if unable to upload image to the S3 bucket.
     */
    @Throws(ObjectUploadFailedException::class)
    override suspend fun uploadObject(
        s3Object: S3Object,
        imageContent: ByteArray,
    ) {
        try {
            s3Client.putObject(
                s3Object.putObjectRequest,
                AsyncRequestBody.fromBytes(imageContent),
            ).await()
        } catch (e: SdkClientException) {
            throw ObjectUploadFailedException(e)
        } catch (e: S3Exception) {
            throw ObjectUploadFailedException(e)
        }
    }

    /**
     * Get content length of S3 Object in bytes using async AWS S3 Client.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     *
     * @return size of [s3Object] in bytes.
     *
     * @throws ObjectNotFoundException if [s3Object] is not found.
     * @throws GetObjectSizeException if unable to retreive object metadata from S3.
     */
    @Throws(ObjectNotFoundException::class, GetObjectSizeException::class)
    override suspend fun getObjectSize(s3Object: S3Object): Long {
        try {
            return s3Client.headObject(s3Object.headObjectRequest).await().contentLength()
        } catch (e: NoSuchKeyException) {
            throw ObjectNotFoundException(e)
        } catch (e: S3Exception) {
            throw GetObjectSizeException(e)
        }
    }

    /**
     * Gets contents of file in S3.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     *
     * @return contents of file in S3.
     *
     * @throws ObjectNotFoundException if [s3Object] is not found.
     * @throws GetObjectException if unable to retreive object from S3.
     */
    @Throws(ObjectNotFoundException::class, GetObjectException::class)
    override suspend fun getFileContent(s3Object: S3Object): ByteArray {
        try {
            return s3Client.getObject(s3Object.getObjectRequest, AsyncResponseTransformer.toBytes()).await()
                .asByteArray()
        } catch (e: NoSuchKeyException) {
            throw ObjectNotFoundException(e)
        } catch (e: S3Exception) {
            throw GetObjectException(e)
        }
    }

    /**
     * Gets dimensions of an image stored in S3.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     *
     * @return [Dimension] of image stored in S3.
     *
     * @throws ImageProcessingException if [s3Object] cannot be processed as an image.
     * @throws ObjectNotFoundException if [s3Object] is not found.
     * @throws GetObjectException if unable to retreive object from S3.
     */
    @Throws(ObjectNotFoundException::class, GetObjectException::class, ImageProcessingException::class)
    override suspend fun getImageDimensions(s3Object: S3Object): Dimension {
        val bytes = getFileContent(s3Object)
        return getImageSizeAsync(bytes).await()
    }

    @Throws(ImageProcessingException::class)
    private suspend fun getImageSizeAsync(bytes: ByteArray): Deferred<Dimension> =
        coroutineScope {
            async(Dispatchers.IO) {
                try {
                    val bufferedImage = ImageIO.read(ByteArrayInputStream(bytes))
                    Dimension(bufferedImage.width, bufferedImage.height)
                } catch (e: IOException) {
                    throw ImageProcessingException(e)
                }
            }
        }

    private fun getS3AsyncClient(): S3AsyncClient {
        val sdkAsyncHttpClient =
            NettyNioAsyncHttpClient
                .builder()
                .connectionAcquisitionTimeout(CONNECTION_ACQUISITION_TIMEOUT_SECONDS)
                .build()
        return S3AsyncClient.builder()
            .httpClient(sdkAsyncHttpClient)
            .build()
    }
}
