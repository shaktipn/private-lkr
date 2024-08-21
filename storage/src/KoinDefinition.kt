package com.suryadigital.leo.storage

import aws.sdk.kotlin.services.s3.S3Client
import com.suryadigital.leo.storage.s3.S3StorageImpl
import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module definition for S3 implementations.
 * To use the S3 functionality in a project, this definition needs to be added to the project's Koin module definitions.
 */
@Suppress("unused")
val storageS3ModuleDefinition: Module =
    module {
        single<S3Client> { runBlocking { S3Client.fromEnvironment() } }
        single<Storage> { S3StorageImpl() }
    }
