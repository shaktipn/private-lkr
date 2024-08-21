package com.suryadigital.leo.tableRuntime.extensions.xslx

/**
 * Properties used by XLSX extension to identify how the XLSX is formatted.
 * @property columnHeaders determines if a column header should be present in the XLSX or not.
 * @property zoneId [java.time.ZoneId] in which the time should be formatted.
 */
data class XLSXProperties(
    val columnHeaders: Boolean = true,
    val zoneId: String = "UTC",
)
