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

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPref.edit { putBoolean("is_logged_in", isLoggedIn) }
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean("is_logged_in", false)
    }

    fun savePinCode(pin: String) {
        sharedPref.edit { putString("pin_code", pin) }
    }

    fun getPinCode(): String? {
        return sharedPref.getString("pin_code", null)
    }

    fun hasPinCode(): Boolean {
        return !getPinCode().isNullOrEmpty()
    }
}