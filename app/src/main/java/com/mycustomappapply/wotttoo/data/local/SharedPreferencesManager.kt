package com.mycustomappapply.wotttoo.data.local

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Save data to SharedPreferences
    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Retrieve data from SharedPreferences
    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    // Remove data from SharedPreferences
    fun removeData(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    // Clear all data from SharedPreferences
    fun clearAllData() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}