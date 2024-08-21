package com.suryadigital.leo.tableRuntime

import java.io.File
import java.io.InputStream
import java.time.Instant
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

internal abstract class AbstractTest {
    val time: Instant = Instant.EPOCH
    val rowWithInvalidSize =
        listOf("John Smith", 32, 35.6, Currency.getInstance("INR"), false, UUID.fromString("8ec57ebd-38a5-403d-9feb-9b7d99426834"), LocalDate.of(1993, 2, 4))
    val rowWithValidSize =
        listOf("John Smith", 32, 35.6, Currency.getInstance("INR"), false, UUID.fromString("8ec57ebd-38a5-403d-9feb-9b7d99426834"), LocalDate.of(1993, 2, 4), Instant.now())
    val data =
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
        )
    val columnHeaders =
        listOf(
            DataTable.ColumnType("name", DataTable.CellType.String()),
            DataTable.ColumnType("age", DataTable.CellType.Integer()),
            DataTable.ColumnType("weight", DataTable.CellType.Decimal()),
            DataTable.ColumnType("currency", DataTable.CellType.Currency()),
            DataTable.ColumnType("isEmployed", DataTable.CellType.Boolean()),
            DataTable.ColumnType("employeeId", DataTable.CellType.UUID()),
            DataTable.ColumnType("dob", DataTable.CellType.Date()),
            DataTable.ColumnType("recordCreatedAt", DataTable.CellType.Timestamp()),
        )
    val baseTable =
        DataTable(
            columnHeaders = columnHeaders,
            data = data,
        )
    val baseTableWithNullableColumns =
        DataTable(
            columnHeaders = columnHeaders.map { DataTable.ColumnType(name = it.name, type = it.type, optional = true) },
            data = data,
        )

    companion object {
        fun getResourceFilePath(pathString: String): File {
            return File(
                Companion::class.java.getResource(pathString)?.file
                    ?: throw IllegalStateException("File not found in testresources folder. Please make sure that the file exists."),
            )
        }

        fun getResourceFilePathAsInputStream(pathString: String): InputStream {
            return Companion::class.java.getResourceAsStream(pathString)
                ?: throw IllegalStateException("File not found in testresources folder. Please make sure that the file exists.")
        }
    }
}
