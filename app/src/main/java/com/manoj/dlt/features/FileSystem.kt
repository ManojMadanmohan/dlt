package com.manoj.dlt.features

import android.content.Context
import android.content.SharedPreferences
import com.manoj.dlt.interfaces.IFileSystem

import java.util.ArrayList

class FileSystem(context: Context, key: String) : IFileSystem {

    private val _preferences: SharedPreferences
    private val _editor: SharedPreferences.Editor

    init {
        _preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        _editor = _preferences.edit()
    }

    override fun write(key: String, value: String) {
        _editor.putString(key, value)
        _editor.commit()
    }

    override fun read(key: String): String {
        return _preferences.getString(key, null)
    }

    override fun clear(key: String) {
        _editor.remove(key)
        _editor.commit()
    }

    override fun clearAll() {
        _editor.clear()
        _editor.commit()
    }

    override fun keyList(): List<String> {
        return ArrayList(_preferences.all.keys)
    }

    override fun values(): List<String> {
        val values = ArrayList<String>()
        for (key in keyList()) {
            val value = read(key)
            values.add(value)
        }
        return values
    }
}
