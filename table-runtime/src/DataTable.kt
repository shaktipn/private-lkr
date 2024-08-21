package com.suryadigital.leo.tableRuntime

import com.suryadigital.leo.tableRuntime.exceptions.DataConversionException
import com.suryadigital.leo.tableRuntime.exceptions.InvalidCellTypeException
import com.suryadigital.leo.tableRuntime.exceptions.InvalidRowSizeException
import com.suryadigital.leo.tableRuntime.exceptions.MissingRowException
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DateFormat.YearMonthDay
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DateTimeFormat
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DecimalFormat.DecimalDefault
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.IntegerFormat.IntegerDefault
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.TimeFormat.Hour24
import com.suryadigital.leo.tableRuntime.extensions.xslx.XLSXFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.time.format.DateTimeParseException
import java.util.Currency
import java.util.UUID

/**
 * Represents a single row in the data schema of the [DataTable.data].
 */
typealias RowRecord = List<Any?>

/**
 * Represents a list of [RowRecord] in the data schema of the [DataTable.data]
 */
typealias DataRecord = List<RowRecord>
private typealias MutableDataRecord = MutableList<MutableList<Any?>>

/**
 * Base structure to represent a Table.
 *
 * @property columnHeaders list of columns present in the table, with type metadata.
 * @property data data that should be present in table while constructing the [DataTable] object.
 */
data class DataTable(
    val columnHeaders: List<ColumnType>,
    private val data: DataRecord? = null,
) {
    private val mutableData: MutableDataRecord =
        data?.let {
            it.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { column, cell ->
                    validateCellType(rowIndex, column, cell)
                }
            }
            it.toMutableDataRecord()
        } ?: mutableListOf()

    //region Column Headers

    /**
     * Defines the name and the type of the column for the [DataTable].
     *
     * @param name name of the column.
     * @param type [CellType] of the column.
     * @param optional defines if the column accepts null values or not.
     */
    data class ColumnType(
        val name: String,
        val type: CellType,
        val optional: Boolean = false,
    )

    /**
     * Defines all the possible types for a [ColumnType] in [DataTable].
     *
     * @param cellFormatter formatter used to render the data present in the cell as [kotlin.String].
     */
    sealed class CellType(
        val cellFormatter: CellFormatter,
    ) {
        /**
         * A common interface implemented by every member of [CellType] sealed class, which represents how type conversion should be handled for `to` and `from` string operations.
         */
        interface CellFormatter {
            /**
             * Defines the `xlsx` format code for the cell.
             */
            val xlsxFormat: XLSXFormat

            /**
             * Function that converts [value] of type [kotlin.String] to the kotlin type corresponding to the given [CellType].
             *
             * @throws DataConversionException if there is an error while fetching the actual data type.
             */
            @Throws(DataConversionException::class)
            fun fromString(value: kotlin.String): Any

            /**
             * Function that converts and formats the [value] of the kotlin type corresponding to the given [CellType] into a human-readable [kotlin.String].
             */
            @Throws(DataConversionException::class)
            fun toFormattedString(value: Any): kotlin.String
        }

        /**
         * A [CellType] that represents a number [ColumnType].
         * Kotlin equivalent of this will be [Long].
         *
         * @param formatter formatter that should be used to format the Cell.
         */
        data class Integer(val formatter: IntegerCellFormatter = IntegerCellFormatter()) : CellType(formatter) {
            /**
             * Default implementation of [CellFormatter] for [CellType.Integer].
             */
            open class IntegerCellFormatter : CellFormatter {
                override val xlsxFormat: XLSXFormat = XLSXFormat.Integer(IntegerDefault)

                /**
                 * Converts value of type [kotlin.String] to its [kotlin.Int] representation.
                 */
                override fun fromString(value: kotlin.String): Any {
                    return try {
                        value.toInt()
                    } catch (e: IllegalArgumentException) {
                        throw DataConversionException(message = "Given value $value is not a valid integer.", cause = e)
                    }
                }

                /**
                 * Converts value of type [kotlin.Int] to its [kotlin.String] representation.
                 */
                override fun toFormattedString(value: Any): kotlin.String {
                    return "$value"
                }
            }
        }

        /**
         * A [CellType] that represents a decimal [ColumnType].
         * Kotlin equivalent of this will be [Double].
         *
         * @param formatter formatter that should be used to format the Cell.
         */
        data class Decimal(val formatter: DecimalCellFormatter = DecimalCellFormatter()) : CellType(formatter) {
            /**
             * Default implementation of [CellFormatter] for [CellType.Decimal].
             *
             * @param precision number of decimal points till which precision should be calculated.
             */
            open class DecimalCellFormatter(precision: Int = 4) : CellFormatter {
                override val xlsxFormat: XLSXFormat = XLSXFormat.Decimal(DecimalDefault(precision))

                companion object {
                    private val decimalFormat = DecimalFormat()
                }

                init {
                    decimalFormat.applyPattern(("0.${"0".repeat(precision)}"))
                }

                /**
                 * Converts value of type [kotlin.String] to its [kotlin.Double] representation.
                 */
                override fun fromString(value: kotlin.String): Any {
                    return try {
                        decimalFormat.parse(value).toDouble()
                    } catch (e: ParseException) {
                        throw DataConversionException(message = "Given value $value is not a valid decimal number.", cause = e)
                    }
                }

                /**
                 * Converts value of type [kotlin.Double] to its [kotlin.String] representation.
                 */
                override fun toFormattedString(value: Any): kotlin.String {
                    return decimalFormat.format(value)
                }
            }
        }

        /**
         * A [CellType] that represents a string [ColumnType].
         * Kotlin equivalent of this will be [kotlin.String].
         *
         * @param formatter formatter that should be used to format the Cell.
         */
        data class String(val formatter: StringCellFormatter = StringCellFormatter()) : CellType(formatter) {
            /**
             * Default implementation of [CellFormatter] for [CellType.String].
             */
            open class StringCellFormatter : CellFormatter {
                override val xlsxFormat: XLSXFormat = XLSXFormat.Default

                /**
                 * The default implementation does not do anything to the already existing string representation. But this can be overriden to apply some sort of custom formatting.
                 */
                override fun fromString(value: kotlin.String): Any {
                    return value
                }

                /**
                 * The default implementation converts the value from type [Any] to a [kotlin.String]. This can be overriden to apply some sort of custom formatting.
                 */
                override fun toFormattedString(value: Any): kotlin.String {
                    return "$value"
                }
            }
        }

        /**
         * A [CellType] that represents a UUID [ColumnType].
         * Kotlin equivalent of this will be [java.util.UUID].
         *
         * @param formatter formatter that should be used to format the Cell.
         */
        data class UUID(val formatter: UUIDCellFormatter = UUIDCellFormatter()) : CellType(formatter) {
            /**
             * Default implementation of [CellFormatter] for [CellType.UUID].
             */
            open class UUIDCellFormatter : CellFormatter {
                override val xlsxFormat: XLSXFormat = XLSXFormat.Default

                /**
                 * Converts value of type [kotlin.String] to its [java.util.UUID] representation.
                 */
                override fun fromString(value: kotlin.String): Any {
                    return try {
                        java.util.UUID.fromString(value)
                    } catch (e: IllegalArgumentException) {
                        throw DataConversionException(message = "Given string $value is not a valid UUID.", cause = e)
                    }
                }

                /**
                 * Converts value of type [java.util.UUID] to its [kotlin.String] representation.
                 */
                override fun toFormattedString(value: Any): kotlin.String {
                    return "$value"
                }
            }
        }

        /**
         * A [CellType] that represents a date [ColumnType].
         * Kotlin equivalent of this will be [LocalDate].
         *
         * @param formatter formatter that should be used to format the Cell.
         */
        data class Date(val formatter: DateFormatter = DateFormatter(xlsxFormat = DEFAULT_DATE_FORMAT)) : CellType(formatter) {
            /**
             * Default implementation of [CellFormatter] for [CellType.Date].
             */
            open class DateFormatter(override val xlsxFormat: XLSXFormat = DEFAULT_DATE_FORMAT) : CellFormatter {
                companion object {
                    /**
                     * [DateTimeFormatter] object that is used for date formatting.
                     */
                    val dateFormat: DateTimeFormatter =
                        DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT.dateFormat.format).withZone(
                            ZoneId.of(
                                DEFAULT_ZONE_ID,
                            ),
                        )
                }

                /**
                 * Converts value of type [kotlin.String] to its [java.time.LocalDate] representation.
                 */
                override fun fromString(value: kotlin.String): Any {
                    return try {
                        LocalDate.from(dateFormat.parse(value))
                    } catch (e: DateTimeParseException) {
                        throw DataConversionException(message = "Given value $value is not a valid Date.", cause = e)
                    }
                }

                /**
                 * Converts value of type [java.time.LocalDate] to its [kotlin.String] representation.
                 */
                override fun toFormattedString(value: Any): kotlin.String {
                    if (value !is LocalDate) {
                        throw IllegalStateException("Given value $value is not of type Date.")
                    }
                    return dateFormat.format(value)
                }
            }
        }

        /**
         * A [CellType] that represents a timestamp [ColumnType].
         * Kotlin equivalent of this will be [Instant].
         *
         * @param formatter formatter that should be used to format the Cell.
         */
        data class Timestamp(val formatter: TimestampFormatter = TimestampFormatter(DEFAULT_DATE_TIME_FORMAT)) :
            CellType(formatter) {
            /**
             * Default implementation of [CellFormatter] for [CellType.Timestamp].
             */
            open class TimestampFormatter(override val xlsxFormat: XLSXFormat.DateTime = DEFAULT_DATE_TIME_FORMAT) : CellFormatter {
                companion object {
                    private val dateTimeFormatter: DateTimeFormatter = ISO_INSTANT
                }

                /**
                 * Converts value of type [kotlin.String] to its [java.time.Instant] representation.
                 */
                override fun fromString(value: kotlin.String): Any {
                    return try {
                        Instant.from(dateTimeFormatter.parse(value))
                    } catch (e: DateTimeParseException) {
                        throw DataConversionException(message = "Given value $value is not a valid Timestamp.", cause = e)
                    }
                }

                /**
                 * Converts value of type [java.time.Instant] to its [kotlin.String] representation.
                 */
                override fun toFormattedString(value: Any): kotlin.String {
                    if (value !is Instant) {
                        throw IllegalStateException("Given value $value is not of type Timestamp.")
                    }
                    return dateTimeFormatter.format(value)
                }
            }
        }

        /**
         * A [CellType] that represents a currency [ColumnType].
         * Kotlin equivalent of this will be [java.util.Currency].
         *
         * @param formatter formatter that should be used to format the Cell.
         */
        data class Currency(val formatter: CurrencyFormatter = CurrencyFormatter()) :
            CellType(formatter) {
            /**
             * Default implementation of [CellFormatter] for [CellType.Currency].
             */
            open class CurrencyFormatter(private val formatType: FormatType = FormatType.CODE) : CellFormatter {
                override val xlsxFormat: XLSXFormat = XLSXFormat.Default

                companion object {
                    internal val availableCurrencies: MutableSet<java.util.Currency> = java.util.Currency.getAvailableCurrencies()

                    /**
                     * Used to define the type of formatting that needs to be done for the [Currency].
                     */
                    enum class FormatType {
                        /**
                         * Defines the format to be ISO 4217 currency code of the currency.
                         */
                        CODE,

                        /**
                         * Defines the format to be of type symbol of the currency. For example, for USD, the symbol will be $.
                         */
                        SYMBOL,

                        /**
                         * Defines the format to be the name that is suitable for displaying the currency.
                         */
                        DISPLAY_NAME,

                        /**
                         * Defines the format to be the three-digit numeric code that can be used to represent the currency.
                         */
                        NUMERIC_CODE,
                    }
                }

                /**
                 * Converts value of type [kotlin.String] to its [java.util.Currency.currencyCode] representation.
                 */
                override fun fromString(value: kotlin.String): Any {
                    return when (formatType) {
                        FormatType.CODE ->
                            availableCurrencies.firstOrNull { it.currencyCode == value }
                                ?: throw DataConversionException("Currency with code $value not found.")

                        FormatType.SYMBOL ->
                            availableCurrencies.firstOrNull { it.symbol == value }
                                ?: throw DataConversionException("Currency with symbol $value not found.")

                        FormatType.DISPLAY_NAME ->
                            availableCurrencies.firstOrNull { it.displayName == value }
                                ?: throw DataConversionException("Currency with display name $value not found.")

                        FormatType.NUMERIC_CODE ->
                            availableCurrencies.firstOrNull { "${it.numericCode}" == value }
                                ?: throw DataConversionException("Currency with numeric code $value not found.")
                    }
                }

                /**
                 * Converts value of type [java.util.Currency.currencyCode] to its [kotlin.String] representation.
                 */
                override fun toFormattedString(value: Any): kotlin.String {
                    val currency =
                        try {
                            value as java.util.Currency
                        } catch (e: java.lang.ClassCastException) {
                            throw DataConversionException(message = "Given value $value is not a valid Currency.", cause = e)
                        }
                    return when (formatType) {
                        FormatType.CODE -> currency.currencyCode
                        FormatType.SYMBOL -> currency.symbol
                        FormatType.DISPLAY_NAME -> currency.displayName
                        FormatType.NUMERIC_CODE -> currency.numericCodeAsString
                    }
                }
            }
        }

        /**
         * A [CellType] that represents a boolean [ColumnType].
         * Kotlin equivalent of this will be [Boolean].
         *
         * @param formatter formatter that should be used to format the Cell.
         */
        data class Boolean(val formatter: BooleanFormatter = BooleanFormatter()) : CellType(formatter) {
            /**
             * Default implementation of [CellFormatter] for [CellType.Boolean].
             */
            open class BooleanFormatter(private val type: BooleanType = BooleanType.LITERAL_LOWERCASE) : CellFormatter {
                override val xlsxFormat: XLSXFormat = XLSXFormat.Default

                companion object {
                    /**
                     * Enum class which defines different ways to represent boolean values in [kotlin.String] format.
                     *
                     * @param forTrue [String] representation for Boolean value to be true.
                     * @param forFalse [String] representation for Boolean value to be false.
                     */
                    enum class BooleanType(val forTrue: kotlin.String, val forFalse: kotlin.String) {
                        /**
                         * How booleans are represented in Binary format.
                         *
                         * Here:
                         *
                         * 1 -> true
                         *
                         * 0 -> false
                         */
                        BINARY("1", "0"),

                        /**
                         * How booleans are represented literally, but the string is in lowercase format. Here:
                         */
                        LITERAL_LOWERCASE("true", "false"),

                        /**
                         * How booleans are represented literally, but the string is in uppercase format. Here:
                         */
                        LITERAL_UPPERCASE("TRUE", "FALSE"),

                        /**
                         * How booleans are represented literally, but the string is in PascalCase format. Here:
                         */
                        LITERAL_PASCALCASE("True", "False"),

                        /**
                         * Only the first character of their representative string representation (t for true, f for false) is used to represent the boolean.
                         */
                        LOWERCASE_CHARACTER("t", "f"),

                        /**
                         * Only the first character in the upper case of their representative string representation (T for true, F for false) is used to represent the boolean.
                         */
                        UPPERCASE_CHARACTER("T", "F"),
                    }
                }

                /**
                 * Converts value of type [kotlin.String] to its [kotlin.Boolean] representation.
                 */
                override fun fromString(value: kotlin.String): Any {
                    val booleanType =
                        BooleanType.entries.firstOrNull { it.forTrue == value || it.forFalse == value }
                            ?: throw DataConversionException("$value is not a valid Boolean representation.")
                    return booleanType.forTrue == value
                }

                /**
                 * Converts value of the type [kotlin.Boolean] to its [kotlin.String] representation.
                 */
                override fun toFormattedString(value: Any): kotlin.String {
                    return if (value == true) {
                        type.forTrue
                    } else {
                        type.forFalse
                    }
                }
            }
        }
    }

    //endregion

    //region Cell Operations

    /**
     * Iterates through all the cells in the [DataTable] object and performs the [transform] function on each cell.
     *
     * @param transform action that needs to be performed on each cell.
     */
    fun forEachCell(transform: Any?.() -> Unit) {
        mutableData.forEach { row ->
            row.forEach { cell ->
                cell.transform()
            }
        }
    }

    /**
     * This function is used to set given value at the provided coordinates.
     * The new value will overwrite the value at the given coordinates.
     *
     * @param value value that needs to be inserted.
     * @param rowIndex row index at which the value should be inserted.
     * @param columnIndex column index at which the value should be inserted.
     */
    fun setCell(
        value: Any?,
        rowIndex: Int,
        columnIndex: Int,
    ) {
        validateCellType(rowIndex, columnIndex, value)
        mutableData[rowIndex][columnIndex] = value
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [Any].
     *
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCell(
        rowIndex: Int,
        columnIndex: Int,
    ): Any? {
        return mutableData[rowIndex][columnIndex]
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [String].
     *
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCellAsString(
        rowIndex: Int,
        columnIndex: Int,
    ): String? {
        return try {
            getCell(rowIndex, columnIndex)?.let { it as String }
        } catch (_: ClassCastException) {
            throw InvalidCellTypeException(
                message = "Cannot get given cell of column type ${columnHeaders[columnIndex].type::class.simpleName} as type String.",
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [Long].
     *
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCellAsLong(
        rowIndex: Int,
        columnIndex: Int,
    ): Long? {
        return try {
            val value = getCell(rowIndex, columnIndex) ?: return null
            if (value is Int) {
                value.toLong()
            } else {
                value as Long
            }
        } catch (e: ClassCastException) {
            throw InvalidCellTypeException(
                message = "Cannot get given cell of column type ${columnHeaders[columnIndex].type::class.simpleName} as type Long.",
                cause = e,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [Double].
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCellAsDouble(
        rowIndex: Int,
        columnIndex: Int,
    ): Double? {
        return try {
            getCell(rowIndex, columnIndex)?.let { it as Double }
        } catch (e: ClassCastException) {
            throw InvalidCellTypeException(
                message = "Cannot get given cell of column type ${columnHeaders[columnIndex].type::class.simpleName} as type Double.",
                cause = e,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [UUID].
     *
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCellAsUUID(
        rowIndex: Int,
        columnIndex: Int,
    ): UUID? {
        return try {
            getCell(rowIndex, columnIndex)?.let { it as UUID }
        } catch (e: ClassCastException) {
            throw InvalidCellTypeException(
                message = "Cannot get given cell of column type ${columnHeaders[columnIndex].type::class.simpleName} as type UUID.",
                cause = e,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [Boolean].
     *
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCellAsBoolean(
        rowIndex: Int,
        columnIndex: Int,
    ): Boolean? {
        return try {
            getCell(rowIndex, columnIndex)?.let { it as Boolean }
        } catch (e: ClassCastException) {
            throw InvalidCellTypeException(
                message = "Cannot get given cell of column type ${columnHeaders[columnIndex].type::class.simpleName} as type Boolean.",
                cause = e,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [LocalDate].
     *
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCellAsLocalDate(
        rowIndex: Int,
        columnIndex: Int,
    ): LocalDate? {
        return try {
            getCell(rowIndex, columnIndex)?.let { it as LocalDate }
        } catch (e: ClassCastException) {
            throw InvalidCellTypeException(
                message = "Cannot get given cell of column type ${columnHeaders[columnIndex].type::class.simpleName} as type LocalDate.",
                cause = e,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [Instant].
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCellAsInstant(
        rowIndex: Int,
        columnIndex: Int,
    ): Instant? {
        return try {
            getCell(rowIndex, columnIndex)?.let { it as Instant }
        } catch (e: ClassCastException) {
            throw InvalidCellTypeException(
                message = "Cannot get given cell of column type ${columnHeaders[columnIndex].type::class.simpleName} as type Instant.",
                cause = e,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    /**
     * This function is used to get the value of the cell present at the given coordinates as type [java.util.Currency].
     *
     * @param rowIndex row index from which the value should be fetched.
     * @param columnIndex column index from which the value should be fetched.
     */
    fun getCellAsCurrency(
        rowIndex: Int,
        columnIndex: Int,
    ): Currency? {
        return try {
            getCell(rowIndex, columnIndex)?.let { it as Currency }
        } catch (e: ClassCastException) {
            throw InvalidCellTypeException(
                message = "Cannot get given cell of column type ${columnHeaders[columnIndex].type::class.simpleName} as type Currency.",
                cause = e,
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    /**
     * Gets the formatted string equivalent of the value at the given cell.
     *
     * @param rowIndex row index at which the value is present.
     * @param columnIndex column index at which the value is present.
     */
    fun getFormattedCell(
        rowIndex: Int,
        columnIndex: Int,
    ): String {
        return getCell(rowIndex, columnIndex)?.let(columnHeaders[columnIndex].type.cellFormatter::toFormattedString) ?: NULL_STRING
    }

    //endregion

    //region Row Operations

    /**
     * Iterates through the rows in the [DataTable] object and performs the [transform] function on each row.
     *
     * @param transform action that needs to be performed on each row.
     */
    fun forEachRow(transform: RowRecord.() -> Unit) {
        mutableData.forEach { it.transform() }
    }

    /**
     * This function is used to set the value of the row at the given index.
     * The new value will overwrite the value at the given index.
     *
     * @param row row data that needs to be inserted.
     * @param atIndex index at which the row should be inserted.
     *
     * @throws InvalidRowSizeException if the given row size does not match the size of the column headers.
     * @throws MissingRowException if there are no rows present prior to the index at which user is trying to insert data.
     */
    fun setRow(
        row: RowRecord,
        atIndex: Int,
    ) {
        validateRow(row, atIndex)
        row.forEachIndexed { column, value ->
            setCell(value, atIndex, column)
        }
    }

    /**
     * This function is used to add value of the row in [DataTable] object.
     *
     * @param row row data that needs to be inserted.
     * @param atIndex index at which the row should be inserted.
     * - If provided, [row] is inserted at the given index, and the existing value at that index is shifted by one.
     * - If not provided, [row] is inserted at the end of current data.
     */
    fun addRow(
        row: RowRecord,
        atIndex: Int? = null,
    ) {
        validateRow(row, atIndex)
        if (atIndex != null) {
            mutableData.add(atIndex, row.toMutableList())
        } else {
            mutableData.add(row.toMutableList())
        }
    }

    /**
     * This function is used to get the row data present on the given index.
     *
     * @param atIndex index from which the row data needs to be fetched.
     *
     * @throws MissingRowException if row at given index does not exist.
     */
    fun getRow(atIndex: Int): RowRecord {
        checkIfRowExists(atIndex)
        return mutableData[atIndex]
    }

    /**
     * Gets the formatted list of string representing the entire row at the given index.
     *
     * @param rowIndex row index at which the row is present.
     *
     * @throws MissingRowException if no row is found at the given [rowIndex].
     */
    fun getFormattedRow(rowIndex: Int): List<String> {
        checkIfRowExists(rowIndex)
        return mutableData[rowIndex].mapIndexed { columnIndex, value ->
            value?.let(columnHeaders[columnIndex].type.cellFormatter::toFormattedString) ?: NULL_STRING
        }
    }

    /**
     * This function is used to delete the row data at the given row index.
     *
     * @param atIndex row index at which the data should be deleted.
     */
    fun deleteRow(atIndex: Int) {
        checkIfRowExists(atIndex)
        mutableData.removeAt(atIndex)
    }

    //endregion

    //region Table Operations

    /**
     * This function is used to fetch all the data present in the [DataTable] object.
     */
    fun getTable(): DataRecord {
        return mutableData
    }

    /**
     * This function is used to append the table data given at the end of data that is already present in the [DataTable] object.
     * @param data table data that needs to be appended to the existing data.
     *
     * @throws InvalidRowSizeException if the given row size does not match the size of the column headers.
     * @throws MissingRowException if there are no rows present prior to the index at which user is trying to insert data.
     */
    fun appendTableData(data: DataRecord) {
        data.forEach(::validateRow)
        mutableData.plusAssign(data.toMutableDataRecord())
    }

    /**
     * Returns the number of rows currently present in the [DataTable] object.
     */
    val numberOfRows: Int
        get() = mutableData.size

    /**
     * Returns the number of columns in the [DataTable] object.
     */
    val numberOfColumns: Int
        get() = columnHeaders.size

    //endregion

    private fun DataRecord.toMutableDataRecord(): MutableDataRecord {
        return this.map(RowRecord::toMutableList).toMutableList()
    }

    private fun validateRow(
        row: RowRecord,
        atIndex: Int? = null,
    ) {
        if (row.size != columnHeaders.size) {
            throw InvalidRowSizeException(
                message = "The given row size of ${row.size} does not match with the number of headers ${columnHeaders.size}. Given row contains: $row",
                expectedSize = columnHeaders.size,
                actualSize = row.size,
            )
        }
        if (atIndex != null) {
            checkIfRowExists(atIndex)
            row.forEachIndexed { columnIndex, value ->
                validateCellType(atIndex, columnIndex, value)
            }
        }
    }

    private fun checkIfRowExists(atIndex: Int) {
        if (mutableData.size < atIndex) {
            throw MissingRowException(
                message = "Cannot add row at index $atIndex as number of rows is equal to ${mutableData.size}.",
                currentIndex = atIndex,
                numberOfRows = numberOfRows,
            )
        }
    }

    private fun validateCellType(
        rowIndex: Int,
        columnIndex: Int,
        value: Any?,
    ) {
        val columnType = columnHeaders[columnIndex]
        if (value == null) {
            if (columnType.optional) {
                return
            } else {
                throw InvalidCellTypeException(
                    message = "Value for column ${columnType.name} cannot be null.",
                    rowIndex = rowIndex,
                    columnIndex = columnIndex,
                )
            }
        }
        val isValid =
            when (columnType.type) {
                is CellType.Integer -> value is Long || value is Int
                is CellType.Boolean -> value is Boolean
                is CellType.String -> value is String
                is CellType.Currency -> value is Currency
                is CellType.Date -> value is LocalDate
                is CellType.Decimal -> value is Double
                is CellType.Timestamp -> value is Instant
                is CellType.UUID -> value is UUID
            }
        if (!isValid) {
            throw InvalidCellTypeException(
                message = "Invalid input $value for column ${columnHeaders[columnIndex].name}. Column type: ${columnHeaders[columnIndex].type.javaClass.simpleName}. Input ${value.javaClass.typeName}.",
                rowIndex = rowIndex,
                columnIndex = columnIndex,
            )
        }
    }

    companion object {
        private const val NULL_STRING: String = ""
    }
}

/**
 * Default [java.time.ZoneId] for which the Date/Time types should be formatted.
 */
const val DEFAULT_ZONE_ID: String = "UTC"

/**
 * Default [XLSXFormat.Date] format that will be used when generating `xlsx` files.
 */
val DEFAULT_DATE_FORMAT: XLSXFormat.Date = XLSXFormat.Date(YearMonthDay)

/**
 * Default [XLSXFormat.DateTime] format that will be used when generating `xlsx` files.
 */
val DEFAULT_DATE_TIME_FORMAT: XLSXFormat.DateTime =
    XLSXFormat.DateTime(
        dateTimeFormat =
            DateTimeFormat(
                dateFormat = YearMonthDay,
                timeFormat = Hour24,
            ),
    )
