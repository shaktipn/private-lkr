package com.suryadigital.leo.tableRuntime

import com.suryadigital.leo.tableRuntime.DataTable.CellType
import com.suryadigital.leo.tableRuntime.DataTable.CellType.Boolean.BooleanFormatter
import com.suryadigital.leo.tableRuntime.DataTable.CellType.Boolean.BooleanFormatter.Companion.BooleanType
import com.suryadigital.leo.tableRuntime.DataTable.CellType.Currency.CurrencyFormatter
import com.suryadigital.leo.tableRuntime.DataTable.CellType.Currency.CurrencyFormatter.Companion.FormatType
import com.suryadigital.leo.tableRuntime.DataTable.ColumnType
import com.suryadigital.leo.tableRuntime.exceptions.DataConversionException
import com.suryadigital.leo.tableRuntime.exceptions.InvalidCellTypeException
import com.suryadigital.leo.tableRuntime.exceptions.InvalidRowSizeException
import com.suryadigital.leo.tableRuntime.exceptions.MissingRowException
import java.lang.IllegalStateException
import java.time.Instant
import java.time.LocalDate
import java.util.Currency
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class DataTableTest : AbstractTest() {
    @Test
    fun `Test invalid type during object initialization`() {
        assertFailsWith(InvalidCellTypeException::class) {
            DataTable(
                columnHeaders =
                    listOf(
                        ColumnType("name", CellType.String()),
                        ColumnType("age", CellType.Integer()),
                    ),
                data =
                    listOf(
                        listOf("Alice Smith", "92"),
                    ),
            )
        }
    }

    @Test
    fun `Test DataTable with CellType Integer as Long`() {
        val dataTable =
            DataTable(
                columnHeaders =
                    listOf(
                        ColumnType(
                            name = "LongValue",
                            type = CellType.Integer(),
                        ),
                    ),
                data = listOf(listOf(10L)),
            )
        assertEquals(dataTable.getCellAsLong(0, 0), 10L)
    }

    @Test
    fun `Test DataTable to insert null value for non nullable columns`() {
        try {
            DataTable(
                columnHeaders =
                    listOf(
                        ColumnType(
                            name = "name",
                            type = CellType.String(),
                        ),
                    ),
                data = listOf(listOf(null)),
            )
        } catch (e: InvalidCellTypeException) {
            assertEquals(0, e.rowIndex)
            assertEquals(0, e.columnIndex)
        }
    }

    @Test
    fun `Test DataTable operations on object with no records`() {
        val dataTable = DataTable(columnHeaders = listOf())
        assert(dataTable.columnHeaders.isEmpty())
        assert(dataTable.getTable().isEmpty())
    }

    @Test
    fun `Test forEachRow()`() {
        baseTable.forEachRow {
            assert(this.isNotEmpty())
        }
    }

    @Test
    fun `Test forEachCell()`() {
        baseTable.forEachCell {
            assertNotNull(this)
        }
    }

    @Test
    fun `Test getTable()`() {
        assertEquals(data, baseTable.getTable())
    }

    @Test
    fun `Test setCell()`() {
        val currentTime = Instant.now()
        val changedData =
            listOf(
                listOf("John Smith", 32, 65.2, Currency.getInstance("USD"), true, UUID.fromString("601a13cf-eaa4-48f3-9e88-c92ecc386dfe"), LocalDate.of(1989, 7, 20), time),
                listOf("Bob Johnson", 22, 78.9, Currency.getInstance("EUR"), false, UUID.fromString("c0b75f54-c6df-4331-a981-0378b14f79d3"), LocalDate.of(1977, 3, 12), time),
                listOf("Charlie Brown", 28, 60.5, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Diana Miller", 38, 55.5, Currency.getInstance("CAD"), true, UUID.fromString("c1bb2e07-2332-4b74-b9ad-3703460dd3ec"), LocalDate.of(1984, 9, 18), time),
                listOf("Edward Davis", 50, 82.3, Currency.getInstance("AUD"), false, UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), LocalDate.of(1972, 1, 30), time),
                listOf("Fiona White", 27, 68.7, Currency.getInstance("JPY"), true, UUID.fromString("9f3ca552-a7ca-4c3b-9ac7-3f27849cd724"), LocalDate.of(1995, 4, 8), time),
                listOf("George Smith", 33, 75.6, Currency.getInstance("CHF"), false, UUID.fromString("38e3286b-b442-4208-995f-d45280ea1fc1"), LocalDate.of(1988, 6, 14), time),
                listOf("Helen Brown", 42, 60.8, Currency.getInstance("INR"), true, UUID.fromString("f8324d58-75d7-4987-ae1e-28f1bbaa8f92"), LocalDate.of(1979, 10, 22), currentTime),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf(null, 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
            )
        baseTableWithNullableColumns.setCell("John Smith", 0, 0)
        baseTableWithNullableColumns.setCell(22, 1, 1)
        baseTableWithNullableColumns.setCell(60.5, 2, 2)
        baseTableWithNullableColumns.setCell(Currency.getInstance("CAD"), 3, 3)
        baseTableWithNullableColumns.setCell(false, 4, 4)
        baseTableWithNullableColumns.setCell(UUID.fromString("9f3ca552-a7ca-4c3b-9ac7-3f27849cd724"), 5, 5)
        baseTableWithNullableColumns.setCell(LocalDate.of(1988, 6, 14), 6, 6)
        baseTableWithNullableColumns.setCell(currentTime, 7, 7)
        baseTableWithNullableColumns.setCell(null, 9, 0)
        assertEquals(changedData, baseTableWithNullableColumns.getTable())
    }

    @Test
    fun `Test setCell() type validation for InvalidInputTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) { baseTable.setCell(UUID.randomUUID(), 0, 0) }
        assertFailsWith(InvalidCellTypeException::class) { baseTable.setCell("Smith", 1, 1) }
        assertFailsWith(InvalidCellTypeException::class) { baseTable.setCell(UUID.randomUUID(), 2, 2) }
        assertFailsWith(InvalidCellTypeException::class) { baseTable.setCell("ABC", 3, 3) }
        assertFailsWith(InvalidCellTypeException::class) { baseTable.setCell("true", 4, 4) }
        assertFailsWith(InvalidCellTypeException::class) { baseTable.setCell("f8324d58-75d7-4987-ae1e-28f1bbaa8f92", 5, 5) }
        assertFailsWith(InvalidCellTypeException::class) { baseTable.setCell(Instant.now(), 6, 6) }
        assertFailsWith(InvalidCellTypeException::class) { baseTable.setCell(LocalDate.now(), 7, 7) }
    }

    @Test
    fun `Test getCell()`() {
        assertEquals("Jack Miller", baseTable.getCell(9, 0))
        assertEquals(29, baseTable.getCell(8, 1))
        assertEquals(60.8, baseTable.getCell(7, 2))
        assertEquals(Currency.getInstance("CHF"), baseTable.getCell(6, 3))
        assertEquals(true, baseTable.getCell(5, 4))
        assertEquals(UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), baseTable.getCell(4, 5))
        assertEquals(LocalDate.of(1984, 9, 18), baseTable.getCell(3, 6))
        assertEquals(time, baseTable.getCell(2, 7))
    }

    @Test
    fun `Test getCellAsString()`() {
        assertEquals("Jack Miller", baseTable.getCellAsString(9, 0))
    }

    @Test
    fun `Test getCellAsString() with value as null`() {
        baseTableWithNullableColumns.setCell(null, 9, 0)
        assertEquals(null, baseTableWithNullableColumns.getCellAsString(9, 0))
    }

    @Test
    fun `Test getCellAsString() with InvalidCellTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) {
            baseTable.getCellAsString(8, 1)
        }
    }

    @Test
    fun `Test getCellAsLong()`() {
        assertEquals(29, baseTable.getCellAsLong(8, 1))
    }

    @Test
    fun `Test getCellAsLong() with value as null`() {
        baseTableWithNullableColumns.setCell(null, 8, 1)
        assertEquals(null, baseTableWithNullableColumns.getCellAsLong(8, 1))
    }

    @Test
    fun `Test getCellAsLong() with InvalidCellTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) {
            baseTable.getCellAsLong(9, 0)
        }
    }

    @Test
    fun `Test getCellAsDouble()`() {
        assertEquals(60.8, baseTable.getCellAsDouble(7, 2))
    }

    @Test
    fun `Test getCellAsDouble() with value as null`() {
        baseTableWithNullableColumns.setCell(null, 7, 2)
        assertEquals(null, baseTableWithNullableColumns.getCellAsDouble(7, 2))
    }

    @Test
    fun `Test getCellAsDouble() with InvalidCellTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) {
            baseTable.getCellAsDouble(9, 0)
        }
    }

    @Test
    fun `Test getCellAsBoolean()`() {
        assertEquals(true, baseTable.getCellAsBoolean(5, 4))
    }

    @Test
    fun `Test getCellAsBoolean() with value as null`() {
        baseTableWithNullableColumns.setCell(null, 5, 4)
        assertEquals(null, baseTableWithNullableColumns.getCellAsBoolean(5, 4))
    }

    @Test
    fun `Test getCellAsBoolean() with InvalidCellTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) {
            baseTable.getCellAsBoolean(7, 2)
        }
    }

    @Test
    fun `Test getCellAsUUID()`() {
        assertEquals(UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), baseTable.getCellAsUUID(4, 5))
    }

    @Test
    fun `Test getCellAsUUID() with value as null`() {
        baseTableWithNullableColumns.setCell(null, 4, 5)
        assertEquals(null, baseTableWithNullableColumns.getCellAsUUID(4, 5))
    }

    @Test
    fun `Test getCellAsUUID() with InvalidCellTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) {
            baseTable.getCellAsUUID(7, 2)
        }
    }

    @Test
    fun `Test getCellAsLocalDate()`() {
        assertEquals(LocalDate.of(1984, 9, 18), baseTable.getCellAsLocalDate(3, 6))
    }

    @Test
    fun `Test getCellAsLocalDate() with value as null`() {
        baseTableWithNullableColumns.setCell(null, 3, 6)
        assertEquals(null, baseTableWithNullableColumns.getCellAsLocalDate(3, 6))
    }

    @Test
    fun `Test getCellAsLocalDate() with InvalidCellTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) {
            baseTable.getCellAsLocalDate(7, 2)
        }
    }

    @Test
    fun `Test getCellAsInstant()`() {
        assertEquals(time, baseTable.getCellAsInstant(2, 7))
    }

    @Test
    fun `Test getCellAsInstant() with value as null`() {
        baseTableWithNullableColumns.setCell(null, 2, 7)
        assertEquals(null, baseTableWithNullableColumns.getCellAsInstant(2, 7))
    }

    @Test
    fun `Test getCellAsInstant() with InvalidCellTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) {
            baseTable.getCellAsInstant(7, 2)
        }
    }

    @Test
    fun `Test getCellAsCurrency()`() {
        assertEquals(Currency.getInstance("USD"), baseTable.getCellAsCurrency(0, 3))
    }

    @Test
    fun `Test getCellAsCurrency() with value as null`() {
        baseTableWithNullableColumns.setCell(null, 0, 3)
        assertEquals(null, baseTableWithNullableColumns.getCellAsCurrency(0, 3))
    }

    @Test
    fun `Test getCellAsCurrency() with InvalidCellTypeException`() {
        assertFailsWith(InvalidCellTypeException::class) {
            baseTable.getCellAsCurrency(7, 2)
        }
    }

    @Test
    fun `Test setRow()`() {
        val changedData =
            listOf(
                listOf("Alice Smith", 32, 65.2, Currency.getInstance("USD"), true, UUID.fromString("601a13cf-eaa4-48f3-9e88-c92ecc386dfe"), LocalDate.of(1989, 7, 20), time),
                listOf("Bob Johnson", 45, 78.9, Currency.getInstance("EUR"), false, UUID.fromString("c0b75f54-c6df-4331-a981-0378b14f79d3"), LocalDate.of(1977, 3, 12), time),
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Diana Miller", 38, 55.5, Currency.getInstance("CAD"), true, UUID.fromString("c1bb2e07-2332-4b74-b9ad-3703460dd3ec"), LocalDate.of(1984, 9, 18), time),
                rowWithValidSize,
                listOf("Fiona White", 27, 68.7, Currency.getInstance("JPY"), true, UUID.fromString("9f3ca552-a7ca-4c3b-9ac7-3f27849cd724"), LocalDate.of(1995, 4, 8), time),
                listOf("George Smith", 33, 75.6, Currency.getInstance("CHF"), false, UUID.fromString("38e3286b-b442-4208-995f-d45280ea1fc1"), LocalDate.of(1988, 6, 14), time),
                listOf("Helen Brown", 42, 60.8, Currency.getInstance("INR"), true, UUID.fromString("f8324d58-75d7-4987-ae1e-28f1bbaa8f92"), LocalDate.of(1979, 10, 22), time),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
            )
        baseTable.setRow(rowWithValidSize, 4)
        assertEquals(changedData, baseTable.getTable())
    }

    @Test
    fun `Test setRow() row validations`() {
        assertFailsWith(InvalidRowSizeException::class) { baseTable.setRow(rowWithInvalidSize, 1) }
        assertFailsWith(MissingRowException::class) { baseTable.setRow(rowWithValidSize, 20) }
    }

    @Test
    fun `Test addRow() with index`() {
        val changedData =
            listOf(
                listOf("Alice Smith", 32, 65.2, Currency.getInstance("USD"), true, UUID.fromString("601a13cf-eaa4-48f3-9e88-c92ecc386dfe"), LocalDate.of(1989, 7, 20), time),
                listOf("Bob Johnson", 45, 78.9, Currency.getInstance("EUR"), false, UUID.fromString("c0b75f54-c6df-4331-a981-0378b14f79d3"), LocalDate.of(1977, 3, 12), time),
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Diana Miller", 38, 55.5, Currency.getInstance("CAD"), true, UUID.fromString("c1bb2e07-2332-4b74-b9ad-3703460dd3ec"), LocalDate.of(1984, 9, 18), time),
                rowWithValidSize,
                listOf("Edward Davis", 50, 82.3, Currency.getInstance("AUD"), false, UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), LocalDate.of(1972, 1, 30), time),
                listOf("Fiona White", 27, 68.7, Currency.getInstance("JPY"), true, UUID.fromString("9f3ca552-a7ca-4c3b-9ac7-3f27849cd724"), LocalDate.of(1995, 4, 8), time),
                listOf("George Smith", 33, 75.6, Currency.getInstance("CHF"), false, UUID.fromString("38e3286b-b442-4208-995f-d45280ea1fc1"), LocalDate.of(1988, 6, 14), time),
                listOf("Helen Brown", 42, 60.8, Currency.getInstance("INR"), true, UUID.fromString("f8324d58-75d7-4987-ae1e-28f1bbaa8f92"), LocalDate.of(1979, 10, 22), time),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
            )
        baseTable.addRow(rowWithValidSize, 4)
        assertEquals(changedData, baseTable.getTable())
    }

    @Test
    fun `Test addRow() without index`() {
        val changedData =
            listOf(
                listOf("Alice Smith", 32, 65.2, Currency.getInstance("USD"), true, UUID.fromString("601a13cf-eaa4-48f3-9e88-c92ecc386dfe"), LocalDate.of(1989, 7, 20), time),
                listOf("Bob Johnson", 45, 78.9, Currency.getInstance("EUR"), false, UUID.fromString("c0b75f54-c6df-4331-a981-0378b14f79d3"), LocalDate.of(1977, 3, 12), time),
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Diana Miller", 38, 55.5, Currency.getInstance("CAD"), true, UUID.fromString("c1bb2e07-2332-4b74-b9ad-3703460dd3ec"), LocalDate.of(1984, 9, 18), time),
                listOf("Edward Davis", 50, 82.3, Currency.getInstance("AUD"), false, UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), LocalDate.of(1972, 1, 30), time),
                listOf("Fiona White", 27, 68.7, Currency.getInstance("JPY"), true, UUID.fromString("9f3ca552-a7ca-4c3b-9ac7-3f27849cd724"), LocalDate.of(1995, 4, 8), time),
                listOf("George Smith", 33, 75.6, Currency.getInstance("CHF"), false, UUID.fromString("38e3286b-b442-4208-995f-d45280ea1fc1"), LocalDate.of(1988, 6, 14), time),
                listOf("Helen Brown", 42, 60.8, Currency.getInstance("INR"), true, UUID.fromString("f8324d58-75d7-4987-ae1e-28f1bbaa8f92"), LocalDate.of(1979, 10, 22), time),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
                rowWithValidSize,
            )
        baseTable.addRow(rowWithValidSize)
        assertEquals(changedData, baseTable.getTable())
    }

    @Test
    fun `Test addRow() row validations`() {
        try {
            baseTable.addRow(rowWithValidSize, 20)
        } catch (e: MissingRowException) {
            assertEquals(20, e.currentIndex)
            assertEquals(10, e.numberOfRows)
        }
        try {
            baseTable.addRow(rowWithInvalidSize, 10)
        } catch (e: InvalidRowSizeException) {
            assertEquals(rowWithInvalidSize.size, e.actualSize)
            assertEquals(rowWithValidSize.size, e.expectedSize)
        }
    }

    @Test
    fun `Test getRow()`() {
        val expectedRow =
            listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time)
        assertEquals(expectedRow, baseTable.getRow(2))
    }

    @Test
    fun `Test getRow() to have data copy instead of passing the pointer`() {
        val rowData = baseTable.getRow(0) as MutableList
        rowData[0] = "John Smith"
        assertEquals(baseTable, baseTable)
    }

    @Test
    fun `Test getRow() row validation`() {
        try {
            baseTable.getRow(20)
        } catch (e: MissingRowException) {
            assertEquals(20, e.currentIndex)
            assertEquals(10, e.numberOfRows)
        }
    }

    @Test
    fun `Test appendTableData()`() {
        val changedData =
            listOf(
                listOf("Alice Smith", 32, 65.2, Currency.getInstance("USD"), true, UUID.fromString("601a13cf-eaa4-48f3-9e88-c92ecc386dfe"), LocalDate.of(1989, 7, 20), time),
                listOf("Bob Johnson", 45, 78.9, Currency.getInstance("EUR"), false, UUID.fromString("c0b75f54-c6df-4331-a981-0378b14f79d3"), LocalDate.of(1977, 3, 12), time),
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Diana Miller", 38, 55.5, Currency.getInstance("CAD"), true, UUID.fromString("c1bb2e07-2332-4b74-b9ad-3703460dd3ec"), LocalDate.of(1984, 9, 18), time),
                listOf("Edward Davis", 50, 82.3, Currency.getInstance("AUD"), false, UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), LocalDate.of(1972, 1, 30), time),
                listOf("Fiona White", 27, 68.7, Currency.getInstance("JPY"), true, UUID.fromString("9f3ca552-a7ca-4c3b-9ac7-3f27849cd724"), LocalDate.of(1995, 4, 8), time),
                listOf("George Smith", 33, 75.6, Currency.getInstance("CHF"), false, UUID.fromString("38e3286b-b442-4208-995f-d45280ea1fc1"), LocalDate.of(1988, 6, 14), time),
                listOf("Helen Brown", 42, 60.8, Currency.getInstance("INR"), true, UUID.fromString("f8324d58-75d7-4987-ae1e-28f1bbaa8f92"), LocalDate.of(1979, 10, 22), time),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
                rowWithValidSize,
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
            )
        val dataToAppend =
            listOf(
                rowWithValidSize,
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
            )
        baseTable.appendTableData(dataToAppend)
        assertEquals(changedData, baseTable.getTable())
    }

    @Test
    fun `Test appendTableData() row validation`() {
        val dataToAppend =
            listOf(
                rowWithInvalidSize,
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
            )
        assertFailsWith(InvalidRowSizeException::class) { baseTable.appendTableData(dataToAppend) }
    }

    @Test
    fun `Test deleteRow()`() {
        val changedData =
            listOf(
                listOf("Alice Smith", 32, 65.2, Currency.getInstance("USD"), true, UUID.fromString("601a13cf-eaa4-48f3-9e88-c92ecc386dfe"), LocalDate.of(1989, 7, 20), time),
                listOf("Bob Johnson", 45, 78.9, Currency.getInstance("EUR"), false, UUID.fromString("c0b75f54-c6df-4331-a981-0378b14f79d3"), LocalDate.of(1977, 3, 12), time),
                listOf("Charlie Brown", 28, 70.0, Currency.getInstance("GBP"), true, UUID.fromString("939d576c-49e2-4d09-865f-4eb7f224c89d"), LocalDate.of(1993, 11, 5), time),
                listOf("Diana Miller", 38, 55.5, Currency.getInstance("CAD"), true, UUID.fromString("c1bb2e07-2332-4b74-b9ad-3703460dd3ec"), LocalDate.of(1984, 9, 18), time),
                listOf("Edward Davis", 50, 82.3, Currency.getInstance("AUD"), false, UUID.fromString("fedf4cbd-c36c-4ab9-99c9-d2176c793404"), LocalDate.of(1972, 1, 30), time),
                listOf("George Smith", 33, 75.6, Currency.getInstance("CHF"), false, UUID.fromString("38e3286b-b442-4208-995f-d45280ea1fc1"), LocalDate.of(1988, 6, 14), time),
                listOf("Helen Brown", 42, 60.8, Currency.getInstance("INR"), true, UUID.fromString("f8324d58-75d7-4987-ae1e-28f1bbaa8f92"), LocalDate.of(1979, 10, 22), time),
                listOf("Ivan Johnson", 29, 73.1, Currency.getInstance("CNY"), false, UUID.fromString("f8cd918e-3e96-4417-8322-22c5e9f98d5c"), LocalDate.of(1992, 8, 3), time),
                listOf("Jack Miller", 36, 77.4, Currency.getInstance("SGD"), true, UUID.fromString("6f8be127-131e-47cf-8140-f7254e5ee67b"), LocalDate.of(1985, 2, 17), time),
            )
        baseTable.deleteRow(5)
        assertEquals(changedData, baseTable.getTable())
    }

    @Test
    fun `Test deleteRow() row validation`() {
        assertFailsWith(MissingRowException::class) { baseTable.deleteRow(20) }
    }

    @Test
    fun `Test default cell formatters with getFormatterCell()`() {
        assertEquals("Alice Smith", baseTable.getFormattedCell(0, 0))
        assertEquals("45", baseTable.getFormattedCell(1, 1))
        assertEquals("70.0000", baseTable.getFormattedCell(2, 2))
        assertEquals("CAD", baseTable.getFormattedCell(3, 3))
        assertEquals("false", baseTable.getFormattedCell(4, 4))
        assertEquals("9f3ca552-a7ca-4c3b-9ac7-3f27849cd724", baseTable.getFormattedCell(5, 5))
        assertEquals("1988-06-14", baseTable.getFormattedCell(6, 6))
        assertEquals("1970-01-01T00:00:00Z", baseTable.getFormattedCell(7, 7))
        baseTableWithNullableColumns.setCell(null, 8, 0)
        assertEquals("", baseTableWithNullableColumns.getFormattedCell(8, 0))
    }

    @Test
    fun `Test negative for CurrencyFormatter of FormatType CODE`() {
        assertFailsWith<DataConversionException> {
            CurrencyFormatter(FormatType.CODE).fromString("ABC")
        }
    }

    @Test
    fun `Test CurrencyFormatter with SYMBOL format type`() {
        assertEquals("$", executeFormattedCurrency(FormatType.SYMBOL))
    }

    @Test
    fun `Test negative for CurrencyFormatter of FormatType SYMBOL`() {
        assertFailsWith<DataConversionException> {
            CurrencyFormatter(FormatType.SYMBOL).fromString("ABC")
        }
    }

    @Test
    fun `Test CurrencyFormatter with DISPLAY_NAME format type`() {
        assertEquals("US Dollar", executeFormattedCurrency(FormatType.DISPLAY_NAME))
    }

    @Test
    fun `Test negative for CurrencyFormatter of FormatType DISPLAY_NAME`() {
        assertFailsWith<DataConversionException> {
            CurrencyFormatter(FormatType.DISPLAY_NAME).fromString("ABC")
        }
    }

    @Test
    fun `Test CurrencyFormatter with NUMERIC_CODE format type`() {
        assertEquals("840", executeFormattedCurrency(FormatType.NUMERIC_CODE))
    }

    @Test
    fun `Test negative for CurrencyFormatter of FormatType NUMERIC_CODE`() {
        assertFailsWith<DataConversionException> {
            CurrencyFormatter(FormatType.NUMERIC_CODE).fromString("ABC")
        }
    }

    @Test
    fun `Test negative for DateFormatter toFormattedString`() {
        assertFailsWith<IllegalStateException> {
            CellType.Date.DateFormatter().toFormattedString(1)
        }
    }

    @Test
    fun `Test negative for TimestampFormatter toFormattedString`() {
        assertFailsWith<IllegalStateException> {
            CellType.Timestamp.TimestampFormatter().toFormattedString(1)
        }
    }

    @Test
    fun `Test negative for Boolean fromString`() {
        assertFailsWith<DataConversionException> {
            BooleanFormatter().fromString("INVALID_BOOL_REPRESENTATION")
        }
    }

    @Test
    fun `Test getFormattedRow()`() {
        val formattedRow = baseTable.getFormattedRow(0)
        assertEquals("Alice Smith", formattedRow[0])
        assertEquals("32", formattedRow[1])
        assertEquals("65.2000", formattedRow[2])
        assertEquals("USD", formattedRow[3])
        assertEquals("true", formattedRow[4])
        assertEquals("601a13cf-eaa4-48f3-9e88-c92ecc386dfe", formattedRow[5])
        assertEquals("1989-07-20", formattedRow[6])
        assertEquals("1970-01-01T00:00:00Z", formattedRow[7])
    }

    @Test
    fun `Test getFormattedRow() with null value`() {
        baseTableWithNullableColumns.setCell(null, 0, 0)
        val formattedRow = baseTableWithNullableColumns.getFormattedRow(0)
        assertEquals("", formattedRow[0])
    }

    @Test
    fun `Test numberOfRows`() {
        assertEquals(8, baseTable.numberOfColumns)
    }

    @Test
    fun `Test numberOfColumn`() {
        assertEquals(8, baseTable.numberOfColumns)
    }

    @Test
    fun `Test BooleanFormatter fromString for BINARY type boolean`() {
        assertTrue(CellType.Boolean(BooleanFormatter(BooleanType.BINARY)).formatter.fromString("1") as Boolean)
        assertFalse(CellType.Boolean(BooleanFormatter(BooleanType.BINARY)).formatter.fromString("0") as Boolean)
    }

    @Test
    fun `Test BooleanFormatter fromString for LITERAL_LOWERCASE type boolean`() {
        assertTrue(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_LOWERCASE)).formatter.fromString("true") as Boolean)
        assertFalse(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_LOWERCASE)).formatter.fromString("false") as Boolean)
    }

    @Test
    fun `Test BooleanFormatter fromString for LITERAL_UPPERCASE type boolean`() {
        assertTrue(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_UPPERCASE)).formatter.fromString("TRUE") as Boolean)
        assertFalse(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_UPPERCASE)).formatter.fromString("FALSE") as Boolean)
    }

    @Test
    fun `Test BooleanFormatter fromString for LITERAL_PASCALCASE type boolean`() {
        assertTrue(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_PASCALCASE)).formatter.fromString("True") as Boolean)
        assertFalse(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_PASCALCASE)).formatter.fromString("False") as Boolean)
    }

    @Test
    fun `Test BooleanFormatter fromString for UPPERCASE_CHARACTER type boolean`() {
        assertTrue(CellType.Boolean(BooleanFormatter(BooleanType.UPPERCASE_CHARACTER)).formatter.fromString("T") as Boolean)
        assertFalse(CellType.Boolean(BooleanFormatter(BooleanType.UPPERCASE_CHARACTER)).formatter.fromString("F") as Boolean)
    }

    @Test
    fun `Test BooleanFormatter fromString for LOWERCASE_CHARACTER type boolean`() {
        assertTrue(CellType.Boolean(BooleanFormatter(BooleanType.LOWERCASE_CHARACTER)).formatter.fromString("t") as Boolean)
        assertFalse(CellType.Boolean(BooleanFormatter(BooleanType.LOWERCASE_CHARACTER)).formatter.fromString("f") as Boolean)
    }

    @Test
    fun `Test ClassCastException in toFormattedString for CurrencyFormatter`() {
        assertFailsWith<DataConversionException> {
            CurrencyFormatter().toFormattedString("Not a currency type.")
        }
    }

    @Test
    fun `Test BooleanFormatter toFormattedString`() {
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.BINARY)).formatter.toFormattedString(false), "0")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.BINARY)).formatter.toFormattedString(true), "1")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_LOWERCASE)).formatter.toFormattedString(false), "false")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_LOWERCASE)).formatter.toFormattedString(true), "true")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_PASCALCASE)).formatter.toFormattedString(false), "False")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_PASCALCASE)).formatter.toFormattedString(true), "True")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_UPPERCASE)).formatter.toFormattedString(false), "FALSE")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.LITERAL_UPPERCASE)).formatter.toFormattedString(true), "TRUE")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.LOWERCASE_CHARACTER)).formatter.toFormattedString(false), "f")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.LOWERCASE_CHARACTER)).formatter.toFormattedString(true), "t")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.UPPERCASE_CHARACTER)).formatter.toFormattedString(false), "F")
        assertEquals(CellType.Boolean(BooleanFormatter(BooleanType.UPPERCASE_CHARACTER)).formatter.toFormattedString(true), "T")
    }

    private fun executeFormattedCurrency(
        currencyType: FormatType,
        currency: Currency = Currency.getInstance("USD"),
    ): String {
        return DataTable(
            columnHeaders =
                listOf(
                    ColumnType(
                        name = "currency",
                        type = CellType.Currency(CurrencyFormatter(currencyType)),
                    ),
                ),
            data =
                listOf(
                    listOf(
                        currency,
                    ),
                ),
        ).getFormattedCell(0, 0)
    }
}
