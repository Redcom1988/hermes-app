package dev.redcom1988.hermes.core.auth

import androidx.compose.runtime.key
import dev.redcom1988.hermes.core.preference.Preference
import dev.redcom1988.hermes.core.preference.PreferenceStore

class AuthPreference (
    private val preferenceStore: PreferenceStore
) {
    fun userId(): Preference<Int> = preferenceStore.getInt(
        key = "auth_user_id",
        defaultValue = -1,
    )

    fun userEmail(): Preference<String> = preferenceStore.getString(
        key = "auth_user_email",
        defaultValue = ""
    )

    fun role(): Preference<String> = preferenceStore.getString(
        key = "auth_user_role",
        defaultValue = ""
    )

    fun authToken(): Preference<String> = preferenceStore.getString(
        key = "auth_token",
        defaultValue = ""
    )

    fun tokenExpiresAt(): Preference<Long> = preferenceStore.getLong(
        key = "auth_token_expires_at",
        defaultValue = 0L
    )

    fun isLoggedIn(): Preference<Boolean> = preferenceStore.getBoolean(
        key = "auth_is_logged_in",
        defaultValue = false
    )

    fun lastLoginAt(): Preference<String> = preferenceStore.getString(
        key = "auth_last_login_at",
        defaultValue = ""
    )

    fun isTokenValid(): Boolean {
        return isLoggedIn().get() && System.currentTimeMillis() < tokenExpiresAt().get()
    }

    fun saveLoginData(
        userId: Int,
        userEmail: String,
        role: String,
        authToken: String,
        tokenExpiresAt: Long,
        lastLoginAt: String
    ) {
        this.userId().set(userId)
        this.userEmail().set(userEmail)
        this.role().set(role)
        this.authToken().set(authToken)
        this.tokenExpiresAt().set(tokenExpiresAt)
        this.isLoggedIn().set(true)
        this.lastLoginAt().set(lastLoginAt)
    }

    fun clearLoginData() {
        userId().set(-1)
        userEmail().set("")
        role().set("")
        authToken().set("")
        tokenExpiresAt().set(0L)
        isLoggedIn().set(false)
        lastLoginAt().set("")
    }

}