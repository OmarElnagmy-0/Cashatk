package com.example.cashatk.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceHelper(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("CashatkPrefs", Context.MODE_PRIVATE)

    fun setOnboardingCompleted(completed: Boolean) {
        sharedPref.edit { putBoolean("is_onboarding_completed", completed) }
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPref.getBoolean("is_onboarding_completed", false)
    }

    fun saveUserName(firstName: String, lastName: String) {
        sharedPref.edit {
            putString("first_name", firstName)
                .putString("last_name", lastName)
        }
    }

    fun getUserName(): String {
        val first = sharedPref.getString("first_name", "") ?: ""
        val last = sharedPref.getString("last_name", "") ?: ""
        return "$first $last".trim()
    }

    fun saveProfileImageUri(uri: String) {
        sharedPref.edit { putString("profile_image_uri", uri) }
    }

    fun getProfileImageUri(): String? {
        return sharedPref.getString("profile_image_uri", null)
    }

    fun clearProfileImageUri() {
        sharedPref.edit { remove("profile_image_uri") }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        sharedPref.edit { putBoolean("is_app_lock_enabled", enabled) }
    }

    fun isAppLockEnabled(): Boolean {
        return sharedPref.getBoolean("is_app_lock_enabled", false)
    }
}
