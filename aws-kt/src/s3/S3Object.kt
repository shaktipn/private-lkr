package com.suryadigital.leo.awskt.s3

import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

/**
 * Metadata of the object that needs to be uploaded to / downloaded from S3.
 *
 * @property bucketName name of the bucket for the object.
 * @property key key of the object.
 * @property fileName name by check the object is stored, or needs to be stored.
 */
data class S3Object(val bucketName: String, val key: String, val fileName: String? = null) {
    /**
     * Computed value for the `HEAD` operation that return metadata of the object without returning the object itself.
     */
    val headObjectRequest: HeadObjectRequest
        get() {
            return HeadObjectRequest.builder().bucket(bucketName).key(key).build()
        }

    /**
     * Computed value to build the request for fetching the object from S3.
     */
    val getObjectRequest: GetObjectRequest
        get() {
            return fileName?.let {
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .responseContentDisposition("attachment; filename =\"$fileName\"")
                    .build()
            }
                ?: GetObjectRequest.builder().bucket(bucketName).key(key).build()
        }

    /**
     * Computed value to build the request for putting the object in S3.
     */
    val putObjectRequest: PutObjectRequest
        get() {
            return PutObjectRequest.builder().bucket(bucketName).key(key).build()
        }
}
