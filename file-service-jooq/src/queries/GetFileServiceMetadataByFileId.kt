package com.suryadigital.leo.fileServiceJooq.queries

import com.suryadigital.leo.basedb.QueryInput
import com.suryadigital.leo.basedb.QueryResult
import com.suryadigital.leo.basedb.SingleResultOrNullQuery
import java.util.UUID

/**
 * Query to fetch file-service metadata record by [Input.fileId].
 * Returns null if no record is found.
 *
 * NOTE: This query is primarily created to be used by Eagle-Gen generated code, and should not be called directly. Users should call the RPCs that make use of these queries to perform operations.
 */
abstract class GetFileServiceMetadataByFileId :
    SingleResultOrNullQuery<GetFileServiceMetadataByFileId.Input, GetFileServiceMetadataByFileId.Result>() {
    /**
     * Input for [GetFileServiceMetadataByFileId] query.
     *
     * @property fileId primary key of file-service Metadata table.
     */
    data class Input(
        val fileId: UUID,
    ) : QueryInput

    /**
     * Result for [GetFileServiceMetadataByFileId] query if record is found.
     *
     * @property uploadedBy userId of the user who performed the upload operation for the file with fileId [Input.fileId]. In the case of system uploads, this will be null.
     * @property assetType [String] representation of [Enum] value that is defined on the project level, indicating the type of asset the file is associated with.
     * @property filename name of the file.
     * @property fileSize size of the file in bytes.
     * @property isValidated determines if the record was validated by the server or not, i.e., once the client performs an upload operation, server cross-validates if the object exists on the storage provider.
     */
    data class Result(
        val uploadedBy: UUID?,
        val assetType: String,
        val filename: String,
        val fileSize: Long,
        val isValidated: Boolean,
    ) : QueryResult
}
