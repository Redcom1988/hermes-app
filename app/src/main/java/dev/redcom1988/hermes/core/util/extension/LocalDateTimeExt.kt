package dev.redcom1988.hermes.core.util.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toLocalDateTime(pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return LocalDateTime.parse(this, formatter)
}

fun LocalDateTime.formatToString(pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}