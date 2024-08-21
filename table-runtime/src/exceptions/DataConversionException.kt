package com.suryadigital.leo.tableRuntime.exceptions

/**
 * This exception is thrown when an error occurs while converting data from one type to another.
 *
 * @property message message passed to the exception.
 * @property cause chained exception due to which the current exception occurred.
 */
class DataConversionException(override val message: String, override val cause: Throwable? = null) : DataTableException(message = message, cause = cause)
