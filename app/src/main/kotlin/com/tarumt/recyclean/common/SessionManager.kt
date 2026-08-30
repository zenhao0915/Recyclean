package com.tarumt.recyclean.common

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SessionManager {
    private const val PREF_NAME = "recyclean_user_session"
    private const val KEY_USERNAME = "saved_username"
    private const val KEY_PASSWORD = "saved_password"

    fun saveSession(context: Context, username: String, password: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
        }
    }

    fun getSavedCredentials(context: Context): Pair<String, String>? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = prefs.getString(KEY_USERNAME, null)
        val password = prefs.getString(KEY_PASSWORD, null)

        return if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            Pair(username, password)
        } else {
            null
        }
    }

    fun clearSession(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { clear() }
    }
}