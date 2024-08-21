package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

private val jsonParser =
    Json {
        isLenient = false
        prettyPrint = true
    }

internal fun parse(string: String): JsonObject = jsonParser.parseToJsonElement(string).jsonObject
