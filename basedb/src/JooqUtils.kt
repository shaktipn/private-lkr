package com.suryadigital.leo.basedb

import org.jooq.Field
import org.jooq.Record
import org.jooq.ResultQuery
import org.jooq.exception.DataAccessException
import org.jooq.exception.TooManyRowsException

/**
 * Fetches exactly one record from a [ResultQuery].
 *
 * @param R result type.
 *
 * @return result of query.
 *
 * @throws [DBException] if no records are found, if more than one record is found, or if something went wrong in executing the query.
 */
@Throws(DBException::class)
fun <R : Record> ResultQuery<R>.fetchExactlyOne(): R {
    try {
        return fetchOne() ?: throw DBException("The query did not return any results")
    } catch (e: TooManyRowsException) {
        throw DBException(e)
    } catch (e: DataAccessException) {
        throw DBException(e)
    }
}

/**
 * Fetches either exactly one record, or returns null from a [ResultQuery].
 *
 * @param R result type.
 *
 * @return result of the query if one record exists, null if none exist.
 *
 * @throws [DBException] if more than one record is found, or if something went wrong in executing the query.
 */
@Throws(DBException::class)
fun <R : Record> ResultQuery<R>.fetchOneOrNone(): R? {
    try {
        return fetchOne()
    } catch (e: TooManyRowsException) {
        throw DBException(e)
    } catch (e: DataAccessException) {
        throw DBException(e)
    }
}

/**
 * Retrieves a value of a field from a [Record] and ensures that it is not null.
 *
 * @param T result type
 * @param field field to retrieve.
 *
 * @return field value.
 *
 * @throws [DBException] if field is not found in [Record], or if field is null.
 */
@Throws(DBException::class)
fun <T> Record.getNonNullValue(field: Field<T?>): T {
    try {
        return get(field) ?: throw DBException("Column value is null: ${field.name}")
    } catch (e: IllegalArgumentException) {
        throw DBException(e)
    }
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Int].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Int] type.
 */
@Throws(DBException::class)
fun Record.getIntOrNull(fieldName: String): Int? {
    try {
        val field = field(fieldName) ?: throw DBException("Column not found: $fieldName")
        if (field.getValue(this) == null) {
            return null
        }
        if (field.type == java.lang.Integer::class.java) {
            return get(fieldName, Int::class.java)
        }
        if (field.type == java.math.BigDecimal::class.java) {
            return get(fieldName, java.math.BigDecimal::class.java).intValueExact()
        } else {
            throw DBException("Column has an incorrect type: $fieldName ${field.type}")
        }
    } catch (e: IllegalArgumentException) {
        throw DBException(e)
    } catch (e: ArithmeticException) {
        throw DBException("Value exceeded Int range", e)
    }
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Long].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Long] type.
 */
@Throws(DBException::class)
fun Record.getLongOrNull(fieldName: String): Long? {
    try {
        val field = field(fieldName) ?: throw DBException("Column not found: $fieldName")
        if (field.getValue(this) == null) {
            return null
        }
        if (field.type == java.lang.Long::class.java) {
            return get(fieldName, Long::class.java)
        }
        if (field.type == java.math.BigDecimal::class.java) {
            return get(fieldName, java.math.BigDecimal::class.java).longValueExact()
        } else {
            throw DBException("Column has an incorrect type: $fieldName ${field.type}")
        }
    } catch (e: IllegalArgumentException) {
        throw DBException(e)
    } catch (e: ArithmeticException) {
        throw DBException("Value exceeded Long range", e)
    }
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Boolean].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Boolean] type.
 */
@Throws(DBException::class)
fun Record.getBooleanOrNull(fieldName: String): Boolean? {
    try {
        val field = field(fieldName) ?: throw DBException("Column not found: $fieldName")
        if (field.getValue(this) == null) {
            return null
        }
        if (field.type != java.lang.Boolean::class.java) {
            throw DBException("Column has an incorrect type: $fieldName ${field.type}")
        }
        return get(fieldName, Boolean::class.java)
    } catch (e: IllegalArgumentException) {
        throw DBException(e)
    }
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Float].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Float] type.
 */
@Throws(DBException::class)
fun Record.getFloatOrNull(fieldName: String): Float? {
    try {
        val field = field(fieldName) ?: throw DBException("Column not found: $fieldName")
        if (field.getValue(this) == null) {
            return null
        }
        if (field.type == java.lang.Float::class.java) {
            return get(fieldName, Float::class.java)
        }
        if (field.type == java.math.BigDecimal::class.java) {
            val result = get(fieldName, java.math.BigDecimal::class.java).toFloat()
            if (result.isInfinite()) {
                throw DBException("Value not in Float range")
            } else {
                return result
            }
        } else {
            throw DBException("Column has an incorrect type: $fieldName ${field.type}")
        }
    } catch (e: IllegalArgumentException) {
        throw DBException(e)
    }
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Double].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Double] type.
 */
@Throws(DBException::class)
fun Record.getDoubleOrNull(fieldName: String): Double? {
    try {
        val field = field(fieldName) ?: throw DBException("Column not found: $fieldName")
        if (field.getValue(this) == null) {
            return null
        }
        if (field.type == java.lang.Double::class.java) {
            return get(fieldName, Double::class.java)
        }
        if (field.type == java.math.BigDecimal::class.java) {
            val result = get(fieldName, java.math.BigDecimal::class.java).toDouble()
            if (result.isInfinite()) {
                throw DBException("Value not in Double range")
            } else {
                return result
            }
        } else {
            throw DBException("Column has an incorrect type: $fieldName ${field.type}")
        }
    } catch (e: IllegalArgumentException) {
        throw DBException(e)
    }
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [String].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [String] type.
 */
@Throws(DBException::class)
fun Record.getStringOrNull(fieldName: String): String? {
    try {
        val field = field(fieldName) ?: throw DBException("Column not found: $fieldName")
        if (field.getValue(this) == null) {
            return null
        }
        if (field.type != java.lang.String::class.java) {
            throw DBException("Column has an incorrect type: $fieldName ${field.type}")
        }
        return get(fieldName, String::class.java)
    } catch (e: IllegalArgumentException) {
        throw DBException(e)
    }
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Int].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Int] type.
*/
@Throws(DBException::class)
fun Record.getInt(fieldName: String): Int {
    return getIntOrNull(fieldName) ?: throw DBException("Column is null: $fieldName")
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Long].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Long] type.
 */
@Throws(DBException::class)
fun Record.getLong(fieldName: String): Long {
    return getLongOrNull(fieldName) ?: throw DBException("Column is null: $fieldName")
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Boolean].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Boolean] type.
 */
@Throws(DBException::class)
fun Record.getBoolean(fieldName: String): Boolean {
    return getBooleanOrNull(fieldName) ?: throw DBException("Column is null: $fieldName")
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Float].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Float] type.
 */
@Throws(DBException::class)
fun Record.getFloat(fieldName: String): Float {
    return getFloatOrNull(fieldName) ?: throw DBException("Column is null: $fieldName")
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [Double].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [Double] type.
 */
@Throws(DBException::class)
fun Record.getDouble(fieldName: String): Double {
    return getDoubleOrNull(fieldName) ?: throw DBException("Column is null: $fieldName")
}

/**
 * Retrieves a value of a fieldName from a [Record] and ensures that it is converted to [String].
 *
 * @param fieldName field name to retrieve.
 *
 * @return field value or null.
 *
 * @throws [DBException] if [fieldName] is not found in [Record] or if field value is not [String] type.
 */
@Throws(DBException::class)
fun Record.getString(fieldName: String): String {
    return getStringOrNull(fieldName) ?: throw DBException("Column is null: $fieldName")
}
