package com.suryadigital.leo.tableRuntime

import com.suryadigital.leo.tableRuntime.exceptions.InvalidCellTypeException
import com.suryadigital.leo.tableRuntime.extensions.csv.CSVProperties
import com.suryadigital.leo.tableRuntime.extensions.csv.fromCSV
import com.suryadigital.leo.tableRuntime.extensions.csv.generateCSVTemplate
import com.suryadigital.leo.tableRuntime.extensions.csv.toCSV
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.Currency
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DataTableCSVTest : AbstractTest() {
    @Test
    fun `Test toCSV() for File`() {
        val expectedCsv = getResourceFilePath("/ExpectedDataTable.csv")
        val file = File("${expectedCsv.parent}/GeneratedDataTable.csv")
        baseTable.toCSV(FileOutputStream(file))
        assertEquals(expectedCsv.readText(), file.readText())
    }

    @Test
    fun `Test toCSV() for OutputStream`() {
        val expectedCsv = getResourceFilePath("/ExpectedDataTable.csv")
        val file = File("${expectedCsv.parent}/GeneratedDataTable.csv")
        FileOutputStream(file).use(baseTable::toCSV)
        assertEquals(expectedCsv.readText(), file.readText())
    }

    @Test
    fun `Test toCSV() with no column headers()`() {
        val expectedCsv = getResourceFilePath("/ExpectedDataTableWithoutColumnHeaders.csv")
        val file = File("${expectedCsv.parent}/GeneratedDataTableWithoutColumnHeaders.csv")
        baseTable.toCSV(FileOutputStream(file), CSVProperties(columnHeaders = false))
        assertEquals(expectedCsv.readText(), file.readText())
    }

    @Test
    fun `Test toCSV() with non-default options`() {
        val expectedCsv = getResourceFilePath("/ExpectedDataTableWithNonDefaultOptions.csv")
        val file = File("${expectedCsv.parent}/GeneratedDataTableWithNonDefaultOptions.csv")
        baseTable.copy().toCSV(FileOutputStream(file), csvProperties = CSVProperties(delimiter = '|', quoteCharacter = '\'', lineSeparator = "|\n"))
        assertEquals(expectedCsv.readText(), file.readText())
    }

    @Test
    fun `Test fromCSV() for File`() {
        val fileToParse = getResourceFilePath("/ExpectedDataTable.csv")
        val newTable = DataTable(columnHeaders)
        newTable.fromCSV(FileInputStream(fileToParse))
        assertEquals(baseTable.getTable(), newTable.getTable())
    }

    @Test
    fun `Test fromCSV() for File with empty lines`() {
        val fileToParse = getResourceFilePath("/DataTableWithEmptyLines.csv")
        val newTable = DataTable(columnHeaders)
        newTable.fromCSV(FileInputStream(fileToParse))
        assertEquals(baseTable.getTable(), newTable.getTable())
    }

    @Test
    fun `Test fromCSV() with blank cells`() {
        val expectedXLSX = getResourceFilePathAsInputStream("/DataTableWithBlankCells.csv")
        val nullableColumnHeaders = columnHeaders.map { DataTable.ColumnType(it.name, it.type, true) }
        val newTable = DataTable(nullableColumnHeaders)
        newTable.fromCSV(expectedXLSX)
        val data =
            listOf(
                listOf(null, 32, 65.2, Currency.getInstance("USD"), true, UUID.fromString("601a13cf-eaa4-48f3-9e88-c92ecc386dfe"), LocalDate.of(1989, 7, 20), null),
                listOf("Bob Johnson", null, 78.9, Currency.getInstance("EUR"), false, UUID.fromString("c0b75f54-c6df-4331-a981-0378b14f79d3"), LocalDate.of(1977, 3, 12), null),
                listOf("Charlie Brown", 28, null, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), null),
                listOf("Diana Miller", 38, 55.5, null, true, UUID.fromString("c1bb2e07-2332-4b74-b9ad-3703460dd3ec"), LocalDate.of(1984, 9, 18), null),
                listOf("Edward Davis", 50, 82.3, Currency.getInstance("AUD"), null, UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), LocalDate.of(1972, 1, 30), null),
                listOf("Fiona White", 27, 68.7, Currency.getInstance("JPY"), true, null, LocalDate.of(1995, 4, 8), null),
                listOf("George Smith", 33, 75.6, Currency.getInstance("CHF"), false, UUID.fromString("38e3286b-b442-4208-995f-d45280ea1fc1"), null, null),
                listOf("Helen Brown", 42, 60.8, Currency.getInstance("INR"), true, UUID.fromString("f8324d58-75d7-4987-ae1e-28f1bbaa8f92"), LocalDate.of(1979, 10, 22), null),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), null),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), null),
            )
        assertEquals(DataTable(nullableColumnHeaders, data).getTable(), newTable.getTable())
    }

    @Test
    fun `Test fromCSV() with blank cells for non nullable columns`() {
        testInvalidCellTypeForCSVParsing(
            fileName = "/DataTableWithBlankCells.csv",
            rowIndex = 0,
            columnIndex = 0,
        )
    }

    @Test
    fun `Test fromCSV() for FileInputStream`() {
        val fileToParse = getResourceFilePathAsInputStream("/ExpectedDataTable.csv")
        val newTable = DataTable(columnHeaders)
        newTable.fromCSV(fileToParse)
        assertEquals(baseTable.getTable(), newTable.getTable())
    }

    @Test
    fun `Test fromCSV() with invalid Int`() {
        testInvalidCellTypeForCSVParsing(
            fileName = "/csv_invalid_types/DataTableWithInvalidInt.csv",
            rowIndex = 0,
            columnIndex = 1,
        )
    }

    @Test
    fun `Test fromCSV() with invalid Decimal`() {
        testInvalidCellTypeForCSVParsing(
            fileName = "/csv_invalid_types/DataTableWithInvalidDecimal.csv",
            rowIndex = 0,
            columnIndex = 2,
        )
    }

    @Test
    fun `Test fromCSV() with invalid Currency`() {
        testInvalidCellTypeForCSVParsing(
            fileName = "/csv_invalid_types/DataTableWithInvalidCurrency.csv",
            rowIndex = 0,
            columnIndex = 3,
        )
    }

    @Test
    fun `Test fromCSV() with invalid Boolean`() {
        testInvalidCellTypeForCSVParsing(
            fileName = "/csv_invalid_types/DataTableWithInvalidBoolean.csv",
            rowIndex = 0,
            columnIndex = 4,
        )
    }

    @Test
    fun `Test fromCSV() with invalid UUID`() {
        testInvalidCellTypeForCSVParsing(
            fileName = "/csv_invalid_types/DataTableWithInvalidUUID.csv",
            rowIndex = 0,
            columnIndex = 5,
        )
    }

    @Test
    fun `Test fromCSV() with invalid Date`() {
        testInvalidCellTypeForCSVParsing(
            fileName = "/csv_invalid_types/DataTableWithInvalidDate.csv",
            rowIndex = 0,
            columnIndex = 6,
        )
    }

    @Test
    fun `Test fromCSV() with invalid Timestamp`() {
        testInvalidCellTypeForCSVParsing(
            fileName = "/csv_invalid_types/DataTableWithInvalidTimestamp.csv",
            rowIndex = 0,
            columnIndex = 7,
        )
    }

    private fun testInvalidCellTypeForCSVParsing(
        fileName: String,
        rowIndex: Int,
        columnIndex: Int,
    ) {
        val fileToParse = getResourceFilePath(fileName)
        val newTable = DataTable(columnHeaders)
        try {
            newTable.fromCSV(FileInputStream(fileToParse))
        } catch (e: InvalidCellTypeException) {
            assertEquals(rowIndex, e.rowIndex)
            assertEquals(columnIndex, e.columnIndex)
        }
    }

    @Test
    fun `Test fromCSV() for File without column headers`() {
        val fileToParse = getResourceFilePath("/ExpectedDataTableWithoutColumnHeaders.csv")
        val newTable = DataTable(columnHeaders)
        newTable.fromCSV(FileInputStream(fileToParse), CSVProperties(columnHeaders = false))
        assertEquals(baseTable.getTable(), newTable.getTable())
    }

    @Test
    fun `Test generateCSVTemplate() for OutputStream`() {
        val expectedCsv = getResourceFilePath("/ExpectedTemplateDataTable.csv")
        val file = File("${expectedCsv.parent}/TemplateDataTable.csv")
        FileOutputStream(file).use(baseTable::generateCSVTemplate)
        assertEquals(expectedCsv.readText(), file.readText())
    }

    @Test
    fun `Test generateCSVTemplate() with non-default options`() {
        val expectedCsv = getResourceFilePath("/ExpectedTemplateDataTableWithNonDefaultOptions.csv")
        val file = File("${expectedCsv.parent}/TemplateDataTableWithNonDefaultOptions.csv")
        FileOutputStream(file).use { outputStream ->
            baseTable.generateCSVTemplate(
                outputStream = outputStream,
                csvProperties = CSVProperties(delimiter = '|', quoteCharacter = '\'', lineSeparator = "|\n"),
            )
        }
        assertEquals(expectedCsv.readText(), file.readText())
    }
}
