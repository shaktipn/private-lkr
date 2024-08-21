package com.suryadigital.leo.tableRuntime

import com.suryadigital.leo.tableRuntime.DataTable.CellType.Date.DateFormatter
import com.suryadigital.leo.tableRuntime.DataTable.CellType.Timestamp.TimestampFormatter
import com.suryadigital.leo.tableRuntime.exceptions.InvalidCellTypeException
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DateFormat.DayMonthYear
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DateFormat.MonthDayYear
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.DateTimeFormat
import com.suryadigital.leo.tableRuntime.extensions.dataFormat.TimeFormat.Hour12
import com.suryadigital.leo.tableRuntime.extensions.xslx.XLSXFormat
import com.suryadigital.leo.tableRuntime.extensions.xslx.XLSXProperties
import com.suryadigital.leo.tableRuntime.extensions.xslx.fromXLSX
import com.suryadigital.leo.tableRuntime.extensions.xslx.generateXLSXTemplate
import com.suryadigital.leo.tableRuntime.extensions.xslx.toXLSX
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.Currency
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue

internal class DataTableXLSXTest : AbstractTest() {
    @Test
    fun `Test toXLSX()`() {
        val expectedXLSX = getResourceFilePath("/ExpectedDataTable.xlsx")
        val file = File("${expectedXLSX.parent}/GeneratedDataTable.xlsx")
        FileOutputStream(file).use(baseTable::toXLSX)
        assertXLSXFile(expectedXLSX, file)
    }

    /*
     * For the baseTable in our current implementation, the `toXLSX` function takes an average of 4900ms to render 100K records.
     * The intention behind the following test case is to set a baseline of 6000ms (taking an upper margin of around 1000ms from the average) for the function to render 100K records.
     * This would ensure that if a change is made to the function implementation that results in a major performance regression, the test case will ensure
     * that such changes are caught early in the development cycle.
     */
    @Test
    fun `Test positive for toXLSX() with 100K records to take less than 6000 milliseconds`() {
        val resourcePath = getResourceFilePath("/ExpectedDataTable.xlsx").parent
        for (i in 2..10_000) {
            baseTable.appendTableData(data)
        }
        val file = File("$resourcePath/GeneratedDataTableWith100Krecords.xlsx")
        val timedValue =
            TimeSource.Monotonic.measureTimedValue {
                FileOutputStream(file).use(baseTable::toXLSX)
            }
        assertEquals(baseTable.numberOfRows, 100_000)
        assert(timedValue.duration.inWholeMilliseconds < 6000) { "Time taken to process 100K records is ${timedValue.duration.inWholeMilliseconds}" }
    }

    @Test
    fun `Test toXLSX() without column headers`() {
        val expectedXLSX = getResourceFilePath("/ExpectedDataTableWithoutColumnHeaders.xlsx")
        val file = File("${expectedXLSX.parent}/GeneratedDataTableWithoutColumnHeaders.xlsx")
        FileOutputStream(file).use {
            baseTable.toXLSX(it, XLSXProperties(columnHeaders = false))
        }
        assertXLSXFile(expectedXLSX, file)
    }

    @Test
    fun `Test toXLSX() with null values`() {
        val expectedXLSX = getResourceFilePath("/ExpectedDataTableWithNullValues.xlsx")
        val file = File("${expectedXLSX.parent}/GeneratedDataTableWithNullValues.xlsx")
        for (i in 0..7) {
            baseTableWithNullableColumns.setCell(null, i, i)
        }
        FileOutputStream(file).use {
            baseTableWithNullableColumns.toXLSX(it, XLSXProperties(columnHeaders = false))
        }
        assertXLSXFile(expectedXLSX, file)
    }

    @Test
    fun `Test fromXLSX()`() {
        val expectedXLSX = getResourceFilePathAsInputStream("/ExpectedDataTable.xlsx")
        val newTable = DataTable(columnHeaders)
        newTable.fromXLSX(expectedXLSX)
        assertEquals(baseTable.getTable(), newTable.getTable())
    }

    @Test
    fun `Test fromXLSX() with blank cells`() {
        val expectedXLSX = getResourceFilePathAsInputStream("/DataTableWithBlankCells.xlsx")
        val nullableColumnHeaders = columnHeaders.dropLast(1) + DataTable.ColumnType("recordedAt", DataTable.CellType.Timestamp(), true)
        val newTable = DataTable(nullableColumnHeaders)
        newTable.fromXLSX(expectedXLSX)
        val data =
            listOf(
                listOf("Alice Smith", 32, 65.2, Currency.getInstance("USD"), true, UUID.fromString("601a13cf-eaa4-48f3-9e88-c92ecc386dfe"), LocalDate.of(1989, 7, 20), null),
                listOf("Bob Johnson", 45, 78.9, Currency.getInstance("EUR"), false, UUID.fromString("c0b75f54-c6df-4331-a981-0378b14f79d3"), LocalDate.of(1977, 3, 12), null),
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), null),
                listOf("Diana Miller", 38, 55.5, Currency.getInstance("CAD"), true, UUID.fromString("c1bb2e07-2332-4b74-b9ad-3703460dd3ec"), LocalDate.of(1984, 9, 18), null),
                listOf("Edward Davis", 50, 82.3, Currency.getInstance("AUD"), false, UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), LocalDate.of(1972, 1, 30), null),
                listOf("Fiona White", 27, 68.7, Currency.getInstance("JPY"), true, UUID.fromString("9f3ca552-a7ca-4c3b-9ac7-3f27849cd724"), LocalDate.of(1995, 4, 8), null),
                listOf("George Smith", 33, 75.6, Currency.getInstance("CHF"), false, UUID.fromString("38e3286b-b442-4208-995f-d45280ea1fc1"), LocalDate.of(1988, 6, 14), null),
                listOf("Helen Brown", 42, 60.8, Currency.getInstance("INR"), true, UUID.fromString("f8324d58-75d7-4987-ae1e-28f1bbaa8f92"), LocalDate.of(1979, 10, 22), null),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), null),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), null),
            )
        assertEquals(DataTable(nullableColumnHeaders, data).getTable(), newTable.getTable())
    }

    @Test
    fun `Test fromXLSX() with blank cells and non-nullable column type`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/DataTableWithBlankCells.xlsx", rowIndex = 1, columnIndex = 7)
    }

    /*
     * For the baseTable in our current implementation, the `fromXLSX` function takes an average of 17000ms to render 100K records.
     * The intention behind the following test case is to set a baseline of 20000ms (taking an upper margin of around 3000ms from the average) for the function to parse 100K records.
     * This would ensure that if a change is made to the function implementation that results in a major performance regression, the test case will ensure
     * that such changes are caught early in the development cycle.
     */
    @Test
    fun `Test fromXLSX() with 100K records in less than 17000ms`() {
        val file = getResourceFilePathAsInputStream("/DataTableWith100Krecords.xlsx")
        val newTable = DataTable(columnHeaders)
        val timeTakenForParsing100KRecords =
            TimeSource.Monotonic.measureTimedValue {
                newTable.fromXLSX(file)
            }
        assert(timeTakenForParsing100KRecords.duration.inWholeMilliseconds < 20000) { "Took more time than expected for parsing XLSX: ${timeTakenForParsing100KRecords.duration.inWholeMilliseconds}ms" }
        assertEquals(100_000, newTable.numberOfRows)
    }

    @Test
    fun `Test fromXLSX() without headers`() {
        val expectedXLSX = getResourceFilePathAsInputStream("/ExpectedDataTableWithoutColumnHeaders.xlsx")
        val newTable = DataTable(columnHeaders)
        newTable.fromXLSX(expectedXLSX, XLSXProperties(columnHeaders = false))
        assertEquals(baseTable.getTable(), newTable.getTable())
    }

    /**
     * Here, the values for the data remain the same, but instead of having the values as literal types in the first row, we are deriving them from some formulas.
     * This test is there to make sure that if a value is present as a formula, the parser is able to resolve the value and render it with a proper type.
     */
    @Test
    fun `Test fromXLSX() for values as a formula`() {
        val expectedXLSX = getResourceFilePathAsInputStream("/DataTableWithValuesAsFormula.xlsx")
        val newTable = DataTable(columnHeaders)
        newTable.fromXLSX(expectedXLSX)
        assertEquals(baseTable.getTable(), newTable.getTable())
    }

    @Test
    fun `Test fromXLSX() for invalid String parsing`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/xlsx_invalid_types/DataTableWithInvalidString.xlsx", rowIndex = 1, columnIndex = 0)
    }

    @Test
    fun `Test fromXLSX() for invalid Int parsing`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/xlsx_invalid_types/DataTableWithInvalidInt.xlsx", rowIndex = 1, columnIndex = 1)
    }

    @Test
    fun `Test fromXLSX() for invalid Decimal parsing`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/xlsx_invalid_types/DataTableWithInvalidDecimal.xlsx", rowIndex = 1, columnIndex = 2)
    }

    @Test
    fun `Test fromXLSX() for invalid Currency parsing`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/xlsx_invalid_types/DataTableWithInvalidCurrency.xlsx", rowIndex = 1, columnIndex = 3)
    }

    @Test
    fun `Test fromXLSX() for invalid Boolean parsing`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/xlsx_invalid_types/DataTableWithInvalidBoolean.xlsx", rowIndex = 1, columnIndex = 4)
    }

    @Test
    fun `Test fromXLSX() for invalid UUID parsing`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/xlsx_invalid_types/DataTableWithInvalidUUID.xlsx", rowIndex = 1, columnIndex = 5)
    }

    @Test
    fun `Test fromXLSX() for invalid Date parsing`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/xlsx_invalid_types/DataTableWithInvalidDate.xlsx", rowIndex = 1, columnIndex = 6)
    }

    @Test
    fun `Test fromXLSX() for invalid Timestamp parsing`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/xlsx_invalid_types/DataTableWithInvalidTimestamp.xlsx", rowIndex = 1, columnIndex = 7)
    }

    @Test
    fun `Test fromXLSX() with error in formula`() {
        testInvalidCellTypeForXLSXParsing(fileName = "/DataTableWithFormulaError.xlsx", rowIndex = 1, columnIndex = 7)
    }

    @Test
    fun `Test generateXLSXTemplate()`() {
        val expectedXLSX = getResourceFilePath("/ExpectedTemplateDataTableWithNonDefaultOptions.xlsx")
        val file = File("${expectedXLSX.parent}/GeneratedTemplateDateTable.xlsx")
        FileOutputStream(file).use(baseTable::generateXLSXTemplate)
        assertXLSXFile(expectedXLSX, file)
    }

    @Test
    fun `Test generateXLSXTemplate() with non-default options`() {
        val expectedXLSX = getResourceFilePath("/ExpectedTemplateDataTableWithNonDefaultOptions.xlsx")
        val file = File("${expectedXLSX.parent}/GeneratedTemplateDataTableWithNonDefaultOptions.xlsx")
        val columnHeaders =
            listOf(
                DataTable.ColumnType("name", DataTable.CellType.String()),
                DataTable.ColumnType("age", DataTable.CellType.Integer()),
                DataTable.ColumnType("weight", DataTable.CellType.Decimal()),
                DataTable.ColumnType("currency", DataTable.CellType.Currency()),
                DataTable.ColumnType("isEmployed", DataTable.CellType.Boolean()),
                DataTable.ColumnType("employeeId", DataTable.CellType.UUID()),
                DataTable.ColumnType(
                    "dob",
                    DataTable.CellType.Date(
                        DateFormatter(
                            xlsxFormat = XLSXFormat.Date(DayMonthYear),
                        ),
                    ),
                ),
                DataTable.ColumnType(
                    "recordCreatedAt",
                    DataTable.CellType.Timestamp(
                        TimestampFormatter(
                            xlsxFormat =
                                XLSXFormat.DateTime(
                                    DateTimeFormat(
                                        dateFormat = MonthDayYear,
                                        timeFormat = Hour12,
                                    ),
                                ),
                        ),
                    ),
                ),
            )
        val baseTable =
            DataTable(
                columnHeaders = columnHeaders,
                data = data,
            )
        FileOutputStream(file).use(baseTable::generateXLSXTemplate)
        assertXLSXFile(expectedXLSX, file)
    }

    private fun testInvalidCellTypeForXLSXParsing(
        fileName: String,
        rowIndex: Int,
        columnIndex: Int,
    ) {
        val fileToParse = getResourceFilePathAsInputStream(fileName)
        val newTable = DataTable(columnHeaders)
        try {
            newTable.fromXLSX(fileToParse)
        } catch (e: InvalidCellTypeException) {
            assertEquals(rowIndex, e.rowIndex)
            assertEquals(columnIndex, e.columnIndex)
        }
    }

    private fun assertXLSXFile(
        expectedFile: File,
        generatedFile: File,
    ) {
        val expectedXSSFWorkbook = XSSFWorkbook(expectedFile).getSheetAt(0)
        val generatedXSSFWorkbook = XSSFWorkbook(generatedFile).getSheetAt(0)
        expectedXSSFWorkbook.zip(generatedXSSFWorkbook).forEach { (expectedRow, generatedRow) ->
            expectedRow.zip(generatedRow).forEach { (expectedCell, generatedCell) ->
                assertEquals("$expectedCell", "$generatedCell")
            }
        }
    }
}
