package com.suryadigital.leo.tableRuntime.exceptions

/**
 * This exception is thrown when the row size provided by the user does not match the number of columns of that [com.suryadigital.leo.tableRuntime.DataTable] object.
 *
 * @property message message passed to the exception.
 * @property expectedSize expected size of the row.
 * @property actualSize actual size of the row determined by [com.suryadigital.leo.tableRuntime.DataTable.columnHeaders] size.
 */
class InvalidRowSizeException(override val message: String, val expectedSize: Int, val actualSize: Int) : DataTableException(message = message, cause = null)
