package com.suryadigital.leo.jooq

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy.Mode
import org.jooq.codegen.GeneratorStrategy.Mode.DAO
import org.jooq.codegen.GeneratorStrategy.Mode.DEFAULT
import org.jooq.codegen.GeneratorStrategy.Mode.DOMAIN
import org.jooq.codegen.GeneratorStrategy.Mode.ENUM
import org.jooq.codegen.GeneratorStrategy.Mode.INTERFACE
import org.jooq.codegen.GeneratorStrategy.Mode.PATH
import org.jooq.codegen.GeneratorStrategy.Mode.POJO
import org.jooq.codegen.GeneratorStrategy.Mode.RECORD
import org.jooq.codegen.GeneratorStrategy.Mode.SYNTHETIC_DAO
import org.jooq.meta.Definition
import org.jooq.meta.ForeignKeyDefinition

/**
 * This class is used to define the generator strategy while generating JOOQ code.
 * When defining the generator, we can customize how the casing for the generated table POJOs should be done.
 * This class is used to generate table names in UpperCamelCase (PascalCase), and field name in camelCase.
 *
 * Starting from jOOQ 3.19+, implicit JOINs to many was introduced.
 * As a result, self-referential foreign keys break in the generated code.
 * Thus, `isImplicitJoinPathsToMany` is set to `false` in generator configuration to overcome this issue.
 *
 * An example snippet of using this generator strategy in your jooq gradle:
 * ```
 * configuration {
 *     jdbc {
 *         driver = "org.postgresql.Driver"
 *         url = "jdbc:postgresql://$dbHostName:$dbPortNumber/$dbName"
 *         user = dbUser
 *         password = dbPassword
 *     }
 *     generator {
 *         generate {
 *             isImplicitJoinPathsToMany = false
 *             withRoutines(false)
 *         }
 *         name = "org.jooq.codegen.KotlinGenerator"
 *         database {
 *             inputSchema = "public"
 *         }
 *         strategy {
 *             name = "com.suryadigital.leo.jooq.CamelCaseNameGeneratorStrategy"
 *         }
 *         target {
 *             packageName = "com.company.project.jooq"
 *             directory = "${projectDir}/src"
 *             withClean(true)
 *         }
 *     }
 * }
```
 */
@Suppress("Unused") // This suppression is required as this class is used by jooq internally as a strategy, and the input for jooq is the fully qualified name of this class as a string.
class CamelCaseNameGeneratorStrategy : DefaultGeneratorStrategy() {
    /**
     * Foreign key name generation strategy is overridden when the custom naming strategy is defined, resulting in the generation of duplicate foreign keys with the same name.
     *
     * For example, for the below schema:
     * ```
     * CREATE TABLE public."DealRequest" (
     * 	id uuid NOT NULL,
     *     ...
     * );
     * CREATE TABLE public."DealHistory" (
     *     id uuid NOT NULL,
     *     ...
     *     CONSTRAINT fk_deal_request_id FOREIGN KEY ("dealRequestId") REFERENCES public."DealRequest"(id)
     * );
     * CREATE TABLE public."EQDealRequest" (
     *     id uuid NOT NULL,
     *     ...
     *     CONSTRAINT fk_deal_request_id FOREIGN KEY ("dealRequestId") REFERENCES public."DealRequest"(id)
     * );
     *```
     * Without overriding [ForeignKeyDefinition] name generation, the code generated in `Keys.kt` is as follows:
     * ```
     *  val fk_deal_request_id: ForeignKey<DealHistory, DealRequest> = ...
     *  ...
     *  val fk_deal_request_id: ForeignKey<DealHistory, DealRequest> = ...
     *  ```
     * The generated code above is incorrect due to duplicate variable names.
     *
     * Overriding [ForeignKeyDefinition] name generation with the below strategy, the code generated in `Keys.kt` is as follows:
     * ```
     * val DealHistory__fk_deal_request_id: ForeignKey<DealHistoryRecord, DealRequestRecord> = ...
     * ...
     * val EQDealRequest__fk_deal_request_id: ForeignKey<EQDealRequestRecord, DealRequestRecord> = ...
     * ```
     */
    override fun getJavaIdentifier(definition: Definition): String {
        return when (definition) {
            is ForeignKeyDefinition -> {
                definition.table.outputName + "__" + definition.outputName
            }
            else -> {
                return definition.outputName
            }
        }
    }

    /**
     * The generated code for classes having [Mode.RECORD] does not follow PascalCase naming convention.
     * For example, for the below schema:
     * ```
     * CREATE TABLE public."DealRequest" (
     * 	id uuid NOT NULL,
     *     ...
     * );
     *```
     * Without overriding class name generation strategy, class name generated in `jooq.tables.records` is `DealrequestRecord`.
     *
     * Overriding class name generation strategy, class name generated in `jooq.tables.records` `DealRequestRecord`.
     */
    override fun getJavaClassName(
        definition: Definition,
        mode: Mode?,
    ): String {
        return buildString {
            append(definition.outputName)
            when (mode) {
                RECORD -> {
                    append("Record")
                }
                DAO -> {
                    append("Dao")
                }
                INTERFACE -> {
                    insert(0, "I")
                }
                PATH -> {
                    append("Path")
                }
                DEFAULT, POJO, SYNTHETIC_DAO, ENUM, DOMAIN, null -> {}
            }
        }
    }

    /**
     * The generated jooq code breaks for the schema where there exists a foreign key with
     * the same name as Table.
     * This breaks only when camelCase naming scheme is used.
     * For example, for the below schema:
     * ```
     * CREATE TABLE "LLT" (
     *  ...
     * );
     * CREATE TABLE "SLT" (
     *  ...
     *  "llt" VARCHAR(255) NOT NULL,
     *  CONSTRAINT fk_llt FOREIGN KEY ("llt") REFERENCES "LLT" ("llt")
     * );
     * ```
     * The generated jooq code in `com.suryadigital.training.jooq.tables.SLT` has conflicting declarations.
     * ```
     * open class SLT(...): TableImpl<SLTRecord>(...) {
     *  ...
     *  val llt: TableField<SLTRecord, String?> = ...
     *  ...
     *  val llt: LLTPath = ...
     *  ...
     * }
     * ```
     *
     * By overriding the method name generation strategy, the implicit join path kotlin properties for [ForeignKeyDefinition]
     * are suffixed with `Fk`.
     * ```
     * open class SLT(...): TableImpl<SLTRecord>(...) {
     *  ...
     *  val llt: TableField<SLTRecord, String?> = ...
     *  ...
     *  val lltFk: LLTPath = ...
     *  ...
     * }
     * ```
     */
    override fun getJavaMethodName(
        definition: Definition,
        mode: Mode,
    ): String {
        return super.getJavaMethodName(definition, mode) +
            when (definition) {
                is ForeignKeyDefinition -> "Fk"
                else -> ""
            }
    }
}
