package com.suryadigital.leo.tableRuntime.extensions.dataFormat

/**
 * Represents a class that contains information about the format in which the data will be presented.
 * @property format the format code that will be used to represent the data.
 */
interface DataFormat {
    val format: String
}

/**
 * Represents the set of format codes available to display the integer value.
 * @property format the format code that will be used to represent the integer value.
 */
enum class IntegerFormat(override val format: String) : DataFormat {
    /**
     * Default format code for integer.
     */
    IntegerDefault("0"),
}

/**
 * Represents the set of format codes available to display the decimal value.
 * @property format the format code that will be used to represent the decimal value.
 */
sealed class DecimalFormat(override val format: String) : DataFormat {
    /**
     * Default format code for decimal numbers.
     * @param precision number of significant digits after the decimal point.
     */
    class DecimalDefault(precision: Int) : DecimalFormat(format = "0.${"0".repeat(precision)}")
}

/**
 * Represents the set of format codes available to display the date value.
 * @property format the format code that will be used to represent the date value.
 */
enum class DateFormat(override val format: String) : DataFormat {
    /**
     * Represents [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html) date format.
     */
    YearMonthDay("yyyy-MM-dd"),

    /**
     * Represents the Day-Month-Year date format (DD-MM-YYYY).
     */
    DayMonthYear("dd-MM-yyyy"),

    /**
     * Represents the Month-Day-Year date format (MM-DD-YYYY).
     */
    MonthDayYear("MM-dd-yyyy"),
}

/**
 * Represents the set of format codes available to display the time value.
 * @property format the format code that will be used to represent the time value.
 */
enum class TimeFormat(override val format: String) : DataFormat {
    /**
     * Represents 12-Hour time format.
     */
    Hour12("hh:mm:ss AM/PM"),

    /**
     * Represents 24-Hour (Military Time) time format.
     */
    Hour24("HH:mm:ss"),
}

/**
 * Represents the set of format codes to display the timestamp value.
 * @property dateFormat [DateFormat] value that will be used to represent the date part of the timestamp.
 * @property timeFormat [TimeFormat] value that will be used to represent the time part of the timestamp.
 */
class DateTimeFormat(
    private val dateFormat: DateFormat,
    private val timeFormat: TimeFormat,
) : DataFormat {
    /**
     * The format code that will be used to represent the timestamp value.
     */
    override val format: String
        get() = "${dateFormat.format} ${timeFormat.format}"
}
