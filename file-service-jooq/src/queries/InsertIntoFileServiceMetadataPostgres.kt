package com.suryadigital.leo.fileServiceJooq.queries

import com.suryadigital.leo.basedb.DBException
import com.suryadigital.leo.fileServiceJooq.generatedCode.tables.references.METADATA
import org.jooq.DSLContext

internal class InsertIntoFileServiceMetadataPostgres : InsertIntoFileServiceMetadata() {
    override fun implementation(
        ctx: DSLContext,
        input: Input,
    ) {
        val insertedRows =
            ctx.insertInto(METADATA)
                .set(METADATA.ID, input.fileId)
                .set(METADATA.FILENAME, input.filename)
                .set(METADATA.ASSETTYPE, input.assetType)
                .set(METADATA.FILETYPE, input.fileType)
                .set(METADATA.FILESIZE, input.fileSize)
                .set(METADATA.UPLOADEDBY, input.uploadedBy)
                .execute()
        if (insertedRows != 1) {
            throw DBException("Invalid number of records we inserted: $insertedRows.")
        }
    }
}
