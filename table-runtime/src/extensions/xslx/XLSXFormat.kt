package com.suryadigital.leo.tableRuntime.extensions.xslx

import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DateFormat
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DateTimeFormat
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DecimalFormat
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.IntegerFormat

/**
 * Defines the XLSX format style for different data types.
 * @property format the format code that will be used to represent the values for different data types.
 */
sealed class XLSXFormat(val format: String) {
    /**
     * Represents the default format style. This is to be used when no specific format is required.
     */
    data object Default : XLSXFormat(format = "General")

    /**
     * Represents the format for a date value.
     * @property dateFormat the format code in which the date value will be displayed.
     */
    class Date(val dateFormat: DateFormat) : XLSXFormat(dateFormat.format)

    /**
     * Represents the format for a timestamp value.
     * @property dateTimeFormat the format code in which the timestamp value will be displayed.
     */
    class DateTime(val dateTimeFormat: DateTimeFormat) : XLSXFormat(dateTimeFormat.format)

    /**
     * Represents the format for an integer value.
     * @property integerFormat the format code in which the integer value will be displayed.
     */
    class Integer(private val integerFormat: IntegerFormat) : XLSXFormat(integerFormat.format)

    /**
     * Represents the format for a decimal value.
     * @property decimalFormat the format code in which the decimal value will be displayed.
     */
    class Decimal(private val decimalFormat: DecimalFormat) : XLSXFormat(decimalFormat.format)
}
