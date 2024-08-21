package com.suryadigital.leo.tableRuntime.extensions.csv

/**
 * Properties used by CSV extensions to identify how the CSV is formatted.
 * @param delimiter determines how values inside a row are separated in the CSV.
 * @param quoteCharacter determines how the value of a cell is quoted in the CSV.
 * @param lineSeparator determines how a newline in defined for the CSV.
 * @param columnHeaders determines if a column header should be present in the CSV or not.
 */
data class CSVProperties(
    val delimiter: Char = ',',
    val quoteCharacter: Char = '\"',
    val lineSeparator: String = "\n",
    val columnHeaders: Boolean = true,
)
