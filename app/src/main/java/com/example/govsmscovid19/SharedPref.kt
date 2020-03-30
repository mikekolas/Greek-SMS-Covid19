package com.example.govsmscovid19

import android.content.Context
import android.content.SharedPreferences

/**
 * Class to save light/dark mode on shared preferences
 *
 */

class SharedPref(context: Context) {

    private var sharedPref: SharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE)

    fun setNightModeState(state: Boolean?) {
        val editor = sharedPref.edit()
        editor.putBoolean("Night Mode", state!!)
        editor.apply()
    }

    fun loadNightModeState(): Boolean? {
        return sharedPref.getBoolean("Night Mode", false)
    }
}