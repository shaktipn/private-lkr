package com.suryadigital.leo.tableRuntime.extensions.csv

import com.fasterxml.jackson.core.FormatSchema
import com.fasterxml.jackson.databind.SequenceWriter
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.suryadigital.leo.tableRuntime.DataTable
import com.suryadigital.leo.tableRuntime.exceptions.DataConversionException
import com.suryadigital.leo.tableRuntime.exceptions.InvalidCellTypeException
import java.io.InputStream
import java.io.OutputStream

/**
 * Writes the current data inside the [DataTable] object to an OutputStream in CSV format.
 *
 * NOTE: This implementation is not thread safe, i.e., during execution, it does not prevent some other function to modify the [DataTable].
 * Please make sure to do this operation in a thread-safe manner to avoid data races.
 *
 * @param outputStream [OutputStream] to which the [DataTable] data should be written.
 * @param csvProperties [CSVProperties] to define CSV format.
 */
fun DataTable.toCSV(
    outputStream: OutputStream,
    csvProperties: CSVProperties = defaultCsvProperties,
) {
    val schema = getCSVSchema(csvProperties)
    writeToCSV(outputStream = outputStream, schema = schema) {
        if (csvProperties.columnHeaders) {
            write(columnHeaders.map(DataTable.ColumnType::name))
        }
        for (rowIndex in 0 until numberOfRows) {
            write(getFormattedRow(rowIndex))
        }
    }
}

/**
 * Reads the current data inside the given CSV [InputStream] to a [DataTable] object.
 *
 * NOTE: This implementation is not thread safe, i.e., during execution, it does not prevent some other function to modify the [DataTable].
 * Please make sure to do this operation in a thread-safe manner to avoid data races.
 *
 * @param inputStream [InputStream] from which the [DataTable] data should be parsed.
 * @param csvProperties [CSVProperties] to define CSV format.
 *
 * @throws InvalidCellTypeException if either optional value is found for a non-optional column, or some error occurs during data conversion.
 */
@Throws(InvalidCellTypeException::class)
fun DataTable.fromCSV(
    inputStream: InputStream,
    csvProperties: CSVProperties = defaultCsvProperties,
) {
    val schema =
        CsvSchema.emptySchema()
            .withLineSeparator(csvProperties.lineSeparator)
            .withColumnSeparator(csvProperties.delimiter)
            .withQuoteChar(csvProperties.quoteCharacter)
            .withSkipFirstDataRow(csvProperties.columnHeaders)
    with(inputStream) {
        csvMapper
            .readerWithSchemaFor(String::class.java)
            .with(schema)
            .with(CsvParser.Feature.WRAP_AS_ARRAY)
            .with(CsvParser.Feature.SKIP_EMPTY_LINES)
            .with(CsvParser.Feature.EMPTY_STRING_AS_NULL)
            .readTree(this)
            .mapIndexed { rowIndex, row ->
                val dataTableRow =
                    columnHeaders.mapIndexed { columnIndex, columnType ->
                        val cell = row[columnIndex]
                        if (cell == null || cell.isNull) {
                            if (columnType.optional) {
                                null
                            } else {
                                throw InvalidCellTypeException(
                                    message = "Value for column ${columnType.name} cannot be null.",
                                    rowIndex = rowIndex,
                                    columnIndex = columnIndex,
                                )
                            }
                        } else {
                            try {
                                columnType.type.cellFormatter.fromString(cell.textValue())
                            } catch (e: DataConversionException) {
                                throw InvalidCellTypeException(
                                    message = e.message,
                                    cause = e,
                                    rowIndex = rowIndex,
                                    columnIndex = columnIndex,
                                )
                            }
                        }
                    }
                addRow(dataTableRow)
            }
    }
}

private fun getCSVSchema(csvProperties: CSVProperties): FormatSchema {
    return if (csvProperties == defaultCsvProperties) {
        defaultCsvSchema
    } else {
        CsvSchema.emptySchema()
            .withQuoteChar(csvProperties.quoteCharacter)
            .withColumnSeparator(csvProperties.delimiter)
            .withLineSeparator(csvProperties.lineSeparator)
    }
}

/**
 * Generates a CSV Template file by writing [DataTable.columnHeaders] to the specified [OutputStream].
 * @param outputStream [OutputStream] to which the [DataTable.columnHeaders] should be written.
 * @param csvProperties [CSVProperties] to define CSV format.
 */
fun DataTable.generateCSVTemplate(
    outputStream: OutputStream,
    csvProperties: CSVProperties = defaultCsvProperties,
) {
    val schema = getCSVSchema(csvProperties)
    writeToCSV(outputStream = outputStream, schema = schema) {
        write(columnHeaders.map(DataTable.ColumnType::name))
    }
}

private fun writeToCSV(
    outputStream: OutputStream,
    schema: FormatSchema,
    csvWriteBlock: SequenceWriter.() -> Unit,
) {
    outputStream.use { stream ->
        csvMapper.writer(schema).writeValues(stream).use(csvWriteBlock)
    }
}

private val csvMapper by lazy(::CsvMapper)
private val defaultCsvProperties by lazy(::CSVProperties)
private val defaultCsvSchema by lazy {
    CsvSchema.emptySchema()
        .withQuoteChar(defaultCsvProperties.quoteCharacter)
        .withColumnSeparator(defaultCsvProperties.delimiter)
        .withLineSeparator(defaultCsvProperties.lineSeparator)
}
