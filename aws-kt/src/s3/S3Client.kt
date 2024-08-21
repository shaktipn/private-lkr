package com.suryadigital.leo.awskt.s3

import java.awt.Dimension
import java.net.URL
import java.time.Duration
import kotlin.jvm.Throws

/**
 * Interface defined for common S3 operations.
 */
interface S3Client {
    /**
     * Get pre-signed URL to upload a file to S3 using async AWS S3 Client.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     * @param expirationDuration duration for the generated URL to remain active.
     *
     * @return [URL] presigned PUT URL of the S3 Object.
     *
     * @throws GetPresignedUrlException if unable to retreive URL from S3Presigner.
     */
    @Throws(GetPresignedUrlException::class)
    fun getPresignedPutUrl(
        s3Object: S3Object,
        expirationDuration: Duration,
    ): URL

    /**
     * Get pre-signed URL of an object that is uploaded to private or public S3 bucket using async AWS S3 Client.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     * @param expirationDuration duration for the generated URL to remain active.
     *
     * @return [URL] presigned URL of the S3 Object which can be used to download the object.
     *
     * @throws GetPresignedUrlException if unable to retreive URL from S3Presigner.
     */
    @Throws(GetPresignedUrlException::class, ObjectNotFoundException::class)
    suspend fun getPresignedDownloadUrl(
        s3Object: S3Object,
        expirationDuration: Duration,
    ): URL

    /**
     * Uploads the object to the S3 bucket.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     * @param imageContent byteArray of the object.
     *
     * @throws ObjectUploadFailedException if unable to upload image to the S3 bucket.
     */
    @Throws(ObjectUploadFailedException::class)
    suspend fun uploadObject(
        s3Object: S3Object,
        imageContent: ByteArray,
    )

    /**
     * Get content length of S3 Object in bytes using async AWS S3 Client.
     *
     * Note: S3 bucket must exist in the same region as the calling service.
     *
     * @param s3Object [S3Object] metadata containing bucket name and object key.
     *
     * @return size of S3 Object in bytes.
     *
     * @throws ObjectNotFoundException if [s3Object] is not found.
     * @throws GetObjectSizeException if unable to retreive object metadata from S3.
     */
    @Throws(ObjectNotFoundException::class, GetObjectSizeException::class)
    suspend fun getObjectSize(s3Object: S3Object): Long

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
    suspend fun getFileContent(s3Object: S3Object): ByteArray

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
    suspend fun getImageDimensions(s3Object: S3Object): Dimension
}
