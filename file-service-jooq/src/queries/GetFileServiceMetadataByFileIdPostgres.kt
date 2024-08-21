package com.suryadigital.leo.fileServiceJooq.queries

import com.suryadigital.leo.basedb.fetchOneOrNone
import com.suryadigital.leo.basedb.getNonNullValue
import com.suryadigital.leo.fileServiceJooq.generatedCode.tables.references.METADATA
import org.jooq.DSLContext

internal class GetFileServiceMetadataByFileIdPostgres : GetFileServiceMetadataByFileId() {
    override fun implementation(
        ctx: DSLContext,
        input: Input,
    ): Result? {
        return ctx.select(
            METADATA.UPLOADEDBY,
            METADATA.ASSETTYPE,
            METADATA.FILENAME,
            METADATA.FILESIZE,
            METADATA.ISVALIDATED,
        ).from(METADATA)
            .where(METADATA.ID.eq(input.fileId))
            .fetchOneOrNone()
            ?.map {
                Result(
                    uploadedBy = it.get(METADATA.UPLOADEDBY),
                    assetType = it.getNonNullValue(METADATA.ASSETTYPE),
                    filename = it.getNonNullValue(METADATA.FILENAME),
                    fileSize = it.getNonNullValue(METADATA.FILESIZE),
                    isValidated = it.getNonNullValue(METADATA.ISVALIDATED),
                )
            }
    }
}
