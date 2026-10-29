package com.example.cashatk.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveUserData(firstName: String, lastName: String) {
        sharedPref.edit {
            putString("first_name", firstName)
            putString("last_name", lastName)
        }
    }

    fun getUserFullName(): String {
        val first = sharedPref.getString("first_name", "") ?: ""
        val last = sharedPref.getString("last_name", "") ?: ""
        return "$first $last".trim()
    }

    fun setPinCode(pin: String) {
        sharedPref.edit { putString("pin_code", pin) }
    }

    fun getPinCode(): String? {
        return sharedPref.getString("pin_code", null)
    }

    fun saveTotalExpenses(total: Float) {
        sharedPref.edit { putFloat("total_expenses", total) }
    }

    fun getTotalExpenses(): Float {
        return sharedPref.getFloat("total_expenses", 0.0f)
    }
}