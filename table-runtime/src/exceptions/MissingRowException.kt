package com.suryadigital.leo.tableRuntime.exceptions

/**
 * This exception is thrown when there are rows missing prior to the index at which user wants to add row data.
 *
 * @property message message passed to the exception.
 * @property currentIndex index of the current row at which the operation is being performed.
 * @property numberOfRows total number of rows currently present in the [com.suryadigital.leo.tableRuntime.DataTable].
 */
class MissingRowException(override val message: String, val currentIndex: Int, val numberOfRows: Int) : DataTableException(message = message, null)
