package com.suryadigital.leo.tableRuntime.exceptions

/**
 * This exception is thrown when the type of cell does not match the [com.suryadigital.leo.tableRuntime.DataTable.CellType] of the respective column.
 *
 * @property message message passed to the exception.
 * @property cause chained exception due to which the current exception occurred.
 * @property rowIndex row number at which the exception occurred.
 * @property columnIndex column number at which the exception occurred.
 */
class InvalidCellTypeException(
    override val message: String,
    override val cause: Throwable? = null,
    val rowIndex: Int,
    val columnIndex: Int,
) : DataTableException(message = message, cause)
