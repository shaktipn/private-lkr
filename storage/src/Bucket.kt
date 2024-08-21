package com.suryadigital.leo.storage

import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Base implementation of Bucket metadata required for [StorageObject.storageBucket].
 *
 * This class streamlines the process of defining buckets in a project by generalizing validations to be performed when a [Bucket] instance is created.
 *
 * @property name [String] value of the name of the bucket.
 */
abstract class Bucket(val name: String) : KoinComponent {
    private val storageClient by inject<Storage>()

    init {
        runBlocking {
            storageClient.getBucketMetadata(this@Bucket)
        }
    }
}
