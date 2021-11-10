package com.example.imagetranslater.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.imagetranslater.utils.AnNot.ObjNames.APP_PREFERENCES

object AppPreferences {
    private var preferences: SharedPreferences? = null

    fun Context.funAddBoolean(key: String, value: Boolean) {
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        preferences!!.edit {
            this.putBoolean(key, value).commit()
        }
    }

    fun Context.funGetBoolean(key: String, defValue: Boolean): Boolean{
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        return preferences!!.getBoolean(key, defValue)
    }
    fun Context.funAddString(key: String, value: String) {
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        preferences!!.edit {
            this.putString(key, value).commit()
        }
    }

    fun Context.funGetString(key: String, defValue: String): String {
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        return preferences!!.getString(key, defValue).toString()
    }

    fun Context.funAddStringSet(key: String, value: MutableSet<String>) {
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        preferences!!.edit { this.putStringSet(key, value).commit() }
    }

    fun Context.funGetStringSet(key: String, defSer: Set<String>): MutableSet<String> {
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        return preferences!!.getStringSet(key, defSer)!!.toMutableSet()
    }
}