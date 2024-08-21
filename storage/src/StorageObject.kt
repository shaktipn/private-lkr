package com.suryadigital.leo.storage

/**
 * Object metadata required to perform operations on that particular object.
 *
 * @property storageBucket [Bucket] on which the object need to be present, or uploaded to.
 * @property objectKey name by which the object will be stored on the storage provider.
 * @property filename name of the object file. This is not the name by which the object will be identified on the storage provider. This is generally used to define the filename in the download URL, so that the file is downloaded with the proper name.
 * @property fileSize size of the file.
 */
data class StorageObject(
    val storageBucket: Bucket,
    val objectKey: String,
    val filename: String,
    val fileSize: Long,
)
