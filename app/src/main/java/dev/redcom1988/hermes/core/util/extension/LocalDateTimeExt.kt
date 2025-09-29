package dev.redcom1988.hermes.core.util.extension

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toLocalDateTime(pattern: String = "yyyy-MM-dd HH:mm:ss"): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return LocalDateTime.parse(this, formatter)
}

fun LocalDateTime.formatToString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}

fun formattedNow(): String {
    return LocalDateTime.now().formatToString()
}

fun String.toLocalDate(pattern: String = "yyyy-MM-dd"): LocalDate {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return LocalDate.parse(this, formatter)
}

fun LocalDate.formatToString(pattern: String = "yyyy-MM-dd"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}