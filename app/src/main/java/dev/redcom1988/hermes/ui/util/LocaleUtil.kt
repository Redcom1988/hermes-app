package dev.redcom1988.hermes.ui.util

import java.util.Currency
import java.util.Locale

fun Currency.getLocale(): Locale {
    return Locale.getAvailableLocales()
        .firstOrNull { locale ->
            try {
                Currency.getInstance(locale) == this
            } catch (e: Exception) {
                false
            }
        } ?: Locale.getDefault()
}