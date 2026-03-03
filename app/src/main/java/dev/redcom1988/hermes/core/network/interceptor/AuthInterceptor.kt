package dev.redcom1988.hermes.core.network.interceptor

import android.util.Log
import dev.redcom1988.hermes.core.preference.PreferenceStore
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds Authorization Bearer token to all API requests
 */
class AuthInterceptor(
    private val preferenceStore: PreferenceStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = preferenceStore.getString("auth_token", "").get()

        Log.d("AuthInterceptor", "URL: ${originalRequest.url}")
        Log.d("AuthInterceptor", "Token from preferences: ${if (token.isEmpty()) "EMPTY" else token.take(20) + "..."}")

        // If no token or empty token, proceed without auth header
        if (token.isEmpty()) {
            Log.w("AuthInterceptor", "No token found, proceeding without auth header")
            return chain.proceed(originalRequest)
        }

        // Add Authorization header with Bearer token
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        Log.d("AuthInterceptor", "Added Authorization header")
        return chain.proceed(authenticatedRequest)
    }
}
