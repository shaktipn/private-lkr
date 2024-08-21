package com.suryadigital.leo.basedb

/**
 * Used to describe a common qualifier name across all projects for the DBInstance that needs to be created.
 * @property READ_ONLY qualifier to identify read-only instance of a database.
 */
@Deprecated(
    message = "This has been replaced with Database::queryAsync.isReadOnly and will be removed in the future.",
    level = DeprecationLevel.WARNING,
)
object DBInstanceType {
    @Suppress("MemberVisibilityCanBePrivate")
    const val READ_ONLY: String = "READ_ONLY"
}
