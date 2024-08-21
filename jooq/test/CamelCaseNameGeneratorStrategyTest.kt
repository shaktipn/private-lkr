package com.suryadigital.leo.jooq

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class CamelCaseNameGeneratorStrategyTest : AbstractTest() {
    @Test
    fun testTwoTablesWithSameForeignKeyConstraintNameAreNamespacesByTableName() {
        assertNotNull(this::class.java.getResource("/com/suryadigital/leo/jooq/generatedCode/keys/Keys.kt"))
            .readText()
            .run {
                assertTrue(this.contains("val DealHistory__fk_dealid"))
                assertTrue(this.contains("val DealRequest__fk_dealid"))
            }
    }

    @Test
    fun testTableRecordNameInCamelCaseIsPreserved() {
        assertNotNull(
            this::class.java.getResource("/com/suryadigital/leo/jooq/generatedCode/tables/records/DealHistoryRecord.kt"),
        )
        assertNotNull(
            this::class.java.getResource("/com/suryadigital/leo/jooq/generatedCode/tables/records/DealRequestRecord.kt"),
        )
    }

    @Test
    fun testMethodNameSuffixedWithFk() {
        assertNotNull(this::class.java.getResource("/com/suryadigital/leo/jooq/generatedCode/tables/DealHistory.kt"))
            .readText()
            .run {
                assertTrue(this.contains("fun dealFk()"))
                assertTrue(this.contains("fun dealrequestFk()"))
            }
    }

    @Test
    fun testTableReferencesAreInPascalCase() {
        assertNotNull(this::class.java.getResource("/com/suryadigital/leo/jooq/generatedCode/tables/references/Tables.kt"))
            .readText()
            .run {
                assertTrue(this.contains("val Deal"))
                assertTrue(this.contains("val DealHistory"))
                assertTrue(this.contains("val DealRequest"))
            }
    }

    @Test
    fun testFieldsInCamelCase() {
        assertNotNull(this::class.java.getResource("/com/suryadigital/leo/jooq/generatedCode/tables/DealHistory.kt"))
            .readText()
            .run {
                assertTrue(this.contains("val histId"))
                assertTrue(this.contains("val lastUpdate"))
                assertTrue(this.contains("val dealDate"))
                assertTrue(this.contains("val dealId"))
            }
        assertNotNull(this::class.java.getResource("/com/suryadigital/leo/jooq/generatedCode/tables/Deal.kt"))
            .readText()
            .run {
                assertTrue(this.contains("val dealName"))
                assertTrue(this.contains("val amount"))
                assertTrue(this.contains("val dealId"))
            }
        assertNotNull(this::class.java.getResource("/com/suryadigital/leo/jooq/generatedCode/tables/DealRequest.kt"))
            .readText()
            .run {
                assertTrue(this.contains("val expiresAt"))
                assertTrue(this.contains("val dealDate"))
                assertTrue(this.contains("val dealId"))
                assertTrue(this.contains("val reqId"))
            }
    }
}
