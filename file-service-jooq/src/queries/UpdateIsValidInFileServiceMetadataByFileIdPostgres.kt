package com.suryadigital.leo.fileServiceJooq.queries

import com.suryadigital.leo.basedb.DBException
import com.suryadigital.leo.fileServiceJooq.generatedCode.tables.references.METADATA
import org.jooq.DSLContext
import java.time.Instant

internal class UpdateIsValidInFileServiceMetadataByFileIdPostgres : UpdateIsValidInFileServiceMetadataByFileId() {
    override fun implementation(
        ctx: DSLContext,
        input: Input,
    ) {
        val updatedRows =
            ctx.update(METADATA)
                .set(METADATA.ISVALIDATED, input.isValidated)
                .set(METADATA.MODIFIEDAT, Instant.now())
                .where(METADATA.ID.eq(input.fileId))
                .execute()
        if (updatedRows != 1) {
            throw DBException("IsValid parameter was not updated for record with fileId: ${input.fileId}.")
        }
    }
}
