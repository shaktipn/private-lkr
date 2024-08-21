package com.suryadigital.leo.ktor

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.ApplicationConfig
import java.io.File

// Copied & modified from https://github.com/ktorio/ktor/blob/762ed1dc777683cf69078b6a721d1192d4ede1a1/ktor-server/ktor-server-host-common/jvm/src/io/ktor/server/engine/CommandLine.kt#L18:L33

/**
 * Parses Ktor configuration from command line arguments.
 *
 * This is needed because Ktor exposes configuration through an interface called [ApplicationConfig].
 * This interface is very minimal, and does not provide a great API to safely read typed configurations.
 *
 * Internally, though, Ktor uses [Config], which does provide a fantastic API. Sadly, Ktor does not expose [Config] to
 * us publicly. This function helps fix that problem.
 *
 * @param args command line arguments.
 *
 * @return [Config] configured in [args].
 */
fun parseConfig(args: Array<String>): Config {
    val argsMap = args.mapNotNull { it.splitPair('=') }.toMap()

    val configFile = argsMap["-config"]?.let(::File)
    val commandLineMap = argsMap.filterKeys { it.startsWith("-P:") }.mapKeys { it.key.removePrefix("-P:") }

    val environmentConfig = ConfigFactory.systemProperties().withOnlyPath("ktor")
    val fileConfig = configFile?.let(ConfigFactory::parseFile) ?: ConfigFactory.load()
    val argConfig = ConfigFactory.parseMap(commandLineMap, "Command-line options")
    return argConfig.withFallback(fileConfig).withFallback(environmentConfig).resolve()
}

private fun String.splitPair(ch: Char): Pair<String, String>? =
    indexOf(ch).let { idx ->
        when (idx) {
            -1 -> null
            else -> take(idx) to drop(idx + 1)
        }
    }
