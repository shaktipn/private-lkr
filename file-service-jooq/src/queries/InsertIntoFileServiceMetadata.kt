package com.suryadigital.leo.fileServiceJooq.queries

import com.suryadigital.leo.basedb.NoResultQuery
import com.suryadigital.leo.basedb.QueryInput
import java.util.UUID

/**
 * Query to insert file-service metadata.
 * Throws [com.suryadigital.leo.basedb.DBException] if number of records inserted is not equal to 1.
 *
 * NOTE: This query is primarily created to be used by Eagle-Gen generated code, and should not be called directly. Users should call the RPCs that make use of these queries to perform operations.
 */
abstract class InsertIntoFileServiceMetadata : NoResultQuery<InsertIntoFileServiceMetadata.Input>() {
    /**
     * Input for [InsertIntoFileServiceMetadata] query.
     *
     * @property fileId primary key of file-service Metadata table.
     * @property uploadedBy userId of the user who performed the upload operation for the file with fileId [fileId]. In the case of system uploads, this will be null.
     * @property filename name of the file.
     * @property assetType [String] representation of [Enum] value that is defined on the project level, indicating the type of asset the file is associated with.
     * @property fileType type of file which is uploaded.
     * @property fileSize size of the file in bytes.
     */
    data class Input(
        val fileId: UUID,
        val uploadedBy: UUID?,
        val filename: String,
        val assetType: String,
        val fileType: String,
        val fileSize: Long,
    ) : QueryInput
}
