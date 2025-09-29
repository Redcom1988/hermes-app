package dev.redcom1988.hermes.data.local.auth

import androidx.compose.runtime.key
import dev.redcom1988.hermes.core.preference.Preference
import dev.redcom1988.hermes.core.preference.PreferenceStore
import dev.redcom1988.hermes.domain.account_data.enums.DivisionType
import dev.redcom1988.hermes.domain.account_data.enums.UserRole

class UserPreference(
    private val preferenceStore: PreferenceStore
) {
    fun userRole(): Preference<String> = preferenceStore.getString(
        key = "user_role",
        defaultValue = ""
    )

    fun userName(): Preference<String> = preferenceStore.getString(
        key = "user_name",
        defaultValue = ""
    )

    fun getUserRole(): UserRole? {
        return UserRole.Companion.fromLabel(userRole().get())
    }

    fun employeeId(): Preference<Int> = preferenceStore.getInt(
        key = "employee_id",
        defaultValue = -1
    )

    fun divisionType(): Preference<String> = preferenceStore.getString(
        key = "division_type",
        defaultValue = ""
    )

    fun getDivisionType(): DivisionType? {
        return DivisionType.Companion.fromLabel(divisionType().get())
    }

    fun lastSyncTime(): Preference<String> = preferenceStore.getString(
        key = "last_sync_time",
        defaultValue = ""
    )

    fun saveUserData(
        name: String,
        role: String,
        employeeId: Int,
        divisionType: String,
    ) {
        this.userName().set(name)
        this.userRole().set(role)
        this.employeeId().set(employeeId)
        this.divisionType().set(divisionType)
    }

    fun clearUserData() {
        listOf(
            userName(),
            userRole(),
            employeeId(),
            divisionType(),
            lastSyncTime()
        ).forEach { it.delete() }
    }
}