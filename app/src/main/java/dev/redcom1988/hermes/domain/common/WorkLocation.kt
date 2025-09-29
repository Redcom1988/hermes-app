package dev.redcom1988.hermes.domain.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WorkLocation(val label: String) {
    @SerialName("office")
    OFFICE("Office"),
    @SerialName("anywhere")
    ANYWHERE("Anywhere");

    companion object {
        fun fromNameOrLabel(input: String): WorkLocation {
            val normalized = input.trim().lowercase()

            return entries.firstOrNull {
                it.name.equals(normalized, ignoreCase = true) || it.label.equals(normalized, ignoreCase = true)
            } ?: OFFICE
        }
    }
}
