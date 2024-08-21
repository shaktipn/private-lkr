package com.suryadigital.leo.fileServiceJooq

import com.suryadigital.leo.fileServiceJooq.queries.GetFileServiceMetadataByFileId
import com.suryadigital.leo.fileServiceJooq.queries.GetFileServiceMetadataByFileIdPostgres
import com.suryadigital.leo.fileServiceJooq.queries.InsertIntoFileServiceMetadata
import com.suryadigital.leo.fileServiceJooq.queries.InsertIntoFileServiceMetadataPostgres
import com.suryadigital.leo.fileServiceJooq.queries.UpdateIsValidInFileServiceMetadataByFileId
import com.suryadigital.leo.fileServiceJooq.queries.UpdateIsValidInFileServiceMetadataByFileIdPostgres
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module definition for Postgres implementation for file-service queries.
 *
 * NOTE: This is primarily created to be used by Eagle-Gen generated code, and should not be called directly. This will already be included in Eagle-Gen generated module definition for file-service.
 */
@Suppress("unused")
val fileServicePostgresModules: Module =
    module {
        single<GetFileServiceMetadataByFileId> { GetFileServiceMetadataByFileIdPostgres() }
        single<InsertIntoFileServiceMetadata> { InsertIntoFileServiceMetadataPostgres() }
        single<UpdateIsValidInFileServiceMetadataByFileId> { UpdateIsValidInFileServiceMetadataByFileIdPostgres() }
    }
