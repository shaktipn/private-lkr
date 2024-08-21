package com.suryadigital.leo.fileServiceJooq.queries

import com.suryadigital.leo.basedb.NoResultQuery
import com.suryadigital.leo.basedb.QueryInput
import java.util.UUID

/**
 * Query to update `isValidated` column in file-service Metadata table.
 * throws [com.suryadigital.leo.basedb.DBException] if the number of records updated is not equal to 1.
 *
 * NOTE: This query is primarily created to be used by Eagle-Gen generated code, and should not be called directly. Users should call the RPCs that make use of these queries to perform operations.
 */
abstract class UpdateIsValidInFileServiceMetadataByFileId : NoResultQuery<UpdateIsValidInFileServiceMetadataByFileId.Input>() {
    /**
     * Input for [UpdateIsValidInFileServiceMetadataByFileId] query.
     *
     * @property fileId primary key of file-service Metadata table.
     * @property isValidated determines if the record was validated by the server or not, i.e., once the client performs an upload operation, server cross-validates if the object exists on the storage provider.
     */
    data class Input(
        val fileId: UUID,
        val isValidated: Boolean,
    ) : QueryInput
}
