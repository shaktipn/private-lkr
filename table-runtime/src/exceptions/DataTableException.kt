package com.suryadigital.leo.tableRuntime.exceptions

/**
 * This is the base exception inherited by all the exceptions thrown by the [com.suryadigital.leo.tableRuntime.DataTable].
 *
 * @property message message passed to the exception.
 * @property cause chained exception due to which the current exception occurred.
 */
abstract class DataTableException(override val message: String, override val cause: Throwable?) : Exception(message, cause)
