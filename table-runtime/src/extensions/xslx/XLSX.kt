package com.suryadigital.leo.tableRuntime.extensions.xslx

import com.github.pjfanning.xlsx.StreamingReader
import com.suryadigital.leo.tableRuntime.DataTable
import com.suryadigital.leo.tableRuntime.DataTable.CellType
import com.suryadigital.leo.tableRuntime.exceptions.DataConversionException
import com.suryadigital.leo.tableRuntime.exceptions.InvalidCellTypeException
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType.BLANK
import org.apache.poi.ss.usermodel.CellType.BOOLEAN
import org.apache.poi.ss.usermodel.CellType.ERROR
import org.apache.poi.ss.usermodel.CellType.FORMULA
import org.apache.poi.ss.usermodel.CellType.NUMERIC
import org.apache.poi.ss.usermodel.CellType.STRING
import org.apache.poi.ss.usermodel.CellType._NONE
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFCell
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Converts the current data inside the [DataTable] object to a XLSX output stream.
 * This function uses [Apache POI](https://poi.apache.org/) to convert [DataTable] into XLSX compatible format.
 *
 * For Apache POI to work, the underlying system needs some fonts installed.
 * Make sure your system has fonts installed before using this function.
 * On linux systems, you can install `fontconfig` package to have basic font functionality setup.
 * `fontconfig` installs the `DejaVu` font family along with some other font utilities.
 *
 * You can also set `org.apache.poi.ss.ignoreMissingFontSystem` system property as `true` to ignore the error due to missing fonts.
 *
 * NOTE: This implementation is not thread safe, i.e., during execution, it does not prevent some other function to modify the [DataTable].
 * Please make sure to do this operation in a thread-safe manner to avoid data races.
 *
 * @param outputStream [OutputStream] to which the [DataTable] data should be written.
 * @param xlsxProperties [XLSXProperties] to define XLSX format.
 *
 * @see [java.awt.GraphicsEnvironment]
 */
fun DataTable.toXLSX(
    outputStream: OutputStream,
    xlsxProperties: XLSXProperties = defaultXlsxProperties,
) {
    writeToXLSX(outputStream = outputStream) {
        val worksheetDocument = createSheet()
        var startRow = 0
        if (xlsxProperties.columnHeaders) {
            startRow = 1
            val workSheetRow = worksheetDocument.createRow(0)
            columnHeaders.forEachIndexed { index, header ->
                workSheetRow.createCell(index).setCellValue(header.name)
            }
        }
        columnHeaders.forEachIndexed { index, header ->
            worksheetDocument.setDefaultColumnStyle(
                index,
                getColumnFormatType(header.type.cellFormatter.xlsxFormat),
            )
        }
        for (row in 0 until numberOfRows) {
            val worksheetRow = worksheetDocument.createRow(row + startRow)
            for (column in 0 until numberOfColumns) {
                val cell = worksheetRow.createCell(column)
                setCellValueBasedOnColumnType(
                    cell = cell,
                    row = row,
                    column = column,
                    xlsxProperties = xlsxProperties,
                )
            }
        }
        columnHeaders.forEachIndexed { index, _ ->
            worksheetDocument.trackColumnForAutoSizing(index)
            worksheetDocument.autoSizeColumn(index)
        }
        write(outputStream)
    }
}

/**
 * Converts the current data inside the XLSX input stream into [DataTable] object.
 * This function uses [Apache POI](https://poi.apache.org/) to parse XLSX into [DataTable] compatible format.
 *
 * For Apache POI to work, the underlying system needs some fonts installed.
 * Make sure your system has some fonts installed before using this function.
 * On linux systems, you can install `fontconfig` package to have basic font functionality setup.
 * `fontconfig` installs the `DejaVu` font family along with some other font utilities.
 *
 * You can also set `org.apache.poi.ss.ignoreMissingFontSystem` system property as `true` to ignore the error due to missing fonts.
 *
 * NOTE: This implementation is not thread safe, i.e., during execution, it does not prevent some other function to modify the [DataTable].
 * Please make sure to do this operation in a thread-safe manner to avoid data races.
 *
 * @param inputStream [InputStream] from which the [DataTable] data should be parsed.
 * @param xlsxProperties [XLSXProperties] to define XLSX format.
 *
 * @throws InvalidCellTypeException
 *
 * @see [java.awt.GraphicsEnvironment]
 */
@Throws(InvalidCellTypeException::class)
fun DataTable.fromXLSX(
    inputStream: InputStream,
    xlsxProperties: XLSXProperties = defaultXlsxProperties,
) {
    val workbook: Workbook =
        StreamingReader.builder()
            .open(inputStream)
    val worksheetDocument = workbook.getSheetAt(0)
    worksheetDocument.rowIterator()
        .let {
            if (xlsxProperties.columnHeaders) {
                it.next()
            }
            it
        }.forEach { row ->
            var columnIndex = 0
            val dataTableRow = mutableListOf<Any?>()
            row.cellIterator().forEach { cell ->
                val columnType = columnHeaders[columnIndex]
                validateCell(cell, columnType)
                when (cell.cellType) {
                    BOOLEAN,
                    NUMERIC,
                    STRING,
                    FORMULA,
                    -> {
                        try {
                            when (val dataTableCellType = columnType.type) {
                                is CellType.String,
                                is CellType.UUID,
                                is CellType.Currency,
                                -> {
                                    dataTableRow.add(dataTableCellType.cellFormatter.fromString(cell.stringCellValue))
                                }

                                is CellType.Decimal -> {
                                    dataTableRow.add(cell.numericCellValue)
                                }

                                is CellType.Integer -> {
                                    dataTableRow.add(cell.numericCellValue.toInt())
                                }

                                is CellType.Timestamp -> {
                                    dataTableRow.add(cell.localDateTimeCellValue.atZone(ZoneId.of(xlsxProperties.zoneId)).toInstant())
                                }

                                is CellType.Date -> {
                                    dataTableRow.add(cell.localDateTimeCellValue.atZone(ZoneId.of(xlsxProperties.zoneId)).toLocalDate())
                                }

                                is CellType.Boolean -> {
                                    dataTableRow.add(cell.booleanCellValue)
                                }
                            }
                        } catch (e: DataConversionException) {
                            throw InvalidCellTypeException(message = e.message, cause = e, rowIndex = cell.rowIndex, columnIndex = cell.columnIndex)
                        }
                    }

                    BLANK -> {
                        dataTableRow.add(null)
                    }

                    _NONE, ERROR, null -> {
                        throw InvalidCellTypeException(
                            message = "Cell type ${cell.cellType} is not supported.",
                            rowIndex = cell.address.row,
                            columnIndex = cell.address.column,
                        )
                    }
                }
                columnIndex++
            }
            if (dataTableRow.isNotEmpty()) {
                addRow(dataTableRow)
            }
        }
}

/**
 * Creates a XLSX file and writes the [DataTable.columnHeaders] along with the column format to the specified [OutputStream].
 * This function uses [Apache POI](https://poi.apache.org/) to convert [DataTable] into XLSX compatible format.
 *
 * For Apache POI to work, the underlying system needs some fonts installed.
 * Make sure your system has some fonts installed before using this function.
 * On linux systems, you can install `fontconfig` package to have basic font functionality setup.
 *
 * @param outputStream [OutputStream] to which the [DataTable.columnHeaders] data should be written.
 *
 * @see [java.awt.GraphicsEnvironment]
 */
fun DataTable.generateXLSXTemplate(outputStream: OutputStream) {
    writeToXLSX(outputStream = outputStream) {
        val worksheetDocument = createSheet()
        val workSheetRow = worksheetDocument.createRow(0)
        columnHeaders.forEachIndexed { index, header ->
            workSheetRow.createCell(index).setCellValue(header.name)
            worksheetDocument.setDefaultColumnStyle(
                index,
                getColumnFormatType(header.type.cellFormatter.xlsxFormat),
            )
            worksheetDocument.trackColumnForAutoSizing(index)
            worksheetDocument.autoSizeColumn(index)
        }
        write(outputStream)
    }
}

private fun SXSSFWorkbook.getColumnFormatType(xlsxFormat: XLSXFormat): CellStyle {
    return createCellStyle().apply {
        dataFormat =
            creationHelper
                .createDataFormat()
                .getFormat(xlsxFormat.format)
    }
}

@Throws(InvalidCellTypeException::class)
private fun validateCell(
    cell: Cell,
    type: DataTable.ColumnType,
) {
    if (cell.cellType == BLANK) {
        if (type.optional) {
            return
        } else {
            throw InvalidCellTypeException(
                message = "Value for column ${type.name} cannot be null.",
                rowIndex = cell.rowIndex,
                columnIndex = cell.columnIndex,
            )
        }
    }
    val cellType =
        if (cell.cellType == FORMULA) {
            cell.cachedFormulaResultType
        } else {
            cell.cellType
        }
    if (cellType == ERROR) {
        throw InvalidCellTypeException(
            message = "Error while reading the XLSX. The given formula ${cell.cellFormula} cannot be resolved.",
            rowIndex = cell.rowIndex,
            columnIndex = cell.columnIndex,
        )
    }
    val isValidCellType =
        when (type.type) {
            is CellType.Boolean -> cellType == BOOLEAN
            is CellType.Currency -> cellType == STRING
            is CellType.Date -> cellType == NUMERIC
            is CellType.Decimal -> cellType == NUMERIC
            is CellType.Integer -> cellType == NUMERIC
            is CellType.String -> cellType == STRING
            is CellType.Timestamp -> cellType == NUMERIC
            is CellType.UUID -> cellType == STRING
        }
    if (!isValidCellType) {
        throw InvalidCellTypeException(
            message =
                """Cell type is not valid for the given cell.
                        |Expected data table cell type: ${type.type.javaClass.simpleName}
                        |Current XLSX cell type: $cellType
                """.trimMargin(),
            rowIndex = cell.rowIndex,
            columnIndex = cell.columnIndex,
        )
    }
}

private fun DataTable.setCellValueBasedOnColumnType(
    cell: SXSSFCell,
    row: Int,
    column: Int,
    xlsxProperties: XLSXProperties,
) {
    when (columnHeaders[column].type) {
        is CellType.Boolean -> {
            getCellAsBoolean(row, column)?.let(cell::setCellValue) ?: cell.setBlank()
        }

        is CellType.Date ->
            getCellAsLocalDate(row, column)?.let(cell::setCellValue) ?: cell.setBlank()

        is CellType.Decimal -> {
            getCellAsDouble(row, column)?.let(cell::setCellValue) ?: cell.setBlank()
        }

        is CellType.Integer -> {
            getCellAsLong(row, column)?.toDouble()?.let(cell::setCellValue) ?: cell.setBlank()
        }

        is CellType.Timestamp ->
            getCellAsInstant(row, column)?.let {
                cell.setCellValue(LocalDateTime.ofInstant(it, ZoneId.of(xlsxProperties.zoneId)))
            } ?: cell.setBlank()

        is CellType.Currency,
        is CellType.String,
        is CellType.UUID,
        -> {
            getFormattedCell(row, column).let(cell::setCellValue)
        }
    }
}

private fun writeToXLSX(
    outputStream: OutputStream,
    xlsxWriteBlock: SXSSFWorkbook.() -> Unit,
) {
    outputStream.use {
        SXSSFWorkbook().use(xlsxWriteBlock)
    }
}

private val defaultXlsxProperties by lazy(::XLSXProperties)
